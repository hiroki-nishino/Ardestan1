//
//  arobj_dbg_print.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/11/25.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>
#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_dbgprint.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_dbgprint(ARObject*       self        ,
                        void*           __fields__  ,
                        uint_fast8_t    argc        ,
                        ARMessageType*  argt        ,
                        ARValue*        argv        )
{
    ARObjDbgprint* fields = (ARObjDbgprint*)__fields__;
    
    if (argc == 0 || argt[0] != ARMessageType::STRING){
        fields->name = NULL;
        return;
    }
    
    fields->name = argv[0].str;
    
    return;
}

void trigger_func_dbgprint(ARObject*    self        ,
                           int32_t      inlet_no    ,
                           void*        __fields__  )
{
    ARObjDbgprint* fields = (ARObjDbgprint*)__fields__;

    ARMessageType t = self->getInputType(0);
    switch(t){
        case ARMessageType::INT:
            printf("%s: %d\n", fields->name, self->getInputInt(0));
            break;
            
        case ARMessageType::FLOAT:
            printf("%s: %f\n", fields->name, self->getInputFloat(0));
            break;
            
        case ARMessageType::SYM_ID:
            printf("%s: symbol (%i)\n", fields->name, self->getInputSymbol(0));
            break;
            
        case ARMessageType::STRING:
            printf("%s: %s\n", fields->name, self->getInputString(0));
            break;
    }
    
    return;
}
