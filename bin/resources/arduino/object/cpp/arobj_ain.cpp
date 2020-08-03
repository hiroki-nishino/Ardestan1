//
//  arobj_ain.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_ain.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_ain (ARObject*       self        ,
                    void*           __fields__  ,
                    uint_fast8_t    argc        ,
                    ARMessageType*  argt        ,
                    ARValue*        argv        )
{
	ARObjAin* fields = (ARObjAin*)__fields__;
	fields->pin_no = -127;

	if (argc < 1){
		return;
	}

	if (argt[0] == ARMessageType::INT){
		if (argv[0].i < 0 || 127 < argv[0].i){
			return;
		}
		fields->pin_no = (uint8_t)argv[0].i;
		return;
	}

    if (argt[0] != ARMessageType::SYM_ID){
    	return;
    }

    switch(argv[0].sym_id){
    case ID_A0:
    	fields->pin_no = A0;
        break;
    case ID_A1:
    	fields->pin_no = A1;
        break;
    case ID_A2:
    	fields->pin_no = A2;
        break;
    case ID_A3:
    	fields->pin_no = A3;
        break;
    case ID_A4:
    	fields->pin_no = A4;
        break;
    case ID_A5:
    	fields->pin_no = A5;
        break;
    default:
    	break;
    }

    return;
}


void trigger_func_ain  (ARObject* self        ,
                        int32_t   inlet_no    ,
                        void*     __fields__  )
{
    //check if the message at inlet #0 is a 'bang' message.
    ARMessageType t0 = self->getInputType(0);
    if (t0 != ARMessageType::SYM_ID || self->getInputSymbol(0) != ID_BANG){
        return;
    }
    

    //if the pin no is invalid, just return.
	ARObjAin* fields = (ARObjAin*)__fields__;
	if (fields->pin_no < 0){
		return;
	}

    //now, just read the analog input and send it out.
    int32_t v = analogRead(fields->pin_no);
    self->outputInt(0, v);
    
    return;
}

