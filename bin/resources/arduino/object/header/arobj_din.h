//
//  arobj_din.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_din_h
#define arobj_din_h

//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
typedef struct {
    int8_t pin_no;
} ARObjDin;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_din  (ARObject*           self        ,
                     void*               __fields__  ,
                     uint_fast8_t        argc        ,
                     ARMessageType*      argt        ,
                     ARValue*            argv        );

void trigger_func_din   (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );

#endif /* arobj_din_h */
