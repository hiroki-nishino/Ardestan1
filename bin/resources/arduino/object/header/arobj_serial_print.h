//
//  serial_print.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/14/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef serial_print_h
#define serial_print_h

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_serial_print  (ARObject*         self        ,
                              void*             __fields__  ,
                              uint_fast8_t      argc        ,
                              ARMessageType*    argt        ,
                              ARValue*          argv        );

void trigger_func_serial_print    (ARObject* self        ,
                                   int32_t   inlet_no    ,
                                   void*     __fields__  );
#endif /* serial_print_hpp */
