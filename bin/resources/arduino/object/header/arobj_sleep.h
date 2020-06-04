//
//  arobj_sleep.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/12/06.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_sleep_h
#define arobj_sleep_h

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_sleep   (void*               __fields__  ,
                        uint_fast8_t        argc        ,
                        ARMessageType*      types       ,
                        ARValue*            argv        ,
                        ARObject*           object      );

void trigger_func_polling (void*     __patch__   ,
                           ARObject* self        ,
                           void*     __fields__  );

#endif /* arobj_sleep_h */
