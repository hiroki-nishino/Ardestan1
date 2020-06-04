//
//  arobj_float.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/7/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_float_h
#define arobj_float_h

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_float (ARObject*           self        ,
                      void*               __fields__  ,
                      uint_fast8_t        argc        ,
                      ARMessageType*      argt        ,
                      ARValue*            argv        );

void trigger_func_float (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );

#endif /* arobj_float_h */
