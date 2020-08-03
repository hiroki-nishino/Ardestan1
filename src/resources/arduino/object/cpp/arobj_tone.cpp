//
// arobj_tone.cpp
//
// generated by the Ardestan programming language : Wed Jul 08 18:32:39 CST 2020
//

#include <stdio.h>
#include <stdint.h>
#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_tone.hpp"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------

void init_func_tone(
    ARObject*        self      ,
    void*            __fields__,
    uint_fast8_t     argc      ,
    ARMessageType*   argt      ,
    ARValue*         argv
)
{
    ARObjTone* fields = (ARObjTone*)__fields__;

	fields->active = 0;

	ARValue v;
	
	//set the duration and the ratio to the default values.
	//so that we can make it invalid
	v.i = 1000;
	self->setInletValue(1, ARMessageType::INT, v);
	v.i = 1;
	self->setInletValue(2, ARMessageType::INT, v);
	
	
	//pin no.
	if (argc < 1 || argt[0] != ARMessageType::INT){
		//treat 127 as an invalid value. 
		//(we expect there are not so many pins for the arduino).
		fields->pin_no = 127;
		return;
	}

	fields->pin_no = argv[0].i;

	//duration
	if (argc < 2){
		return;
	}
	if (argt[1] != ARMessageType::INT){
		//set an invalid value.
		v.i = -1;
		self->setInletValue(1, ARMessageType::INT, v);
		return;
	}

	self->setInletValue(1, ARMessageType::INT,  argv[1]);

	//tone ratio
	if (argc < 3){
		return;
	}

	if (argt[2] == ARMessageType::INT){
		self->setInletValue(2, ARMessageType::INT, argv[2]);
	}
	else if (argt[2] == ARMessageType::FLOAT){
		self->setInletValue(2, ARMessageType::FLOAT, argv[2]);
	}
	else {
		//set an invalid value.
		v.i = -1;
		self->setInletValue(2, ARMessageType::INT, v);		
	}

	return;
}

void trigger_func_tone(
    ARObject*        self      ,
    int32_t          inlet_no  ,
    void*            __fields__
)
{
    ARObjTone* fields = (ARObjTone*)__fields__;

	//if the pin no is 127, we do nothing.
	if (fields->pin_no == 127){
		return;
	}

	//stop the sound first.
	noTone(fields->pin_no);
	
	//if it is not a integer value, just return;
	if (self->getInputType(0) != ARMessageType::INT){
		return;
	}

	int32_t f = self->getInputInt(0);
	if (f < 31 || 0xFFFF < f){
		return;
	}

	unsigned int freq = (unsigned int)f;
	
	//check the duration.
	if (self->getInputType(1) != ARMessageType::INT){
		return;
	}
	
	int dur = self->getInputInt(1);
	if (dur < 0){
		return;
	}

	//check the tone ratio.
	ARMessageType t2 = self->getInputType(2);
	if (t2 != ARMessageType::INT && t2 != ARMessageType::FLOAT){
		return;
	}

	double ratio = self->getInputFloat(2);
	if (ratio < 0.0 || ratio > 1.0){
		return;
	}

	tone(fields->pin_no, f, (int)(dur * ratio));

	return;
}
