//
//  arobj_symbol.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/05.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_symbol_h
#define arobj_symbol_h

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_symbol(ARObject*       self        ,
                      void*           __fields__  ,
                      uint_fast8_t    argc        ,
                      ARMessageType*  argt        ,
                      ARValue*        argv        );

void trigger_func_symbol(ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );
#endif /* arobj_symbol_h */
