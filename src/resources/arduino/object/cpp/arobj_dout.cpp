//
//  arobj_dout.c
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/12/14.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_dout.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_dout (ARObject*       self        ,
                     void*           __fields__  ,
                     uint_fast8_t    argc        ,
                     ARMessageType*  argt          ,
                     ARValue*        argv        )
{
	ARObjDout* fields = (ARObjDout*)__fields__;

    ARValue v;
    v.i = -1;

    if (argc > 0 && argt[0] == ARMessageType::INT &&
        0 <= argv[0].i && argv[0].i <= 127){
        v.i = (uint8_t)argv[0].i;
        pinMode(v.i, OUTPUT);  
	}

    fields->pin_no = v.i;

    return;
}


void trigger_func_dout  (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  )
{
	ARObjDout* fields = (ARObjDout*)__fields__;

    ARSymID s;
    int32_t v0 = 0;
    
    //if the message type at inlet #1 is not integer,
    //we simply return.
    ARMessageType t1 = self->getInputType(1);
    if (t1 != ARMessageType::INT && t1 != ARMessageType::FLOAT){
        return;
    }
    
    //get the pin no.
    int32_t pin_no = fields->pin_no;
    if (pin_no < 0 || 127 < pin_no){
        return;
    }
    

    //set pin high/low (or init the pin, if init is requested).
    ARMessageType t0 = self->getInputType(0);
    switch (t0)
    {
        case ARMessageType::FLOAT:
        case ARMessageType::INT:
            v0 = self->getInputInt(0);
            digitalWrite(pin_no, v0 == 0 ? 0 : 1);
            break;
            
        case ARMessageType::SYM_ID:
            s = self->getInputSymbol(0);
            if (s == ID_HIGH){
                digitalWrite(pin_no, 1);
            }
            else if (s == ID_LOW){
                digitalWrite(pin_no, 0);
            }
            break;

        default:
            break;
    }
    
    return;
}
