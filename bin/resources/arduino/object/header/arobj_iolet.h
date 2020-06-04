//
//  arobj_iolet.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/02/27.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_iolet_h
#define arobj_iolet_h

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_iolet(ARObject*       self        ,
                     void*           __fields__  ,
                     uint_fast8_t    argc        ,
                     ARMessageType*  argt        ,
                     ARValue*        argv        );

void trigger_func_iolet (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );

#endif /* arobj_iolet_h */
