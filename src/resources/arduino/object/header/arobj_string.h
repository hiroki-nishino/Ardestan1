//
//  arobj_string.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/19/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_string_h
#define arobj_string_h


//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_string(ARObject*           self        ,
                      void*               __fields__  ,
                      uint_fast8_t        argc        ,
                      ARMessageType*      argt        ,
                      ARValue*            argv        );

void trigger_func_string (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );

#endif /* arobj_string_h */
