//
//  arobj_metro.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/11/25.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>
#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_metro.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_metro   (ARObject*           self        ,
                        void*               __fields__  ,
                        uint_fast8_t        argc        ,
                        ARMessageType*      argt        ,
                        ARValue*            argv        )
{	
	ARValue v;
	v.i = 1000;
	
    if (argc >= 1 && argt[0] == ARMessageType::INT && argv[0].i > 0){
		v.i = argv[0].i;
    }
    
    
    self->setInletValue(1, ARMessageType::INT, v);
    return;
}

void trigger_func_metro(ARObject* self        ,
                        int32_t   inlet_no    ,
                        void*     __fields__  )
{
    //--------------------------------------
    ARMessageType   t0 = self->getInputType(0);
    ARValue         v0 = self->getInput(0);
    
    //if the inlet #0 value is a sym id.
    if (ARMessageType::SYM_ID == t0){
        
        //is this metro active? is it a timer callback event?
        if (ID_TIMER_CALLBACK == v0.sym_id){
            
            //send the bang message out!
            goto activate_metro;
        }
        
        //is it a bang/start or stop message?
        switch(v0.sym_id){
            case ID_BANG:
            case ID_START:
                goto activate_metro;
                
            case ID_STOP:
                goto deactivate_metro;
                
            default:
                break;
        }
        return;
    }
    
    //handle the integer input 1/0
    if (t0 == ARMessageType::INT || t0 == ARMessageType::FLOAT){
        int32_t v = self->getInputInt(0);
        if (v != 0){
            goto activate_metro;
        }
        else {
            goto deactivate_metro;
        }
    }
    
    return;
    
    //--------------------------------------
deactivate_metro:
    
    self->stopScheduledTimerCallback();
    
    return;

    //--------------------------------------
activate_metro:
    
    //can we schedule the next? if the period is not valid,
    //just stop this metro.
    ARMessageType   t1 = self->getInputType(1);
    int32_t         v1 = self->getInputInt(1);
    if (!(t1 == ARMessageType::INT || t1 == ARMessageType::FLOAT) || v1 <= 0){
        goto deactivate_metro;
    }
    
    
    self->outputSymbol(0, ID_BANG);
    
    //schedule the next event.    
    self->scheduleTimerCallback(self->getInputInt(1),
                                ARTimerCallbackScheduleMode::OVERWRITE);
    return;

}
