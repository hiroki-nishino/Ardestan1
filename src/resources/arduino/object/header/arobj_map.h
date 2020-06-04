//
//  arobj_map.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/24.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_map_h
#define arobj_map_h

//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
typedef struct {
    int32_t from_low;
    int32_t from_high;
    int32_t to_low;
    int32_t to_high;
} ARObjMap;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_map     (ARObject*           self        ,
                        void*               __fields__  ,
                        uint_fast8_t        argc        ,
                        ARMessageType*      argt        ,
                        ARValue*            argv        );

void trigger_func_map   (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );
#endif /* arobj_map_h */
