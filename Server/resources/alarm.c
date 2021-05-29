#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"
#include <string.h>

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);


RESOURCE(alarm,
	"title=\"LED Alarm\";rt=\"Control\"",
	NULL,
	res_post_handler,
	NULL,
	NULL);


static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	leds_toggle(LEDS_RED);
}

