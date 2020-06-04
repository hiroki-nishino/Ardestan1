//
//  arobj_serial.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/13/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_serial_h
#define arobj_serial_h


//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------


void init_func_serial   (ARObject*         self        ,
                         void*             __fields__  ,
                         uint_fast8_t      argc        ,
                         ARMessageType*    argt        ,
                         ARValue*          argv        );

void trigger_func_serial    (ARObject* self        ,
                             int32_t   inlet_no    ,
                             void*     __fields__  );
#endif /* arobj_serial_h */
