//
//  arobj_metro.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 11/6/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_metro_h
#define arobj_metro_h


//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_metro   (ARObject*           self        ,
                        void*               __fields__  ,
                        uint_fast8_t        argc        ,
                        ARMessageType*      argt        ,
                        ARValue*            argv        );

void trigger_func_metro(ARObject* self        ,
                        int32_t   inlet_no    ,
                        void*     __fields__  );

#endif /* arobj_metro_h */
