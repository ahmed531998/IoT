package iot.unipi.it;

import java.sql.SQLException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class CollectorCoapClient{
	private CoapClient client;

	private Thermometer t;
	private Alarm a;
 
	private boolean toCancel = false;
	
	public CollectorCoapClient(Thermometer t) {
	    this.client = new CoapClient(t.getResourceURI());
		this.t = t;
		this.a = null;
	}

	public void startObserving() {
		CoapObserveRelation coapObserveRelation = this.client.observe(new CoapHandler() {		
			public void onLoad(CoapResponse response) {
				try {
					JSONObject sensorMessage = (JSONObject) JSONValue.parseWithException(response.getResponseText());
					if (sensorMessage.containsKey("temperature")) {
						int timestamp = Integer.parseInt(sensorMessage.get("timestamp").toString());
						Integer numericValue = Integer.parseInt(sensorMessage.get("temperature").toString());
						int lower = 36;
						int upper = 39;
						int index = Collector.thermometers.indexOf(t);
                        a = Collector.alarms.get(index);
						if (numericValue < lower || numericValue > upper) {
                        	a.setState(true);
                        	System.out.println("Dangerous");
                        	String payload = "mode=on";
                        	Request req = new Request(Code.POST);
                        	req.setConfirmable(true);
                        	req.setPayload(payload);
                        	req.setURI(a.getResourceURI()+"?color=r");
                        	req.send();
						} else {
							a.setState(false);
                        	String payload = "mode=off";
                        	Request req = new Request(Code.POST);
                        	req.setConfirmable(true);
                        	req.setPayload(payload);
                        	req.setURI(a.getResourceURI()+"?color=r");
                        	req.send();
							System.out.println("Safe");
						}
						System.out.println("Writing to DB: " + ("Node: "+t.getResourceURI()+
								"\ttime: "+timestamp+
								"\tvalue: "+numericValue+
								" celsius\talarm on? "+a.getState()));
						try {
							Collector.db.addReading(t.getResourceURI(), timestamp, numericValue, a.getState());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						System.out.println("Garbage data from sensor");
						return;
					}	
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			public void onError() {
				toCancel = true;
				System.out.println("I cannot observe anything from " + t.getResourceURI());
				for (int i = 0; i < Collector.thermometers.size(); i++) {
					if(Collector.thermometers.get(i).getResourceURI().equals(t.getResourceURI())){
						Collector.thermometers.remove(i);
						Collector.alarms.remove(i);
						break;
					}
				}
				return;
			}
		});
		if (toCancel) {
			coapObserveRelation.proactiveCancel();
		}
		if(coapObserveRelation.isCanceled()) {
			System.out.println("Observation is cancelled for " + t.getResourceURI());
			for (int i = 0; i < Collector.thermometers.size(); i++) {
				if(Collector.thermometers.get(i).getResourceURI().equals(t.getResourceURI())){
					Collector.thermometers.remove(i);
					Collector.alarms.remove(i);
					break;
				}
			}
		}
	}
}