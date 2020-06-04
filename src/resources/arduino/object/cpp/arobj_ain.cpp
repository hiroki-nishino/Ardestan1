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
    ARValue v;
    v.sym_id = ID_A0;
    if (argc > 0){
        if (argt[0] == ARMessageType::SYM_ID){
            switch(argv[0].sym_id){
                case ID_A0:
                case ID_A1:
                case ID_A2:
                case ID_A3:
                case ID_A4:
                case ID_A5:
                    v.sym_id = argv[0].sym_id;
                    break;
                default:
                    break;
            }
        }
    }
    self->setInletValue(1, ARMessageType::SYM_ID, v);
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
    
    //if the message type at inlet #1 is not a symbol ID,
    //we simply return.
    ARMessageType t1 = self->getInputType(1);
    if (t1 != ARMessageType::SYM_ID){
        return;
    }
    
    //get the pin no.
    ARSymID v1 = self->getInputSymbol(1);
    
    uint8_t pin_no;
    switch(v1){
        case ID_A0:
            pin_no = A0;
            break;
        case ID_A1:
            pin_no = A1;
            break;
        case ID_A2:
            pin_no = A2;
            break;
        case ID_A3:
            pin_no = A3;
            break;

        case ID_A4:
            pin_no = A4;
            break;

        case ID_A5:
            pin_no = A5;
            break;
            
        default:
            return;
    }
    
    //whatever the message was, we just read and send it out.
    int32_t v = analogRead(pin_no);
    self->outputInt(0, v);
    
    return;
}

