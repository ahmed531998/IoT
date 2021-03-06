package iot.unipi.it;

import java.sql.SQLException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class CollectorMqttClient implements MqttCallback{
	
	private String broker = "tcp://127.0.0.1";
	private String clientId = "JavaApp";
	private String subTopic = "temperature";
	private String pubTopic = "alarm";
	private MqttClient mqttClient = null;
	
	public CollectorMqttClient() throws InterruptedException {
		do {
			int timeWindow = 50000;
			Thread.sleep(timeWindow);
			try {
				this.mqttClient = new MqttClient(this.broker,this.clientId);
				this.mqttClient.setCallback( this );
				this.mqttClient.connect();
				this.mqttClient.subscribe(this.subTopic);
			}catch(MqttException me) {
				System.out.println("I could not connect, Retrying ...");
			}
		}while(!this.mqttClient.isConnected());
	}
	
	public void publish(String content, String node) throws MqttException{
		try {
			MqttMessage message = new MqttMessage(content.getBytes());
			this.mqttClient.publish(this.pubTopic+node, message);
		} catch(MqttException me) {
			me.printStackTrace();
		}
	}
	
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		System.out.println("Connection is broken");
		int timeWindow = 3000;
		while (!this.mqttClient.isConnected()) {
			try {
				System.out.println("Trying to reconnect in " + timeWindow/1000 + " seconds.");
				Thread.sleep(timeWindow);
				System.out.println("Reconnecting ...");
				timeWindow *= 2;
				this.mqttClient.connect();
				this.mqttClient.subscribe(this.subTopic);
				System.out.println("Connection is restored");
			}catch(MqttException me) {
				System.out.println("I could not connect");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
		
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		byte[] payload = message.getPayload();
		
		try {
			JSONObject sensorMessage = (JSONObject) JSONValue.parseWithException(new String(payload));
			if (sensorMessage.containsKey("temperature")) {
				int timestamp = Integer.parseInt(sensorMessage.get("timestamp").toString());
				Integer numericValue = Integer.parseInt(sensorMessage.get("temperature").toString());
				String nodeId = sensorMessage.get("node").toString();
				int lower = 36;
				int upper = 39;
				boolean on = false;
				String reply;
				if (numericValue < lower || numericValue > upper) {
					System.out.println("Dangerous");
					reply = "on";
					on = true;
				}
				else {
					System.out.println("Safe");
					reply = "off";
					on = false;
				}
				publish(reply, nodeId);
				System.out.println("Writing to DB: " + ("Node: "+ "mqtt://"+nodeId+
                                                "\ttime: "+timestamp+
                                                "\tvalue: "+numericValue+
                                                " celsius\talarm on? "+on));

                                try {
                                        Collector.db.addReading("mqtt1://"+nodeId, timestamp, numericValue, on);

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} else {
				System.out.println("Garbage data from sensor");
			}	
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		System.out.println("Delievery Complete");
		
	}

}
