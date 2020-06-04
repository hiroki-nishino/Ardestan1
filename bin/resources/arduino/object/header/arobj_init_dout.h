//
//  arobj_init_dout.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 12/13/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_init_dout_h
#define arobj_init_dout_h

//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------

typedef struct {
    uint8_t pinNo;
} ARObjInitDout;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------


void init_func_init_dout (ARObject*           self        ,
                         void*               __fields__  ,
                         uint_fast8_t        argc        ,
                         ARMessageType*      types       ,
                         ARValue*            argv        );

void trigger_func_init_dout  (ARObject* self        ,
                              void*     __fields__  );
#endif /* arobj_init_dout_h */
