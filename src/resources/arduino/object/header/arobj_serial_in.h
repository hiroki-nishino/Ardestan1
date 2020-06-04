//
//  serial_in.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 3/14/19.
//  Copyright © 2019 Hiroki Nishino. All rights reserved.
//

#ifndef serial_in_h
#define serial_in_h

//----------------------------------------------------------------------------
// type
//----------------------------------------------------------------------------
#define AROBJ_SERIAL_IN_MAX_ARG_NUM_IN_BYTES (4)
#define AROBJ_SERIAL_IN_ARDUINO_INPUT_BUFFER_MAX (64)

typedef enum {
	NORMAL_READ	,
	SKIP_UNTIL_NULL
} ARObjSerialInReadMode;


typedef enum {
	SERIAL_IN_INTEGER,
	SERIAL_IN_FLOAT,
	SERIAL_IN_STRING
} ARObjSerialInArg;


typedef struct ARObjSerialIn {
	struct ARObjSerialIn* next;
	uint8_t		args[AROBJ_SERIAL_IN_MAX_ARG_NUM_IN_BYTES]; //i/f/string
	uint8_t		arg_last_index:3;
	uint8_t		current_index:3;
	uint16_t   	obj_id: 10;
} ARObjSerialIn;



//----------------------------------------------------------------------------
// declarations
//----------------------------------------------------------------------------
void serial_in_queue_insert 			(ARObjSerialIn* elem);
void serial_in_queue_handle_serial_input();
void serial_in_queue_output				();

void init_func_serial_in 	 (ARObject*         self        ,
                              void*             __fields__  ,
                              uint_fast8_t      argc        ,
                              ARMessageType*    argt        ,
                              ARValue*          argv        );

void trigger_func_serial_in  	 (ARObject* self        ,
                                  int32_t   inlet_no    ,
                                  void*     __fields__  );
#endif /* serial_in_hpp */
