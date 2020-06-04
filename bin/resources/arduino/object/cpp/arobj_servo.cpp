//
//  arobj_servo.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/17.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>
#include <Servo.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_servo.h"


//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_servo(ARObject*       self        ,
                     void*           __fields__  ,
                     uint_fast8_t    argc        ,
                     ARMessageType*  argt        ,
                     ARValue*        argv        )
{
    ARObjServo* fields = (ARObjServo*)__fields__;

    //TODO:don't know how to handle memory allocation failure in Arduino.
    fields->servo = NULL;
    fields->servo = new Servo();
    
    if (argc == 1){
        if (argt[0] == ARMessageType::INT){
            fields->servo->attach(argv[0].i);
        }
        return;
    }

    if (argc == 3   && argt[0] == ARMessageType::INT
                    && argt[1] == ARMessageType::INT
                    && argt[2] == ARMessageType::INT){
        fields->servo->attach(argv[0].i, argv[1].i, argv[2].i);
        return;
    }
    
    return;
}


void trigger_func_servo (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  )
{
    ARObjServo* fields = (ARObjServo*)__fields__;
    
    ARMessageType t0 = self->getInputType(0);
    if (t0 == ARMessageType::INT || t0 == ARMessageType::FLOAT){
        if (fields->servo == NULL){
            return;
        }
        fields->servo->write(self->getInputInt(0));
        return;
    }
    
    if (t0 == ARMessageType::SYM_ID){
        ARSymID sym = self->getInputSymbol(0);
        ARValue v;
        switch(sym)
        {
            case ID_READ:
                v.i = fields->servo->read();
                self->output(0, ARMessageType::INT, v);
                break;
                
            case ID_ATTACHED:
                v.i = fields->servo->attached() ? 1 : 0;
                self->output(0, ARMessageType::INT, v);
                break;
                
            case ID_DETACH:
                fields->servo->detach();
                break;

            default:
                break;
        }
    }
    
    return;
}
