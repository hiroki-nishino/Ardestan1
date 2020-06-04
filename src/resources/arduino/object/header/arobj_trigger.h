//
//  arobj_trigger.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/5/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_trigger_h
#define arobj_trigger_h


//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------

namespace ARObjTriggerArgType {
    const int8_t ANYTHING  = -1;
    const int8_t BANG      = -2;
    const int8_t INT       = -3;
    const int8_t FLOAT     = -4;
    const int8_t SYMBOL    = -5;
    const int8_t STRING    = -6;
} ;

typedef struct {
    ARMessageType   t;
    ARValue         v;
} ARObjTriggerConstValue;

typedef struct {
    uint8_t     argc;
    int8_t*     arg_types;
    ARObjTriggerConstValue*    const_values;
} ARObjTrigger;



//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_trigger (ARObject*           self        ,
                        void*               __fields__  ,
                        uint_fast8_t        argc        ,
                        ARMessageType*      argt        ,
                        ARValue*            argv        );

void trigger_func_trigger(ARObject* self        ,
                          int32_t   inlet_no    ,
                          void*     __fields__  );

void trigger_func_trigger_bang  (ARObject*      self    ,
                                 ARObjTrigger*  fields  );

void trigger_func_trigger_int   (ARObject*      self    ,
                                 ARObjTrigger*  fields  ,
                                 int32_t        value   );

void trigger_func_trigger_float (ARObject*      self    ,
                                 ARObjTrigger*  fields  ,
                                 float          value   );

void trigger_func_trigger_symbol(ARObject*      self    ,
                                 ARObjTrigger*  fields  ,
                                 ARSymID        value   );

void trigger_func_trigger_string(ARObject*      self    ,
                                 ARObjTrigger*  fields  ,
                                 ARStr*    		value   );

#endif /* arobj_trigger_h */
