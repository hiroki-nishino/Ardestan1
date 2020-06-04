//
//  arobj_pin_mode.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/14/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_pin_mode_h
#define arobj_pin_mode_h


//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_pin_mode (ARObject*           self        ,
                         void*               __fields__  ,
                         uint_fast8_t        argc        ,
                         ARMessageType*      argt        ,
                         ARValue*            argv        );

void trigger_func_pin_mode  (ARObject* self         ,
                             int32_t   inlet_no     ,
                             void*     __fields__   );

#endif /* arobj_pin_mode_h */
