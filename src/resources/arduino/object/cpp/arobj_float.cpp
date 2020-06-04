//
//  arobj_float.cpp
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/7/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_float.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_float(ARObject*       self        ,
                     void*           __fields__  ,
                     uint_fast8_t    argc        ,
                     ARMessageType*  argt        ,
                     ARValue*        argv        )
{
    ARValue v;
    v.f = 0;
    if (argc >= 1){
        if (argt[0] == ARMessageType::INT){
            v.f = argv[0].i;
        }
        else if (argt[0] == ARMessageType::FLOAT){
            v.f = argv[0].f;
        }
    }
    self->setInletValue(1, ARMessageType::FLOAT, v);
    return;
}

void trigger_func_float (ARObject*   self        ,
                         int32_t     inlet_no    ,
                         void*       __fields__  )
{
    ARMessageType t = self->getInputType(0);
    
    if (t == ARMessageType::INT || t == ARMessageType::FLOAT){
        ARValue v;
        v.f = self->getInputFloat(0);
        v.f = v.i;
        self->setInletValue(1, ARMessageType::FLOAT, v);
        self->output(0, ARMessageType::FLOAT, v);
        return;
    }
    
    
    if (t == ARMessageType::SYM_ID){
        ARSymID sym = self->getInputSymbol(0);
        if (sym != ID_BANG){
            return;
        }
        ARMessageType t1 = self->getInputType(1);
        if (t1 == ARMessageType::INT || t1 == ARMessageType::FLOAT){
            ARValue v1;
            v1.f = self->getInputFloat(1);
            self->output(0, ARMessageType::FLOAT, v1);
        }
    }
    
    return;
}


