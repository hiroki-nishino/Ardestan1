//
//  arobj_toggle.c
//  Ardestan
//
//  Created by Hiroki NISHINO on 12/13/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//


#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_toggle.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_toggle(ARObject*       self        ,
                      void*           __fields__  ,
                      uint_fast8_t    argc        ,
                      ARMessageType*  argt        ,
                      ARValue*        argv        )
{
    ARObjToggle* fields = (ARObjToggle*)__fields__;
    
    fields->on = 0;
    if (argc > 0){
        if (argt[0] == ARMessageType::INT){
            fields->on = (argv[0].i != 0) ? 1 : 0;
        }
    }
    return;
}

void trigger_func_toggle(ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
    ARObjToggle* fields = (ARObjToggle*)__fields__;
    
    ARMessageType t = self->getInputType(0);
    if (t == ARMessageType::SYM_ID){
        ARSymID s = self->getInputSymbol(0);
        if (s == ID_BANG){
            fields->on = !(fields->on);
            self->outputInt(0, fields->on ? 1 : 0);
        }
        return;
    }
    
    if (t != ARMessageType::INT && t != ARMessageType::FLOAT){
        return;
    }
    
    int32_t v = self->getInputInt(0);
    if (v == 0){
        fields->on = 0;
    }
    else {
        fields->on = 1;
    }
    fields->on = (v == 0) ? 0 : 1;
    self->outputInt(0, fields->on);

    return;
}
