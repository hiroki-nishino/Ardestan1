//
//  arobj_din_change.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/25.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//



#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_din_change.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_din_change (ARObject*       self        ,
                            void*           __fields__  ,
                            uint_fast8_t    argc        ,
                            ARMessageType*  argt        ,
                            ARValue*        argv        )
{
    ARObjDinChange* fields = (ARObjDinChange*)__fields__;
    
    ARValue v;
    v.i = -1;
    if (argc > 0){
        
        if (argt[0] == ARMessageType::INT){
            if (0 <= argv[0].i && argv[0].i <= 255){
                v.i = (uint8_t)argv[0].i;
                pinMode(v.i, INPUT_PULLUP);
            }
        }
    }
    
    fields->pin_no = v.i;

    return;
}


void trigger_func_din_change  (ARObject* self        ,
                               int32_t   inlet_no    ,
                               void*     __fields__  )
{
    ARObjDinChange* fields = (ARObjDinChange*)__fields__;

    if (fields->pin_no < 0){
        return;
    }
    
    int32_t v = digitalRead(fields->pin_no);
    if (v > 0 && fields->prev_value == 0){
        fields->prev_value = 1;
        self->outputInt(0, 1);
    }
    else if (v == 0 && fields->prev_value == 1){
        fields->prev_value = 0;
        self->outputInt(0, 0);
    }
    
    return;
}

