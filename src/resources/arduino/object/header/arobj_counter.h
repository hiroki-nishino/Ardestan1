//
//  arobj_counter.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 11/6/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_counter_h
#define arobj_counter_h

//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
typedef enum {
    NORMAL = 0,
    REPEAT = 1,
    CYCLE  = 2,
    SUSPEND = 3
} ARObjCounterMode;

typedef struct {
    int32_t current;
    int32_t min;
    int32_t max;
    int32_t step:30;
    ARObjCounterMode mode:2;
} ARObjCounter;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_counter   (ARObject*         self        ,
                          void*             __fields__  ,
                          uint_fast8_t      argc        ,
                          ARMessageType*    argt        ,
                          ARValue*          argv        );

void trigger_func_counter(ARObject* self        ,
                          int32_t   inlet_no    ,
                          void*     __fields__  );
#endif /* arobj_counter_h */
