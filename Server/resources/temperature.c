#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"
#include <stdio.h>
#include <time.h>
#include "coap-observe.h"

#define MAXTEMP 50
#define MINTEMP 16


//API FUNCTION DEFINITIONS
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);


//SENSOR DATA GENERATION
static int temperatureValue = 25;

int measure_temperature(){
	int measuredValue = (rand() % (MAXTEMP+1)) + 16;
	return measuredValue;
}


//RESOURCE DEFINITION
EVENT_RESOURCE(temperature,
	"title=\"Temperature Sensor\";obs;rt=\"Temperature\"",
	res_get_handler,
	NULL,
	NULL,
	NULL,
	res_event_handler);


//API FUNCTIONS IMPLEMENTATIONS
static void res_event_handler(void){
	coap_notify_observers(&temperature);
}



static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){

	srand(time(NULL));
	temperatureValue = measure_temperature();

	unsigned int accept = -1;
	coap_get_header_accept(request, &accept);

	//For easy debugging -- to be removed
	if (accept == -1 || accept == TEXT_PLAIN) {
    		coap_set_header_content_format(response, TEXT_PLAIN);
    		snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "%d", temperatureValue);
    coap_set_payload(response, (uint8_t *)buffer, strlen((char *)buffer));
	}


	else if (accept == APPLICATION_JSON){
		coap_set_header_content_format(response, APPLICATION_JSON);
		snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"temperature\": %d, \"timestamp\": %lu, \"unit\": \"celsius\"}", temperatureValue, clock_seconds());
		coap_set_payload(response, buffer, strlen((char *)buffer));
	}
	else {
		coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
		const char *msg = "Supporting content-type application/json";
   		 coap_set_payload(response, msg, strlen(msg));
	}	

}
