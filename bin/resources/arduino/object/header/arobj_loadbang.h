//
//  arobj_loadbang.hpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/11/25.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_loadbang_h
#define arobj_loadbang_h


//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_loadbang(ARObject*           self        ,
                        void*               __fields__  ,
                        uint_fast8_t        argc        ,
                        ARMessageType*      argt        ,
                        ARValue*            argv        );

void trigger_func_loadbang(ARObject* self        ,
                           int32_t   inlet_no    ,
                           void*     __fields__  );
#endif /* arobj_loadbang_hpp */
