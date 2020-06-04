//
//  arobj_loadbang.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/11/25.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>
#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_loadbang.h"


//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_loadbang(ARObject*       self        ,
                        void*           __fields__  ,
                        uint_fast8_t    argc        ,
                        ARMessageType*  argt        ,
                        ARValue*        argv        )
{
    return;
}

void trigger_func_loadbang(ARObject*    self        ,
                           int32_t      inlet_no    ,
                           void*        __fields__  )
{
    self->outputSymbol(0, ID_BANG);
    return;
}
