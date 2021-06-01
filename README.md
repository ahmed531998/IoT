# IoT Project 2021 - AIDE - UNIPI
An IoT-based system that is intended to keep track of patients temperatures at the hospital and take action accordingly by triggering an alarm (simulated by a led) if necessary. 

# Instructions Manual
1) In terminal, type sudo vi /etc/mosquitto/mosquitto.conf
2) Add the following lines: 
	allow_anonymous true
	listener 1883 fd00::1
	listener 1883 localhost
3) Save the modifications.
4) Under the Mqtt Network directory, modify the PANID in the project-conf.h of the border router to your PANID.
5) Put everything under the contiki-ng/examples directory
