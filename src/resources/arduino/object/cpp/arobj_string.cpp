//
//  arobj_string.cpp
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/19/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_string.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_string(ARObject*       self        ,
                      void*           __fields__  ,
                      uint_fast8_t    argc        ,
                      ARMessageType*  argt        ,
                      ARValue*        argv        )
{
	if (argc > 0 && argt[0] == ARMessageType::STRING){
		//the argv string will be always const, which is not going to be released.
		self->setInletValue(1, ARMessageType::STRING, argv[0]);
	}
	else {
		//temporarily set the inlet #1 value to 0.
		ARValue v;    	
		v.i = 0;
	    self->setInletValue(1, ARMessageType::INT, v);
	}

    return;
}

void trigger_func_string(ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
	ARMessageType 	t = self->getInputType(0);
	ARValue 		v = self->getInput(0);
	
	//if a string value is received at the inlet #0, 
	//keep it as the input at the inlet #1.
	if (t == ARMessageType::STRING){
		self->setInletValue(1, ARMessageType::STRING, v);		
	    self->output(0, ARMessageType::STRING, v);
	    return;
	}
		
    //if it is not a symbol: bang, just return.
    if (t != ARMessageType::SYM_ID || v.sym_id != ID_BANG){
    	return;
    }

	//if the value kept at the inlet #1 is not a string, do nothing. 
	ARMessageType t1 = self->getInputType(1);
	if (t1 != ARMessageType::STRING){
		return;
	}
	
	ARValue v1 = self->getInput(1);
    self->output(0, ARMessageType::STRING, v1);

    return;
}
