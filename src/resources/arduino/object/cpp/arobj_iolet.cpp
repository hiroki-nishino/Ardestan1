//
//  arobj_iolet.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/02/27.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//


#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_iolet.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_iolet(ARObject*       self        ,
                     void*           __fields__  ,
                     uint_fast8_t    argc        ,
                     ARMessageType*  argt        ,
                     ARValue*        argv        )
{
    return;
}

void trigger_func_iolet (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  )
{
    ARMessageType   t = self->getInputType(0);
    ARValue         v = self->getInput(0);
    
    self->output(0, t, v);
    return;
}
