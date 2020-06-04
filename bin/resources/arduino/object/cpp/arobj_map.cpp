//
//  map.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/24.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//


#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_map.h"


//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_map  (ARObject*       self        ,
                     void*           __fields__  ,
                     uint_fast8_t    argc        ,
                     ARMessageType*  argt        ,
                     ARValue*        argv        )
{
    ARObjMap* fields = (ARObjMap*)__fields__;
    
    fields->from_low    = 0;
    fields->from_high   = 0;
    fields->to_low      = 0;
    fields->to_high     = 0;
    
    if (argc != 4){
        return;
    }
    
    if (argt[0] != ARMessageType::INT || argt[1] != ARMessageType::INT ||
        argt[2] != ARMessageType::INT || argt[3] != ARMessageType::INT ){
        return;
    }
    
    fields->from_low    = argv[0].i;
    fields->from_high   = argv[1].i;
    fields->to_low      = argv[2].i;
    fields->to_high     = argv[3].i;

    return;
}

void trigger_func_map   (ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
    ARObjMap* fields = (ARObjMap*)__fields__;

    ARMessageType t = self->getInputType(0);
    if (t != ARMessageType::INT){
        return;
    }
    
    int32_t v0 = self->getInputInt(0);
    
    
    ARValue v;
    v.i = map(v0, fields->from_low  , fields->from_high ,
              fields->to_low    , fields->to_high   );
    
    self->output(0, ARMessageType::INT, v);
    
    return;
}
