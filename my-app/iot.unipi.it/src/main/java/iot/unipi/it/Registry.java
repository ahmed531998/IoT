package iot.unipi.it;

import java.net.InetAddress;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class Registry extends CoapResource {

	public Registry(String name) {
		super(name);
	}

	public void handleGET(CoapExchange exchange) {
		exchange.accept();

		//resource discovery
		InetAddress sensorAddress = exchange.getSourceAddress();
		CoapClient client = new CoapClient("coap://[" + sensorAddress.getHostAddress() + "]:5683/.well-known/core");
		CoapResponse response = client.get();
		
		String code = response.getCode().toString();
		if (!code.startsWith("2")) {
			System.out.println("Error: " + code);
			return;
		}

		String responseText = response.getResponseText();
		Integer startIndex = 0, endIndex;

		while (true) {
			startIndex = responseText.indexOf("</");
			if (startIndex == -1)
				break;
			endIndex = responseText.indexOf(">");
			String path = responseText.substring(startIndex + 2, endIndex);
			responseText = responseText.substring(endIndex + 1);
			boolean toAdd = true;
			if (path.contains("temperature")) {
				Thermometer t = new Thermometer(sensorAddress.getHostAddress(), path);
				for(int i = 0; i < Collector.thermometers.size(); i++) {
					if (Collector.thermometers.get(i).getResourceURI().equals(t.getResourceURI())) {
						toAdd = false;
						break;
					}
				}
				if (toAdd) {
					System.out.println("Adding " + t.getResourceURI());
					Collector.thermometers.add(t);
					observe(t);
				}
				else {
					System.out.println("Already added " + t.getResourceURI());
				}
			} else if (path.contains("alarm")) {
				Alarm a = new Alarm(sensorAddress.getHostAddress(), path);
				for(int i = 0; i < Collector.alarms.size(); i++) {
					if (Collector.alarms.get(i).getResourceURI().equals(a.getResourceURI())) {
						toAdd = false;
						break;
					}
				}
				if (toAdd) {
					System.out.println("Adding " + a.getResourceURI());
					Collector.alarms.add(a);
				}
				else {
					System.out.println("Already added " + a.getResourceURI());
				}
			}
		}
	}

	private static void observe(Thermometer t) {
		CollectorCoapClient observer = new CollectorCoapClient(t);
		observer.startObserving();
	}
}
