//
//  arobj_trigger.cpp
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/5/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>


#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_trigger.h"



//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------


void init_func_trigger (ARObject*       self        ,
                        void*           __fields__  ,
                        uint_fast8_t    argc        ,
                        ARMessageType*  argt        ,
                        ARValue*        argv        )
{
    ARObjTrigger* fields = (ARObjTrigger*)__fields__;

    fields->argc            = 0;
    fields->arg_types       = NULL;
    fields->const_values    = NULL;

    //no need to init if there is no arg.
    if (argc <= 0){
        return;
    }
    
    uint32_t const_value_count = 0;
    for (uint32_t i = 0; i < argc; i++){
        if (argt[i] != ARMessageType::SYM_ID){
            const_value_count++;
            continue;
        }
        switch(argv[i].sym_id){
            case ID_A:
            case ID_I:
            case ID_F:
            case ID_B:
            case ID_S:
            case ID_ANYTHING:
            case ID_INT:
            case ID_FLOAT:
            case ID_BANG:
            case ID_SYMBOL:
            case ID_STRING:
                break;
                
            default:
                const_value_count++;
                break;
        }
    }
    


    uint32_t bytes = argc + sizeof(ARObjTriggerConstValue) * const_value_count;
    fields->argc = argc;
    fields->arg_types   = (int8_t*)malloc(bytes);
    if (fields->arg_types == NULL){
    	panic("init_func_trigger: not enough memory");
    }
    fields->const_values = (ARObjTriggerConstValue*)&(fields->arg_types[argc]);
    
    uint32_t const_index = 0;
    for (uint32_t i = 0; i < argc; i++){

    	if (argt[i] != ARMessageType::SYM_ID){
            fields->arg_types[i] = const_index;
            fields->const_values[const_index].t = argt[i];
            fields->const_values[const_index].v = argv[i];
            const_index++;
            continue;
    	}

        switch(argv[i].sym_id){
            case ID_A:
            case ID_ANYTHING:
                fields->arg_types[i] = ARObjTriggerArgType::ANYTHING;
                break;
            case ID_B:
            case ID_BANG:
                fields->arg_types[i] = ARObjTriggerArgType::BANG;
                break;

            case ID_I:
            case ID_INT:
                fields->arg_types[i] = ARObjTriggerArgType::INT;
                break;
                
            case ID_F:
            case ID_FLOAT:
                fields->arg_types[i] = ARObjTriggerArgType::FLOAT;
                break;
                
            case ID_S:
            case ID_SYMBOL:
                fields->arg_types[i] = ARObjTriggerArgType::SYMBOL;
                break;
                
            case ID_STRING:
                fields->arg_types[i] = ARObjTriggerArgType::STRING;
                break;
                
            default:
                fields->arg_types[i] = const_index;
                fields->const_values[const_index].t = argt[i];
                fields->const_values[const_index].v = argv[i];
                const_index++;
                break;
        }
    }
    return;
}

void trigger_func_trigger(ARObject* self        ,
                          int32_t   inlet_no    ,
                          void*     __fields__  )
{
    ARObjTrigger* fields = (ARObjTrigger*)__fields__;

    ARSymID s;
   
  	ARMessageType t = self->getInputType(0);
    switch(t){    		
        case ARMessageType::INT:
            trigger_func_trigger_int(self, fields, self->getInputInt(0));
            break;
        case ARMessageType::FLOAT:
            trigger_func_trigger_float(self, fields, self->getInputFloat(0));
            break;
            
        case ARMessageType::SYM_ID:
            s = self->getInputSymbol(0);
            if (s == ID_BANG){
                 trigger_func_trigger_bang(self, fields);
            }
            else {
                trigger_func_trigger_symbol(self, fields, s);
            }
            break;
        case ARMessageType::STRING:
            trigger_func_trigger_string(self, fields, self->getInputString(0));
            break;
    }
    
    
    return;
}

void trigger_func_trigger_bang  (ARObject*      self    ,
                                 ARObjTrigger*  fields  )
{
    for (int32_t i = fields->argc - 1; i >= 0; i--){
        int8_t t = fields->arg_types[i] ;
        
        if (t >= 0){
            self->output(i, fields->const_values[t].t, fields->const_values[t].v);
            continue;
        }
        
        switch(t){
                
            case ARObjTriggerArgType::ANYTHING:
            case ARObjTriggerArgType::BANG:
            case ARObjTriggerArgType::SYMBOL:
                self->outputSymbol(i, ID_BANG);
                break;
                
            case ARObjTriggerArgType::INT:
                self->outputInt(i, 0);
                break;
                
            case ARObjTriggerArgType::FLOAT:
                self->outputFloat(i, 0.0f);
                break;
                
            case ARObjTriggerArgType::STRING:
                break;
        }
    }

}
void trigger_func_trigger_int   (ARObject*      self    ,
                                 ARObjTrigger*  fields  ,
                                 int32_t        value   )
{
    for (int32_t i = fields->argc - 1; i >= 0; i--){
        int8_t t = fields->arg_types[i];
        
        if (t >= 0){
            self->output(i, fields->const_values[t].t, fields->const_values[t].v);
            continue;
        }
        
        switch(t){
            case ARObjTriggerArgType::BANG:
                self->outputSymbol(i, ID_BANG);
                break;
                
            case ARObjTriggerArgType::ANYTHING:
            case ARObjTriggerArgType::INT:
                self->outputInt(i, value);
                break;
                
            case ARObjTriggerArgType::FLOAT:
                self->outputFloat(i, value);
                break;

            case ARObjTriggerArgType::SYMBOL:
            case ARObjTriggerArgType::STRING:
                break;
        }
    }
}
void trigger_func_trigger_float (ARObject*      self    ,
                                 ARObjTrigger*  fields  ,
                                 float          value   )
{
    for (int32_t i = fields->argc - 1; i >= 0; i--){
        int8_t t = fields->arg_types[i] ;
        
        if (t >= 0){
            self->output(i, fields->const_values[t].t, fields->const_values[t].v);
            continue;
        }
        
        switch(t){
                
            case ARObjTriggerArgType::BANG:
                self->outputSymbol(i, ID_BANG);
                break;
                
            case ARObjTriggerArgType::INT:
                self->outputInt(i, (int32_t)value);
                break;
                
            case ARObjTriggerArgType::ANYTHING:
            case ARObjTriggerArgType::FLOAT:
                self->outputFloat(i, value);
                break;
                
            case ARObjTriggerArgType::SYMBOL:
            case ARObjTriggerArgType::STRING:
                break;
        }
    }
}
void trigger_func_trigger_symbol(ARObject*      self    ,
                                 ARObjTrigger*  fields  ,
                                 ARSymID        value   )
{
    for (int32_t i = fields->argc - 1; i >= 0; i--){
        int8_t t = fields->arg_types[i] ;
        
        if (t >= 0){
            self->output(i, fields->const_values[t].t, fields->const_values[t].v);
            continue;
        }
        
        switch(t){
                
            case ARObjTriggerArgType::BANG:
                self->outputSymbol(i, ID_BANG);
                break;

            case ARObjTriggerArgType::ANYTHING:
            case ARObjTriggerArgType::SYMBOL:
                self->outputSymbol(i, value);
                break;

            case ARObjTriggerArgType::INT:
                self->outputInt(i, 0);
                break;
                
            case ARObjTriggerArgType::FLOAT:
                self->outputFloat(i, 0.0f);
                break;
                
            case ARObjTriggerArgType::STRING:
                break;
        }
    }

}
void trigger_func_trigger_string(ARObject*      self    ,
                                 ARObjTrigger*  fields  ,
                                 ARStr*    		value   )
{
    for (int32_t i = fields->argc - 1; i >= 0; i--){
        int8_t t = fields->arg_types[i] ;
        
        if (t >= 0){
            self->output(i, fields->const_values[t].t, fields->const_values[t].v);
            continue;
        }
        
        switch(t){

            case ARObjTriggerArgType::BANG:
                self->outputSymbol(i, ID_BANG);
                break;
                
            case ARObjTriggerArgType::SYMBOL:
            case ARObjTriggerArgType::INT:
            case ARObjTriggerArgType::FLOAT:
                break;
                
            case ARObjTriggerArgType::ANYTHING:
            case ARObjTriggerArgType::STRING:
            	self->outputString(i, value);
                break;
        }
    }

}
