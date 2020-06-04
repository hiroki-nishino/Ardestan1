//
//  arobj_dbgprint.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 11/6/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_dbgprint_h
#define arobj_dbgprint_h

//----------------------------------------------------------------------------
// type
//----------------------------------------------------------------------------

typedef struct {
    const char* name;
} ARObjDbgprint;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------


void init_func_dbgprint(ARObject*       self        ,
                        void*           __fields__  ,
                        uint_fast8_t    argc        ,
                        ARMessageType*  argt        ,
                        ARValue*        argv        );

void trigger_func_dbgprint(ARObject*    self        ,
                           int32_t      inlet_no    ,
                           void*        __fields__  );


#endif /* arobj_dbg_print_h */
