//
//  arobj_counter.c
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/11/25.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#include <stdint.h>
#include <limits.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_counter.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------
//
//void init_func_counter   (ARObject*         self        ,
//                          void*             __fields__  ,
//                          uint_fast8_t      argc        ,
//                          ARMessageType*    argt        ,
//                          ARValue*          argv        )
//{
//    ARObjCounter* fields = (ARObjCounter*)__fields__;
//    
//    fields->current = 0;
//    int32_t min = 0;
//    int32_t max = INT_MAX;
//    int8_t  mode = 0;
//    
//    if (argc == 0){
//        goto set_up_inlets;
//    }
//    
//    if (argc >= 1 && argt[0] == ARMessageType::INT){
//        fields->min = argv[0].i;
//    }
//    if (argc >= 2 && argt[1] == ARMessageType::INT){
//        fields->max = argv[1].i;
//        fields->mode = 0;
//    }
//    if (argc >= 3 )
//
//set_up_inlets:
//        
//    return;
//}
//
//void trigger_func_counter(ARObject* self        ,
//                          void*     __fields__  )
//{
//    ARObjCounter* fields = (ARObjCounter*)__fields__;
//    
//    //output the current counter value. then increment by one.
//    self->outputInt(0, fields->cnt);
//    fields->cnt += 1;
//    
//    return;
//}
