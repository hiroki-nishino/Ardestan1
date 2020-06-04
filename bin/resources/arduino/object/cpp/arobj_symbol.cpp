//
//  arobj_symbol.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/05.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_symbol.h"



//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_symbol   (ARObject*       self        ,
                         void*           __fields__  ,
                         uint_fast8_t    argc        ,
                         ARMessageType*  argt        ,
                         ARValue*        argv        )
{
    if (argc > 0 && argt[0] == ARMessageType::SYM_ID){
        self->setInletValue(1, ARMessageType::SYM_ID, argv[0]);
        return;
    }
    
    ARValue v;
    v.sym_id = ID_BANG;
    self->setInletValue(1, ARMessageType::SYM_ID, v);
    
    
    return;
}

void trigger_func_symbol(ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
    ARMessageType t = self->getInputType(0);
    
    if (t == ARMessageType::SYM_ID){
        ARSymID s = self->getInputSymbol(0);
        if (s == ID_BANG && self->getInputType(1) == ARMessageType::SYM_ID){
            ARSymID sym = self->getInputSymbol(1);
            self->outputSymbol(0, sym);
        }
        return;
    }
    
    return;
}
