//
//  arobj_dout.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/12/14.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_dout_h
#define arobj_dout_h

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_dout (ARObject*           self        ,
                     void*               __fields__  ,
                     uint_fast8_t        argc        ,
                     ARMessageType*      argt        ,
                     ARValue*            argv        );

void trigger_func_dout  (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );
#endif /* arobj_dout_h */
