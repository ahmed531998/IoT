# IoT Project 2021 - AIDE - UNIPI
An IoT-based system that is intended to keep track of patients temperatures at the hospital and take action accordingly by triggering an alarm (simulated by a led) if necessary. 

# Instructions Manual
Please follow these steps in order

Locally, on the virtual machine,
1) Save the repo under contiki-ng/examples directory.
2) Load the simulation from the repo 
3) Compile border-router.c under the "CoAP Network"/"Border Router"/ directory and node.c under the "CoAP Network"/"Sensor"/ directory
4) Start the simulation
5) Run this command under the "CoAP Network"/"Border Router"/ directory ==>  make TARGET=cooja connect-router-cooja

Remotely, on the testbed,
4) Connect to the remote testbed via ssh tunneling
5) In the terminal, type sudo vi /etc/mosquitto/mosquitto.conf
6) Add the following 3 lines: 
 	1- allow_anonymous true
	2- listener 1883 fd00::1
	3- listener 1883 localhost
7) Save the modifications.
8) Under the Mqtt Network directory, modify the PANID in the project-conf.h of the border router to your PANID (If it is to be executed on the remote testbed)
9) Start mosquitto  ==> sudo mosquitto -c /etc/mosquitto/mosquitto.conf
10) Deploy the code under the Mqtt Network directory on the remote sensors (one for the border router and another for the sensor)

Locally, on the virtual machine
11) modify the database configuration in the Collector.java file under the my-app java folder inside the iot.unipi.it package
12) run "mvn clean install" in terminal
13) run the code
