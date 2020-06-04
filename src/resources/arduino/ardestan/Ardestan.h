//
//  Ardestan.h
//  Ardestan
//
//  Created by Hiroki NISHINO on 10/30/18.
//  Copyright © 2018 Hiroki Nishino. All rights reserved.
//

#ifndef Ardestan_h
#define Ardestan_h


//----------------------------------------------------------------------------
// constant values.
//----------------------------------------------------------------------------
#define AROBJECT_MAX_NUM_INLETS   (5)
#define AROBJECT_MAX_NUM_OUTLETS  (8)
#define AROBJECT_MAX_NUM_OBJECTS  (1024)


#define REF_CNT_CONST_STRING 	(UINT8_MAX)
#define STRING_REF_CNT_MAX 		(REF_CNT_CONST_STRING - 1)

//----------------------------------------------------------------------------
// typedef (visible to other c files).
//----------------------------------------------------------------------------
class   ARObject;

//----------------------------------------------
typedef struct {
    uint32_t    timestamp;
    uint16_t    obj_id: 10;
    uint16_t    next  : 6;
} ARTimerEvent;

//----------------------------------------------
// message
typedef enum: uint8_t {
    INT = 0,
    FLOAT,
    SYM_ID,
    STRING,
//    LIST,
//    USER_OBJECT_POINTER
} ARMessageType;


typedef struct {
    uint8_t   	ref_cnt;
    const char*	p;
} ARStr;

typedef union {
    int32_t     i       ;
    float       f       ;
    ARSymID     sym_id  ;
    ARStr* 		str     ;
} ARValue;


//----------------------------------------------
// scheduler mode
typedef enum {
    SCHEDULE_IF_CLOSER,
    OVERWRITE
} ARTimerCallbackScheduleMode;

//----------------------------------------------
// panic mode
typedef enum: uint8_t {
	JUST_HALT,
	SERIAL_OUTPUT_MESSAGE,
	BLINK_LED_BUILTIN,
} ARPanicMode;

//----------------------------------------------
// connection
typedef struct {
    const uint8_t     outlet_no   :3  ;
    const uint16_t    dest_obj_id:10  ;
    const uint8_t     inlet_no    :3  ;
} ARConnection;

typedef struct {
    const uint16_t connection_id      :10  ;
    const uint8_t  num_of_connections :6   ;
} AROutlets;

//----------------------------------------------
//typedef for user functions to be called back when triggered.

typedef void  (*__AR_INIT_FUNC_PT__)     (ARObject*         self        ,
                                          void*             __fields__  ,
                                          uint_fast8_t      argc        ,
                                          ARMessageType*    argt        ,
                                          ARValue*          argv        );

typedef void  (*__AR_TRIGGERED_FUNC_PT__)(ARObject* self        ,
                                          int32_t   inlet_no    ,
                                          void*     __fields__  );

//----------------------------------------------------------------------------
// external global variables
//----------------------------------------------------------------------------
extern ARObject     g_objects[];
extern ARValue      g_inlets[];

#ifdef USE_PROGMEM_FOR_OUTLETS
extern const uint16_t g_outlets [] PROGMEM;
#else
extern AROutlets    g_outlets[];
#endif

#ifdef USE_PROGMEM_FOR_CONNECTIONS
extern const uint16_t g_connections [] PROGMEM;
#else
extern ARConnection g_connections[];
#endif

extern uint8_t      g_memory_space[];
extern uint32_t     g_timer_callback_timestamp[];
extern uint8_t		g_panic_mode;
//----------------------------------------------------------------------------
// functions
//----------------------------------------------------------------------------

//time
uint32_t now(void);
void set_now(uint32_t time);

//load bang
void trigger_load_bangs(uint16_t last_load_bang_index);
//polling
void trigger_pollings(uint16_t first_polling_index, uint16_t last_polling_index);

//object pt <--> object id
uint16_t        object_pt_to_object_id  (ARObject *object);
ARObject*       object_id_to_object_pt  (uint16_t object_id);

//object id-> outlets, connection_id -> connection
AROutlets       object_id_to_outlets        (uint16_t object_id     );
ARConnection    connection_id_to_connection (uint16_t connection_id );

//scheduling
uint32_t        process_scheduled_timer_callback(uint32_t upper_limit);

//panic
void			panic(const char* message);
//----------------------------------------------------------------------------
// ARObject
//----------------------------------------------------------------------------
//----------------------------------------------
//object
class ARObject final
{
public:

    static ARStr* createString	 (const char* s, uint32_t initial_ref_cnt);
    static ARStr* retainString   (ARStr* pointer);
    static ARStr* releaseString  (ARStr* pointer);


    void init (void*                    fields          ,
               __AR_TRIGGERED_FUNC_PT__ triggered_func  ,
               int32_t                  num_inlets      ,
               ARValue*                 inlets          ,
               bool                     only_leftmost_inlet_is_hot = true);
    
    void trigger(int32_t inlet_no);

    void outputInt      (int32_t outlet_no, int32_t value);
    void outputFloat    (int32_t outlet_no, float   value);
    void outputSymbol   (int32_t outlet_no, ARSymID value);
    void outputString   (int32_t outlet_no, ARStr*  value);

    ARMessageType   getInputType    (int32_t inlet_no);
    int32_t         getInputInt     (int32_t inlet_no);
    float           getInputFloat   (int32_t inlet_no);
    ARSymID         getInputSymbol  (int32_t inlet_no);
    ARStr*          getInputString  (int32_t inlet_no);
    ARValue         getInput        (int32_t inlet_no);

    void input          (int32_t        inlet_no    ,
                         ARMessageType  type        ,
                         ARValue        value       );
    
    void setInletValue  (int32_t        inlet_no    ,
                         ARMessageType  type        ,
                         ARValue        value       );

    void output         (uint_fast8_t   outlet_no   ,
                         ARMessageType  type        ,
                         ARValue        value       );
        
    uint8_t scheduleTimerCallback       (int32_t  time      ,
                                         ARTimerCallbackScheduleMode mode);

    uint8_t stopScheduledTimerCallback  (void);

    
private:
    void*           fields          ;
    
    __AR_TRIGGERED_FUNC_PT__ triggered_func;

    ARValue*        inlets;

    struct {
        uint8_t only_leftmost_inlet_is_hot: 1;
        uint8_t input0_type : 3;
        uint8_t input1_type : 3;
        uint8_t input2_type : 3;
        uint8_t input3_type : 3;
        uint8_t input4_type : 3;
    } io_info;
};


#define SERIAL_PRINT_FLOAT_DECIMAL_PLACES (2)

#endif /* Ardestan_h */
