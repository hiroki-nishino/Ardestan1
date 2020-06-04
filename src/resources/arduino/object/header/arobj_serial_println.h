//
//  arobj_serial_println.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_serial_println_h
#define arobj_serial_println_h

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_serial_println  (ARObject*         self        ,
                                void*             __fields__  ,
                                uint_fast8_t      argc        ,
                                ARMessageType*    argt        ,
                                ARValue*          argv        );

void trigger_func_serial_println(ARObject* self        ,
                                 int32_t   inlet_no    ,
                                 void*     __fields__  );

#endif /* arobj_serial_println_h */
