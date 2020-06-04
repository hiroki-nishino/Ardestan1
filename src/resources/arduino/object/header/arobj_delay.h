//
//  arobj_delay.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/12/08.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_delay_h
#define arobj_delay_h


//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
typedef struct ARDelayedEvent
{
    uint32_t        output_time ;
    ARMessageType   type        ;
    ARValue         value       ;
    struct ARDelayedEvent* next ;
} ARDelayedEvent;


typedef struct {
    ARDelayedEvent* events      ;
    int32_t         delay_time  ;
} ARObjDelay;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_delay (ARObject*           self        ,
                      void*               __fields__  ,
                      uint_fast8_t        argc        ,
                      ARMessageType*      argt        ,
                      ARValue*            argv        );

void trigger_func_delay (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );



#endif /* delay_h */
