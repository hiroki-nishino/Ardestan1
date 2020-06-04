//
//  arobj_poll.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 12/5/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_poll_h
#define arobj_poll_h


//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_poll(ARObject*           self        ,
					void*               __fields__  ,
                  	uint_fast8_t        argc        ,
                   	ARMessageType*      argt        ,
                    ARValue*            argv        );

void trigger_func_poll (ARObject* self        ,
                       	int32_t   inlet_no    ,
                        void*     __fields__  );


#endif /* arobj_poll_h */
