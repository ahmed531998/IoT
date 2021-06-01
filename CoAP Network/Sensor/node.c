#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/etimer.h"
#include "coap-blocking-api.h"
#include "random.h"
#include "node-id.h"
#include "whatever.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP


#define SERVER "coap://[fd00::1]:5683"


static struct etimer myPeriodicTimer;
bool registered = false;

void client_chunk_handler(coap_message_t *response)
{
	const uint8_t *chunk;

	if(response == NULL) {
		LOG_INFO("Request timed out");
		return;
	}
	
	int len = coap_get_payload(response, &chunk);
	registered = true;	

	LOG_INFO("|%.*s \n", len, (char *)chunk);
}

PROCESS(node, "node");
AUTOSTART_PROCESSES(&node);

int temperatureValue = 37;

extern coap_resource_t ledAlarm;
extern coap_resource_t temperature;

#define MAXTEMP 41
#define MINTEMP 35

int measure_temperature(){
    int measuredValue = (rand() % (MAXTEMP-MINTEMP+1)) + MINTEMP;
    return measuredValue;
}


PROCESS_THREAD(node, ev, data)
{
  srand(time(NULL));
  static coap_endpoint_t myServer;
  static coap_message_t request[1];


  PROCESS_BEGIN();

  PROCESS_PAUSE();

  LOG_INFO("Starting sensor node\n");

  coap_activate_resource(&ledAlarm, "alarm");
  coap_activate_resource(&temperature, "temperature");

  coap_endpoint_parse(SERVER, strlen(SERVER), &myServer);

  coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
  coap_set_header_uri_path(request, "registration");

  LOG_DBG("Registering with server\n");
  COAP_BLOCKING_REQUEST(&myServer, request, client_chunk_handler);

 while(!registered){
	LOG_DBG("Retry registeration \n");
	COAP_BLOCKING_REQUEST(&myServer, request, client_chunk_handler);
 }


  etimer_set(&myPeriodicTimer, 30*CLOCK_SECOND);
  
  while(1) {
    PROCESS_WAIT_EVENT();

      if (ev == PROCESS_EVENT_TIMER && data == &myPeriodicTimer){
	  temperatureValue = measure_temperature();
	  temperature.trigger();
	  etimer_reset(&myPeriodicTimer);
      }
    }

  PROCESS_END();
}
