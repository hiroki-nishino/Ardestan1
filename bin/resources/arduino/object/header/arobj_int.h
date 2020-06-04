//
//  arobj_int.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/12/12.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_int_h
#define arobj_int_h


//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_int   (ARObject*           self        ,
                      void*               __fields__  ,
                      uint_fast8_t        argc        ,
                      ARMessageType*      argt        ,
                      ARValue*            argv        );

void trigger_func_int   (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );
#endif /* arobj_int_h */
