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
    ARValue v;
    v.i = 13;
    if (argc > 0){
        
        if (argt[0] == ARMessageType::INT){
            if (0 <= argv[0].i && argv[0].i <= 255){
                v.i = (uint8_t)argv[0].i;
            }
        }
    }
    self->setInletValue(1, ARMessageType::INT, v);
    return;
}


void trigger_func_aout  (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  )
{
    int32_t v0 = 0;
    
    //if the message type at inlet #1 is not integer,
    //we simply return.
    ARMessageType t1 = self->getInputType(1);
    if (t1 != ARMessageType::INT){
        return;
    }
    
    //get the pin no.
    int32_t v1 = self->getInputInt(1);
    
    if (v1 < 0 || 255 < v1){
        return;
    }
    
    uint8_t pinNo = (uint8_t)v1;
    
    //set pin high/low.
    ARMessageType t0 = self->getInputType(0);
    if (t0 != ARMessageType::INT && t0 != ARMessageType::FLOAT){
        return;
    }
    v0 = self->getInputInt(0);

    if (0 <= v0 && v0 <= 255){
        analogWrite(pinNo, v0);
    }
    
    return;
}
