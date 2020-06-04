//
//  arobj_bop.h
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/15.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_bop_h
#define arobj_bop_h


//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------

#define BINOP_ID_EQ   (0)
#define BINOP_ID_NEQ  (1)
#define BINOP_ID_LT   (2)
#define BINOP_ID_LTEQ (3)
#define BINOP_ID_GT   (4)
#define BINOP_ID_GTEQ (5)
#define BINOP_ID_LAND (6)
#define BINOP_ID_LOR  (7)
#define BINOP_ID_ADD  (8)
#define BINOP_ID_SUB  (9)
#define BINOP_ID_MUL  (10)
#define BINOP_ID_DIV  (11)
#define BINOP_ID_MOD  (12)

typedef struct {
    int8_t bop;
} ARObjBop;



//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------


void init_func_bop  (ARObject*         self        ,
                     void*             __fields__  ,
                     uint_fast8_t      argc        ,
                     ARMessageType*    argt        ,
                     ARValue*          argv        );

void trigger_func_bop(ARObject* self        ,
                      int32_t   inlet_no    ,
                      void*     __fields__  );

#endif /* arobj_bop_h */
