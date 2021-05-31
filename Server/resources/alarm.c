#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

#include <string.h>

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_DBG

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(ledAlarm,
	"title=\"LED Alarm: ?color=r|g|y, POST mode=on|off\";rt=\"Control\"",
	NULL,
	res_post_handler,
	NULL,
	NULL);


static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	if(request != NULL) {
		LOG_DBG("POST Request Sent\n");
	}
	
  size_t len = 0;
  const char *color = NULL;
  const char *mode = NULL;
  uint8_t led = 0;
  int success = 1;

  if((len = coap_get_query_variable(request, "color", &color))) {
    LOG_DBG("color %.*s\n", (int)len, color);
    
    if(strncmp(color, "r", len) == 0) {
      led = LEDS_RED;
    } else {
      success = 0;
    }

  } else {
    success = 0;
  }
 
  if(success && (len = coap_get_post_variable(request, "mode", &mode))) {
    LOG_DBG("mode %s\n", mode);

    if(strncmp(mode, "on", len) == 0) {
      leds_on(LEDS_NUM_TO_MASK(led));
    } else if(strncmp(mode, "off", len) == 0) {
      leds_off(LEDS_NUM_TO_MASK(led));
    } else {
      success = 0;
    }
  } else {
    success = 0;
  } if(!success) {
    coap_set_status_code(response, BAD_REQUEST_4_00);
  }
}
