//
//  arobj_toggle.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 12/13/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_toggle_h
#define arobj_toggle_h


//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
typedef struct {
    uint8_t on;
} ARObjToggle;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_toggle   (ARObject*           self        ,
                         void*               __fields__  ,
                         uint_fast8_t        argc        ,
                         ARMessageType*      argt        ,
                         ARValue*            argv        );

void trigger_func_toggle    (ARObject* self        ,
                             int32_t   inlet_no    ,
                             void*     __fields__  );

#endif /* arobj_toggle_h */
