//
//  arobj_din.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//


#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_din.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_din (ARObject*       self        ,
                    void*           __fields__  ,
                    uint_fast8_t    argc        ,
                    ARMessageType*  argt        ,
                    ARValue*        argv        )
{
    ARValue v;
    v.i = -1;


    ARObjDin* fields = (ARObjDin*)__fields__;
    fields->pin_no = - 1;

    if (argc > 0){
        if (argt[0] == ARMessageType::INT){
            if (0 <= argv[0].i && argv[0].i <= 127){
                v.i = (uint8_t)argv[0].i;
                pinMode(v.i, INPUT_PULLUP);
                fields->pin_no = (uint8_t)v.i;
            }
        }
    }
    return;
}


void trigger_func_din  (ARObject* self        ,
                        int32_t   inlet_no    ,
                        void*     __fields__  )
{    
    ARObjDin* fields = (ARObjDin*)__fields__;

    //if the pin no is not valid, we simply return.
    if (fields->pin_no < 0 || 127 < fields->pin_no){
        return;
    }
    
    
    //whatever the message was, we just read the digital pin and send it out.
    int32_t v = digitalRead(fields->pin_no);
    self->outputInt(0, v == 0 ? 0 : 1);
    
    return;
}

