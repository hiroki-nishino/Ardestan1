//
//  arobj_bang.cpp
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/25/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>
#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_bang.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------


void init_func_bang (ARObject*       self        ,
                     void*           __fields__  ,
                     uint_fast8_t    argc        ,
                     ARMessageType*  types       ,
                     ARValue*        argt        )
{
    return;
}

void trigger_func_bang  (ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
    self->outputSymbol(0, ID_BANG);
    return;
}
