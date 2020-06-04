//
//  arobj_delay.cpp
//  Ardestan
//
//  Created by Hiroki Nishino on 2018/12/08.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//


#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_delay.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_delay (ARObject*           self        ,
                      void*               __fields__  ,
                      uint_fast8_t        argc        ,
                      ARMessageType*      argt        ,
                      ARValue*            argv        )
{
    ARObjDelay* fields = (ARObjDelay*)__fields__;
    
    fields->events = NULL;
    fields->delay_time = 0;
    //the initial delay time.
    if (argt[0] == ARMessageType::INT && 0 <= argv[0].i){
        self->setInletValue(1, ARMessageType::INT, argv[0]);
        fields->delay_time = argv[0].i;
    }
    
    return;
}

void trigger_func_delay (ARObject* self        ,
                         int32_t   inlet_no    ,
                         void*     __fields__  )
{
    ARObjDelay* fields = (ARObjDelay*)__fields__;

    ARMessageType   t0 = self->getInputType(0);
    ARValue         v0 = self->getInput(0);
    
    ARMessageType   t1 = self->getInputType(1);
    ARValue         v1 = self->getInput(1);

    
    //if the inlet #01 received a 'reset' message,
    //delete all the events in the delay queue.
    if (ARMessageType::SYM_ID == t1 && v1.sym_id == ID_RESET){
        while(fields->events != NULL){
            ARDelayedEvent* tmp = fields->events;
            fields->events = tmp->next;
            free(tmp);
        }
        //restore the delay time;
        v1.i = fields->delay_time;
        self->setInletValue(1, ARMessageType::INT, v1);
        
        //stop the timer callback.
        self->stopScheduledTimerCallback();
        return;
    }

    //if it is a polling message, then process the delay queue.
    if (ARMessageType::SYM_ID == t0 && v0.sym_id == ID_TIMER_CALLBACK){
        uint32_t t = now();
        while(fields->events != NULL & fields->events->output_time <= t){
            self->output(0, fields->events->type, fields->events->value);
            ARDelayedEvent* tmp = fields->events;
            fields->events = tmp->next;
            free(tmp);
        }
        
        //if there is no more object, stop the timer callback.
        if (fields->events == NULL){
            self->stopScheduledTimerCallback();
        }
        return;
    }
    
    //now check if the delay time set is valid
    if (ARMessageType::INT != t1 && ARMessageType::FLOAT != t1){
        
        //restore the correct delay time.
        ARValue v;
        v.i = fields->delay_time;
        self->setInletValue(1, ARMessageType::INT, v);
    }
    else {
        //if the delay time is zero, just send out the received message/no queueing
        int32_t v = self->getInputInt(1);
        if (v == 0){
            fields->delay_time = v;
            self->output(0, t0, v0);
            return;
        }
        //if the delay time is negative, restore the previous delay time.
        else if (v < 0){
            //restore the correct delay time.
            ARValue v;
            v.i = fields->delay_time;
            self->setInletValue(1, ARMessageType::INT, v);
        }
        //keep the delay time so that we can restore.
        else {
            fields->delay_time = v;
        }
    }
    
    //schedule timer callback
    self->scheduleTimerCallback(fields->delay_time,
                                ARTimerCallbackScheduleMode::SCHEDULE_IF_CLOSER);
    
    //allocate a new event object
    ARDelayedEvent* e = (ARDelayedEvent*)malloc(sizeof(ARDelayedEvent));
    if (e == NULL){
		panic("trigger_func_delay: not enough memory");
    }
    
    uint32_t output_time = now() + fields->delay_time;
    e->output_time = output_time;
    e->type  = t0;
    e->value = v0;

    //now insert into the event queue.
    
    //no event, just push it to the head.
    if (fields->events == NULL){
        fields->events = e;
        e->next = NULL;
        return;
    }
    
    //the time stamp is the smallest.
    if (e->output_time < fields->events->output_time){
        e->next = fields->events;
        fields->events = e;
        return;
    }
    
    //seek for the position where we should insert the event.
    ARDelayedEvent* prev = fields->events;
    ARDelayedEvent* curr = fields->events->next;
    
    while(curr != NULL){
        //found the place to insert.
        if (e->output_time < curr->output_time){
            prev->next = e;
            e->next = curr;
            return;
        }
        prev = curr;
        curr = curr->next;
    }
    
    //if reached here, that means the time stamp is the largest.
    //the insertion is performed at the tail.
    prev->next = e;
    e->next = NULL;
    
    return;
}
