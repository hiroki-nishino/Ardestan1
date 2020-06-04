//
//  arobj_spigot.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/17.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_spigot.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_spigot(ARObject*       self        ,
                      void*           __fields__  ,
                      uint_fast8_t    argc        ,
                      ARMessageType*  argt        ,
                      ARValue*        argv        )
{
    ARValue v;
    v.i = 1;
    
    if (argc >= 1){
        if (argt[0] == ARMessageType::INT){
            v.i = argv[0].i;
        }
    }
    self->setInletValue(1, ARMessageType::INT, v);
    return;
}

void trigger_func_spigot(ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
    //check if the gate is open or not.
    ARMessageType t1 = self->getInputType(1);
    if (t1 != ARMessageType::INT && t1 != ARMessageType::FLOAT){
        return;
    }
    
    if (self->getInputInt(1) == 0){
        return;
    }
    
    //the gate is open, just let the message pass through.
    ARMessageType   t0 = self->getInputType(0);
    ARValue         v0 = self->getInput(0);
    
    self->output(0, t0, v0);
    
    return;
}
