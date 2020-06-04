//
//  arobj_servo.hpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2019/03/17.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef arobj_servo_hpp
#define arobj_servo_hpp

//----------------------------------------------------------------------------
// types
//----------------------------------------------------------------------------
class Servo;
typedef struct {
    Servo* servo;
} ARObjServo;

//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------

void init_func_servo (ARObject*           self        ,
                      void*               __fields__  ,
                      uint_fast8_t        argc        ,
                      ARMessageType*      argt        ,
                      ARValue*            argv        );

void trigger_func_servo (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  );

#endif /* arobj_servo_hpp */
