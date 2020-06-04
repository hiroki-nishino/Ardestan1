//
//  arobj_din_change.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/25.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_din_change_h
#define arobj_din_change_h


//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
typedef struct {
    int32_t pin_no;
    uint8_t prev_value;
} ARObjDinChange;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_din_change (ARObject*           self        ,
                            void*               __fields__  ,
                            uint_fast8_t        argc        ,
                            ARMessageType*      argt        ,
                            ARValue*            argv        );

void trigger_func_din_change (ARObject* self        ,
                              int32_t   inlet_no    ,
                              void*     __fields__  );

#endif /* arobj_din_change_h*/
