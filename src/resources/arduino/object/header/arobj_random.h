//
//  arobj_random.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 12/13/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_random_h
#define arobj_random_h


//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
typedef struct {
    int32_t 	seed;
} ARObjRandom;
//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_random   (ARObject*           self        ,
                         void*               __fields__  ,
                         uint_fast8_t        argc        ,
                         ARMessageType*      argt        ,
                         ARValue*            argv        );

void trigger_func_random  (ARObject* self        ,
                           int32_t   inlet_no    ,
                           void*     __fields__  );

#endif /* arobj_random_h */
