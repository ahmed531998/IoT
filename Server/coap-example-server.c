#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "sys/etimer.h"


/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

extern coap_resource_t alarm,
  temperature;

static struct etimer myPeriodicTimer;


PROCESS(node, "node");
AUTOSTART_PROCESSES(&node);

PROCESS_THREAD(node, ev, data)
{
  PROCESS_BEGIN();

  PROCESS_PAUSE();

  LOG_INFO("Starting sensor node\n");

  coap_activate_resource(&alarm, "alarm");
  coap_activate_resource(&temperature, "temperature");

  etimer_set(&myPeriodicTimer, 30*CLOCK_SECOND);

  while(1) {
    PROCESS_WAIT_EVENT();

      if (ev == PROCESS_EVENT_TIMER && data == &myPeriodicTimer){
	  res_obs.trigger();
	  etimer_reset(&myPeriodicTimer, 30*CLOCK_SECOND);
      }
    }

  PROCESS_END();
}
