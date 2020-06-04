//
//  arobj_select.cpp
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/25/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//


#include <stdio.h>
#include <stdint.h>
#include <string.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_select.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_select(ARObject*       self        ,
                      void*           __fields__  ,
                      uint_fast8_t    argc        ,
                      ARMessageType*  argt        ,
                      ARValue*        argv        )
{
    if (argc >= 1){
        self->setInletValue(1, argt[0], argv[0]);
    }
    else {
    	ARValue v;
    	v.i = 0;
        self->setInletValue(1, ARMessageType::INT, v);
    }
    return;
}

void trigger_func_select(ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
    ARMessageType   t0 = self->getInputType(0);
    ARValue         v0 = self->getInput(0);

    ARMessageType   t1 = self->getInputType(1);
    ARValue         v1 = self->getInput(1);

       
    switch(t1){
        case ARMessageType::STRING:

        	if (t0 != ARMessageType::STRING){
        		break;
        	}
            if (strcmp(v0.str->p, v1.str->p) == 0){
                self->outputSymbol(0, ID_BANG);
                return;
            }
            break;
            
        case ARMessageType::INT:

        	if ((t0 == ARMessageType::INT && v0.i == v1.i) ||
        		(t0 == ARMessageType::FLOAT && v0.f == v1.i)){
                self->outputSymbol(0, ID_BANG);
                return;
        	}
            break;

        case ARMessageType::FLOAT:
        	if ((t0 == ARMessageType::INT && v0.i == v1.f) ||
        		(t0 == ARMessageType::FLOAT && v0.f == v1.f)){
                self->outputSymbol(0, ID_BANG);
                return;
        	}
            break;

        case ARMessageType::SYM_ID:

        	if (t0 != ARMessageType::SYM_ID){
        		break;
        	}
            if (v0.sym_id == v1.sym_id){
                self->outputSymbol(0, ID_BANG);
                return;
            }
            break;

    }

    self->output(1, t0, v0);

    return;
}
