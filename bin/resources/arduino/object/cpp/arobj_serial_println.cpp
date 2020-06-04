//
//  arobj_serial_println.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_serial_println.h"


//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_serial_println(ARObject*         self        ,
                              void*             __fields__  ,
                              uint_fast8_t      argc        ,
                              ARMessageType*    argt        ,
                              ARValue*          argv        )
{
	//set the default values.
	ARValue v;
	v.i = 0;
	self->setInletValue(1, ARMessageType::INT, v);
	v.i = SERIAL_PRINT_FLOAT_DECIMAL_PLACES;
	self->setInletValue(2, ARMessageType::INT, v);

	//header
	if (argc > 0 && argt[0] == ARMessageType::STRING){
		//the argv string will be always const, which is not going to be released.
		self->setInletValue(1, ARMessageType::STRING, argv[0]);
	}

	//decimal places
	if (argc > 1 && argt[1] == ARMessageType::INT){
		self->setInletValue(2, ARMessageType::INT, argv[1]);
	}

    return;
}


void trigger_func_serial_println  (ARObject* self        ,
                                   int32_t   inlet_no    ,
                                   void*     __fields__  )
{
    ARMessageType   t = self->getInputType(0);
    ARValue         v = self->getInput(0);

    ARStr* h = NULL;
    if (self->getInputType(1) == ARMessageType::STRING){
    	h = self->getInputString(1);
    }

    int dp = SERIAL_PRINT_FLOAT_DECIMAL_PLACES;
    if (self->getInputType(2) == ARMessageType::INT){
    	int tmp = self->getInputInt(2);
    	dp = tmp < 1 ? dp : tmp;
    }
    

    if (h != NULL){
    	Serial.print(h->p);
    	Serial.print(":");
    }

    //received the message.
    switch (t) {
        case ARMessageType::INT:
            Serial.println(v.i);
            break;
        case ARMessageType::SYM_ID:
            Serial.println(v.sym_id);
            break;
            
        case ARMessageType::FLOAT:
            Serial.println(v.f, dp);
            break;
            
        case ARMessageType::STRING:
            Serial.println(v.str->p);
            break;
        default:
            break;
    }
    
    //output the input value as it is.
    self->output(0, t, v);
    
    return;
}
