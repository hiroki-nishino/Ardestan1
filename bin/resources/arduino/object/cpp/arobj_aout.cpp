//
//  arobj_aout.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_aout.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_aout(ARObject*       self        ,
                    void*           __fields__  ,
                    uint_fast8_t    argc        ,
                    ARMessageType*  argt        ,
                    ARValue*        argv        )
{
	ARObjAout* fields = (ARObjAout*)__fields__;
	fields->pin_no = -1;

	if (argc < 1 || argt[0] != ARMessageType::INT || argv[0].i < 0 || 127 < argv[0].i){
		return;
    }
	fields->pin_no = (int8_t)argv[0].i;

    return;
}


void trigger_func_aout  (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  )
{
	ARObjAout* fields = (ARObjAout*)__fields__;

    //check the pin no.
    if (fields->pin_no < 0){
        return;
    }
    
    
    //set pin high/low.
    ARMessageType t0 = self->getInputType(0);
    if (t0 != ARMessageType::INT && t0 != ARMessageType::FLOAT){
        return;
    }

    int32_t v0 = self->getInputInt(0);

    if (0 <= v0 && v0 <= 255){
        analogWrite(fields->pin_no, v0);
    }
    
    return;
}
