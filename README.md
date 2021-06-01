# IoT Project 2021 - AIDE - UNIPI
An IoT-based system that is intended to keep track of patients temperatures at the hospital and take action accordingly by triggering an alarm (simulated by a led) if necessary. 

# Instructions Manual
Please follow these steps in order

# Locally, on the virtual machine,
1) Save the repo under contiki-ng/examples directory.
2) Load the simulation from the repo 
3) Compile border-router.c under the "CoAP Network"/"Border Router"/ directory and node.c under the "CoAP Network"/"Sensor"/ directory
4) Start the simulation
5) Run this command under the "CoAP Network"/"Border Router"/ directory ==>  make TARGET=cooja connect-router-cooja

# Remotely, on the testbed,
1) Connect to the remote testbed via ssh tunneling
2) In the terminal, type sudo vi /etc/mosquitto/mosquitto.conf
3) Add the following 3 lines: 
 	1- allow_anonymous true
	2- listener 1883 fd00::1
	3- listener 1883 localhost
4) Save the modifications.
5) Under the Mqtt Network directory, modify the PANID in the project-conf.h of the border router to your PANID (If it is to be executed on the remote testbed)
6) Start mosquitto  (see instructions.txt)
7) Deploy the code under the Mqtt Network directory on the remote sensors (one for the border router and another for the sensor) (see instructions.txt)

# Locally, on the virtual machine,
1) modify the database configuration in the Collector.java file under the my-app java folder inside the iot.unipi.it package
2) run "mvn clean install" in terminal
3) run the code
