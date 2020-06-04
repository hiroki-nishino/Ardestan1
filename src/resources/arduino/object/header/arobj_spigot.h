//
//  arobj_spigot.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/17.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_spigot_h
#define arobj_spigot_h

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_spigot(ARObject*           self        ,
                      void*               __fields__  ,
                      uint_fast8_t        argc        ,
                      ARMessageType*      argt        ,
                      ARValue*            argv        );

void trigger_func_spigot(ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );
#endif /* arobj_spigot_h */
