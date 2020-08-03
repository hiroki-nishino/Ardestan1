//
//  arobj_aout.hpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_aout_hpp
#define arobj_aout_hpp

//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
typedef struct {
    int8_t pin_no;
} ARObjAout;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_aout (ARObject*           self        ,
                     void*               __fields__  ,
                     uint_fast8_t        argc        ,
                     ARMessageType*      argt        ,
                     ARValue*            argv        );

void trigger_func_aout  (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );
#endif /* arobj_aout_hpp */
