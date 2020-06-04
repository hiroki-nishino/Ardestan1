
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_serial.h"



//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_serial   (ARObject*       self        ,
                         void*           __fields__  ,
                         uint_fast8_t    argc        ,
                         ARMessageType*  argt        ,
                         ARValue*        argv        )
{

    ARValue v;
    v.i = 9600;
    
    if (argc >= 1 && argt[0] == ARMessageType::INT){
        v = argv[0];
    }
    
    self->setInletValue(1, ARMessageType::INT, v);
    Serial.begin(self->getInputInt(1));
    
    return;
}



void trigger_func_serial    (ARObject*  self        ,
                             int32_t    inlet_no    ,
                             void*      __fields__  )
{
    ARMessageType t = self->getInputType(0);

    //received the message.
    if (t == ARMessageType::SYM_ID){
        ARSymID v = self->getInputSymbol(0);
        if (v == ARSymID::ID_STOP){
            Serial.end();
        }
        else if (v == ARSymID::ID_START &&
                 self->getInputType(1) == ARMessageType::INT){
            Serial.begin(self->getInputInt(1));
        }
        return;
    }
    
    
    return;
}
