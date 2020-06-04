//
//  arobj_select.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/25/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_select_h
#define arobj_select_h



//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_select (ARObject*           self        ,
                       void*               __fields__  ,
                       uint_fast8_t        argc        ,
                       ARMessageType*      argt        ,
                       ARValue*            argv        );

void trigger_func_select(ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );


#endif /* arobj_select_h */
