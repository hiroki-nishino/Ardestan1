//
//  arobj_bop.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <math.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_bop.h"



//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_bop   (ARObject*       self        ,
                      void*           __fields__  ,
                      uint_fast8_t    argc        ,
                      ARMessageType*  argt        ,
                      ARValue*        argv        )
{
    ARObjBop* fields = (ARObjBop*)__fields__;

    if (argc == 0){
        return;
    }
    
    fields->bop = ID_EQ;
    
    if (argt[0] != ARMessageType::INT){
        return;
    }


    switch(argv[0].i){
        case BINOP_ID_EQ:
        case BINOP_ID_NEQ:
        case BINOP_ID_LT:
        case BINOP_ID_LTEQ:
        case BINOP_ID_GT:
        case BINOP_ID_GTEQ:
        case BINOP_ID_LAND:
        case BINOP_ID_LOR:
        case BINOP_ID_ADD:
        case BINOP_ID_SUB:
        case BINOP_ID_MUL:
        case BINOP_ID_DIV:
        case BINOP_ID_MOD:
            fields->bop = (int8_t)(argv[0].i);
            break;
            
        default:
            return;
    }
    

    
    if (argc < 2 || (argt[1] != ARMessageType::INT && argt[1] != ARMessageType::FLOAT)){
        ARValue v;
        v.i = 0;
        self->setInletValue(1, ARMessageType::INT, v);
        return;
    }
    
    self->setInletValue(1, argt[1], argv[1]);
    
    return;
}

void trigger_func_bop_int_int(ARObject*    self          ,
                              void*        __fields__    ,
                              int32_t      lhs           ,
                              int32_t      rhs           )
{
    ARObjBop* fields = (ARObjBop*)__fields__;
    
    
    ARValue v;
    switch(fields->bop){
        case BINOP_ID_EQ:
            v.i = (lhs == rhs ? 1 : 0);
            break;
        case BINOP_ID_NEQ:
            v.i = (lhs != rhs ? 1 : 0);
            break;
        case BINOP_ID_LT:
            v.i = (lhs < rhs ? 1 : 0);
            break;
        case BINOP_ID_LTEQ:
            v.i = (lhs <= rhs ? 1 : 0);
            break;
        case BINOP_ID_GT:
            v.i = (lhs > rhs ? 1 : 0);
            break;
        case BINOP_ID_GTEQ:
            v.i = (lhs >= rhs ? 1 : 0);
            break;
        case BINOP_ID_LAND:
            v.i = (lhs && rhs ? 1 : 0);
            break;
        case BINOP_ID_LOR:
            v.i = (lhs || rhs ? 1 : 0);
            break;
            
        case BINOP_ID_ADD:
            v.i = lhs + rhs;
            break;
        case BINOP_ID_SUB:
            v.i = lhs - rhs;
            break;
        case BINOP_ID_MUL:
            v.i = lhs * rhs;
            break;
            
        case BINOP_ID_DIV:
            if (rhs == 0){
                return;
            }
            v.i =  lhs / rhs;
            break;

        case BINOP_ID_MOD:
            if (rhs == 0){
                return;
            }
            v.i =  lhs % rhs;
            break;
            
        default:
            return;
    }

    self->output(0, ARMessageType::INT, v);

    return;
}

void trigger_func_bop_float_float(ARObject*    self          ,
                                  void*        __fields__    ,
                                  float        lhs           ,
                                  float        rhs           )
{
    ARObjBop* fields = (ARObjBop*)__fields__;
    

    ARValue v;
    switch(fields->bop){
        case BINOP_ID_EQ:
            v.i = (lhs == rhs ? 1 : 0);
            self->output(0, ARMessageType::INT, v);
            return;
        case BINOP_ID_NEQ:
            v.i = (lhs != rhs ? 1 : 0);
            self->output(0, ARMessageType::INT, v);
            return;
        case BINOP_ID_LT:
            v.i = (lhs < rhs ? 1 : 0);
            self->output(0, ARMessageType::INT, v);
            return;
        case BINOP_ID_LTEQ:
            v.i = (lhs <= rhs ? 1 : 0);
            self->output(0, ARMessageType::INT, v);
            return;
        case BINOP_ID_GT:
            v.i = (lhs > rhs ? 1 : 0);
            self->output(0, ARMessageType::INT, v);
            return;
        case BINOP_ID_GTEQ:
            v.i = (lhs >= rhs ? 1 : 0);
            self->output(0, ARMessageType::INT, v);
            return;
        case BINOP_ID_LAND:
            v.i = (lhs && rhs ? 1 : 0);
            self->output(0, ARMessageType::INT, v);
            return;
        case BINOP_ID_LOR:
            v.i = (lhs || rhs ? 1 : 0);
            self->output(0, ARMessageType::INT, v);
            return;

        case BINOP_ID_ADD:
            v.f = lhs + rhs;
            break;
            
        case BINOP_ID_SUB:
            v.f = lhs - rhs;
            break;
        case BINOP_ID_MUL:
            v.f = lhs * rhs;
            break;
            
        case BINOP_ID_DIV:
            if (rhs == 0){
                return;
            }
            v.f =  lhs / rhs;
            break;
            
        case BINOP_ID_MOD:
            if (rhs == 0){
                return;
            }
            v.f =  fmodf(lhs, rhs);
            break;
            
        default:
        	return;
    }
    
    self->output(0, ARMessageType::FLOAT, v);
    
    return;
}

void trigger_func_bop    (ARObject*    self        ,
                          int32_t       inlet_no   ,
                          void*        __fields__  )
{
    ARMessageType   t0 = self->getInputType(0);
    ARMessageType   t1 = self->getInputType(1) ;

    if (t0 == ARMessageType::INT){
        if (t1 == ARMessageType::INT){
            trigger_func_bop_int_int(self, __fields__, self->getInputInt(0), self->getInputInt(1));
        }
        else if (t1 == ARMessageType::FLOAT){
            trigger_func_bop_float_float(self, __fields__, self->getInputInt(0), self->getInputFloat(1));
        }
    }
    else if (t0 == ARMessageType::FLOAT){
        if (t1 == ARMessageType::INT){
            trigger_func_bop_float_float(self, __fields__, self->getInputFloat(0), self->getInputInt(1));
        }
        else if (t1 == ARMessageType::FLOAT){
            trigger_func_bop_float_float(self, __fields__, self->getInputFloat(0), self->getInputFloat(1));}
    }
    
    return;
}
