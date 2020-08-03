//
// arobj_tone.hpp
//
// generated by the Ardestan programming language : Wed Jul 08 18:32:39 CST 2020
//

#ifndef __arobj_tone_h__
#define __arobj_tone_h__
//----------------------------------------------------------------------------
// type
//----------------------------------------------------------------------------

typedef struct {
	uint8_t pin_no:7;
	uint8_t active:1;
	long	start_time;
} ARObjTone;

//----------------------------------------------------------------------------
// declaration
//----------------------------------------------------------------------------

void init_func_tone(
    ARObject*        self      ,
    void*            __fields__,
    uint_fast8_t     argc      ,
    ARMessageType*   argt      ,
    ARValue*         argv
);

void trigger_func_tone(
    ARObject*        self      ,
    int32_t          inlet_no  ,
    void*            __fields__
);
#endif
