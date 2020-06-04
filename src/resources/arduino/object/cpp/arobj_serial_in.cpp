//
//  serial_print.cpp
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/14/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//


#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

#include "ArdestanIDs.h"
#include "Ardestan.h"

#include "arobj_serial_in.h"


static ARObjSerialIn* arobj_serial_in_queue = NULL;

//the arduino's serial input buffer is 64 bytes length.
static char arobj_serial_string_buffer[AROBJ_SERIAL_IN_ARDUINO_INPUT_BUFFER_MAX];

static ARObjSerialInReadMode 	arobj_serial_in_current_read_mode	= NORMAL_READ;
static uint8_t					arobj_serial_in_current_buffer_idx 	= 0;
//----------------------------------------------------------------------------
// implementation
//----------------------------------------------------------------------------
void serial_in_queue_handle_serial_input()
{
	//there is no object in the queue.
	if (arobj_serial_in_queue == NULL){
		return;
	}

	//the read loop.
	while(Serial.available() > 0){

		//if the state is skil_until_null.
		if (arobj_serial_in_current_read_mode == SKIP_UNTIL_NULL){
			while(Serial.available() > 0){
				int32_t v = Serial.read();
				if (v == 0x00){
					arobj_serial_in_current_read_mode = NORMAL_READ;
					break;
				}
			}
		}

		//if we are still in the SKIP_UNTIL_NULL mode, then return.
		if (arobj_serial_in_current_read_mode != NORMAL_READ){
			return;
		}


		//read the next input string
		while(Serial.available() > 0){

			bool messageInputHandled = false;

			//read the input bytes
			int v = Serial.read();

			//avoid the buffer overflow
			if (v != 0x00 && arobj_serial_in_current_buffer_idx == AROBJ_SERIAL_IN_ARDUINO_INPUT_BUFFER_MAX - 1){
				v = 0x00;
				arobj_serial_in_current_read_mode = SKIP_UNTIL_NULL;
			}


			arobj_serial_string_buffer[arobj_serial_in_current_buffer_idx] = (char)v;
			arobj_serial_in_current_buffer_idx++;

			if (v == 0x00){
				serial_in_queue_output();
				messageInputHandled = true;
			}

			//if the current message input handled,
			//proceed to the next input data.
			if (messageInputHandled){
				//rewind the buffer index.
				arobj_serial_in_current_buffer_idx = 0;

				//if the left most input data in the argument list just has been finished.
				if (arobj_serial_in_queue->current_index == 0){
					ARObjSerialIn* p = arobj_serial_in_queue;
					arobj_serial_in_queue = arobj_serial_in_queue->next;

					//make the pointer point to itself. This means the element is not in the queue.
					p->next = p;

					//if there is no more data, just return;
					if (arobj_serial_in_queue == NULL){
						return;
					}
				}
				//if there are more arguments to be handled
				else {
					//decrement the argument index and reset the buffer write index.
					arobj_serial_in_queue->current_index--;
				}
			}
		}
	}

	return;
}

void serial_in_queue_output()
{
	ARObjSerialInArg t;
	t = arobj_serial_in_queue->args[arobj_serial_in_queue->current_index];

	ARObject* self = object_id_to_object_pt(arobj_serial_in_queue->obj_id);

	switch(t){

	case SERIAL_IN_INTEGER:{
		int32_t v = atol(arobj_serial_string_buffer);
		self->outputInt(arobj_serial_in_queue->current_index, v);
	}
		break;

	case SERIAL_IN_FLOAT:{
		float v = atof(arobj_serial_string_buffer);
		self->outputFloat(arobj_serial_in_queue->current_index, v);
	}
		break;

	case SERIAL_IN_STRING:{
		char* p = (char*)malloc(arobj_serial_in_current_buffer_idx + 1);
		if (p != NULL){
			strcpy(p, arobj_serial_string_buffer);
			ARStr* s = ARObject::createString(p, 1);
			self->outputString(arobj_serial_in_queue->current_index, s);
			ARObject::releaseString(s);
		}
	}
		break;
	}

	return;
}

void serial_in_queue_insert(ARObjSerialIn* elem)
{
	//the object is already in the wait queue.
	if (elem->next != elem){
		return;
	}

	//put the object in the queue.
	elem->current_index = elem->arg_last_index;
	elem->next = NULL;

	//this is the first one in the queue.
	if (arobj_serial_in_queue == NULL){
		arobj_serial_in_queue = elem;
		arobj_serial_in_current_buffer_idx = 0;

		return;
	}

	//if not, traverse the list and add to the last.
	ARObjSerialIn* p = arobj_serial_in_queue;
	while(p->next != NULL){
		p = p->next;
	}

	p->next = elem;

	return;
}

void init_func_serial_in 	 (ARObject*         self        ,
                              void*             __fields__  ,
                              uint_fast8_t      argc        ,
                              ARMessageType*    argt        ,
                              ARValue*          argv        )
{
	ARObjSerialIn* fields = (ARObjSerialIn*)__fields__;

	//this is not likely to happen as the programming environment
	//will take care of the minimum number of arguments.
	if (argc < 1){
		goto invalid_argument_given;
	}

	//check the validity of the given arguments
	for (int_fast8_t i = 0; i <  argc; i++){
		if (argt[i] != ARMessageType::SYM_ID){
			goto invalid_argument_given;
		}
		ARSymID id = argv[i].sym_id;
		switch(id){
		case ID_I:
		case ID_INTEGER:
		case ID_F:
		case ID_FLOAT:
		case ID_STRING:
			break;
		default:
			goto invalid_argument_given;
		}
	}

	for(int_fast8_t i = 0; i < argc; i++){
		switch (argv[i].sym_id){
		case ID_I:
		case ID_INTEGER:
			fields->args[i] = SERIAL_IN_INTEGER;
			break;
		case ID_F:
		case ID_FLOAT:
			fields->args[i] = SERIAL_IN_FLOAT;
			break;
		case ID_STRING:
			fields->args[i] = SERIAL_IN_STRING;
			break;
		}
	}

	//if the fields->next pointer points itself,
	//it is not in the input queue.
	fields->next = fields;
	fields->obj_id = object_pt_to_object_id(self);
	fields->arg_last_index = argc - 1;

	return;


invalid_argument_given:
	//it can be avoided to put this object into the input wait queue
	//by setting the next field to NULL;
	fields->next = NULL;
	return;
}

void trigger_func_serial_in  	 (ARObject* self        ,
                                  int32_t   inlet_no    ,
                                  void*     __fields__  )
{
	ARObjSerialIn* fields = (ARObjSerialIn*)__fields__;

    ARMessageType t0 = self->getInputType(0);

    if (t0 != ARMessageType::SYM_ID){
    	return;
    }

    ARValue v0 = self->getInput(0);
    if (v0.sym_id != ID_BANG){
    	return;
    }

    //if it is a bang message, schedule the input
    serial_in_queue_insert(fields);

	return;
}
