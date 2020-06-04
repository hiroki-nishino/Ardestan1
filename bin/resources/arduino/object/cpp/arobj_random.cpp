//
//  arobj_toggle.c
//  Ardestan
//
//  Created by Hiroki NISHINO on 12/13/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//


#include <stdio.h>
#include <stdint.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"
#include "arobj_random.h"

//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------
static uint32_t seed_base = 2463534242;

void init_func_random(ARObject*       self        ,
                      void*           __fields__  ,
                      uint_fast8_t    argc        ,
                      ARMessageType*  argt        ,
                      ARValue*        argv        )
{
	ARObjRandom* fields = (ARObjRandom*)__fields__;

	ARValue v;
	v.i = INT32_MAX;
    
    //range
    if (0 < argc && argt[0] == ARMessageType::INT){
    	v.i = argv[0].i;
    }
  
  	self->setInletValue(1, ARMessageType::INT, v);
  	
    //seed 
    if (1 < argc && argt[1] == ARMessageType::INT){
    	fields->seed = argv[1].i;
    }
  	else {
		fields->seed = seed_base + object_pt_to_object_id(self);
  	}
  	
    return;
}

void trigger_func_random(ARObject*  self        ,
                         int32_t    inlet_no    ,
                         void*      __fields__  )
{
	ARObjRandom* fields = (ARObjRandom*)__fields__;

	ARMessageType t = self->getInputType(0);

	//if the input is an integer, use it as a new seed value.
	if (t == ARMessageType::INT){
		ARValue v = self->getInput(0);
		fields->seed = v.i;
	}
	
	int32_t ulim = self->getInputInt(1);
	ulim = ulim < 1 ? 1 : ulim;

	//the xorshit algorithm
	uint32_t y = fields->seed;
	y = y ^ (y << 13); 
	y = y ^ (y >> 17);
  	y = y ^ (y << 15);
  	fields->seed = y;
  	
	self->outputInt(0, y % ulim);
	return;
}
