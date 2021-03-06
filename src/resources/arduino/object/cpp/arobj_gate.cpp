//
// arobj_gate.cpp
//
// generated by the Ardestan programming language : Thu Jul 09 15:45:15 CST 2020
//

#include <stdio.h>
#include <stdint.h>
#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_gate.hpp"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_gate(
    ARObject*        self      ,
    void*            __fields__,
    uint_fast8_t     argc      ,
    ARMessageType*   argt      ,
    ARValue*         argv
)
{
    ARObjGate* fields = (ARObjGate*)__fields__;

	fields->num_outlets = 0;

	//set the default gate no. to zero (stop).
	ARValue v;
	v.i = 0;
	self->setInletValue(1, ARMessageType::INT, v);

	if (argc >= 1 && argt[0] == ARMessageType::INT){
		fields->num_outlets = argv[0].i;
	}
	return;
}

void trigger_func_gate(
    ARObject*        self      ,
    int32_t          inlet_no  ,
    void*            __fields__
)
{
    ARObjGate* fields = (ARObjGate*)__fields__;

	//if the gate no. is invalid, do nothing.
	if (self->getInputType(1) != ARMessageType::INT){
		return;
	}

	//check if the gate no. is valid for routing the incoming message.
	int32_t gate = self->getInputInt(1);
	if (gate < 1 || gate > fields->num_outlets){
		return; 
	}

	//adjust to the outlet no.
	int outlet = gate - 1;

	ARMessageType 	t = self->getInputType(0);
	ARValue 		v = self->getInput(0);
	self->output(outlet, t, v);
	
	return;
}
