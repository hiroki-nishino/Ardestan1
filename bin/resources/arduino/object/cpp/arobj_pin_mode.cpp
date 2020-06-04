//
//  arobj_pin_mode.cpp
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/14/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_pin_mode.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_pin_mode(ARObject*       self        ,
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
    
    //it must be an integer, as the Ardetan check the type.
    self->setInletValue(1, ARMessageType::INT, v);
    return;
}


void trigger_func_pin_mode (ARObject* self        ,
                            int32_t   inlet_no    ,
                            void*     __fields__  )
{
    ARSymID s;
    
    //if the message type at inlet #1 is not integer,
    //we simply return.
    ARMessageType t1 = self->getInputType(1);
    if (t1 != ARMessageType::INT && t1 != ARMessageType::FLOAT){
        return;
    }
    
    //get the pin no.
    int32_t v1 = self->getInputInt(1);
    
    if (v1 < 0 || 255 < v1){
        return;
    }
    
    uint8_t pinNo = (uint8_t)v1;
    
    //we only accept the symbol id for the inlet #0.
    ARMessageType t0 = self->getInputType(0);
    if (t0 != ARMessageType::SYM_ID){
        return;
    }

    s = self->getInputSymbol(0);
    if (s == ID_INPUT){
        pinMode(pinNo, INPUT);
    }
    else if (s == ID_INPUT_PULLUP){
        pinMode(pinNo, INPUT_PULLUP);
    }
    else if (s == ID_OUTPUT){
        pinMode(pinNo, OUTPUT);
    }
    
    return;
}
