//
//  arobj_int.c
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/12/12.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_int.h"
//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_int  (ARObject*       self        ,
                     void*           __fields__  ,
                     uint_fast8_t    argc        ,
                     ARMessageType*  argt        ,
                     ARValue*        argv        )
{
    ARValue v;
    v.i = 0;
    if (argc >= 1){
        if (argt[0] == ARMessageType::INT){
            v.i = argv[0].i;
        }
        else if (argt[0] == ARMessageType::FLOAT){
            v.i = (int32_t)(argv[0].f);
        }
    }
    self->setInletValue(1, ARMessageType::INT, v);
    return;
}

void trigger_func_int   (ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
    ARMessageType t = self->getInputType(0);
    
    if (t == ARMessageType::INT || t == ARMessageType::FLOAT){
        ARValue v;
        v.i = self->getInputInt(0);
        self->setInletValue(1, ARMessageType::INT, v);
        self->output(0, ARMessageType::INT, v);
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
            v1.i = self->getInputInt(1);
            self->output(0, ARMessageType::INT, v1);
        }
    }
    
    return;
}
