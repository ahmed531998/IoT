package iot.unipi.it;

import java.net.InetAddress;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class ResourceRegistrationHandler extends CoapResource {

	public ResourceRegistrationHandler(String name) {
		super(name);
	}

	public void handleGET(CoapExchange exchange) {
		exchange.accept();

		InetAddress inetAddress = exchange.getSourceAddress();
		CoapClient client = new CoapClient("coap://[" + inetAddress.getHostAddress() + "]:5683/.well-known/core");
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

			if (path.contains("temperature")) {
				Thermometer t = new Thermometer(inetAddress.getHostAddress(), path);
				if (!Collector.thermometers.contains(t)) {
					Collector.thermometers.add(t);
					observe(t);
				}
			} else if (path.contains("alarm")) {
				Alarm a = new Alarm(inetAddress.getHostAddress(), path);
				if (!Collector.alarms.contains(a))
					Collector.alarms.add(a);
			}
		}
	}

	private static void observe(Thermometer t) {
		CollectorCoapClient observer = new CollectorCoapClient(t);
		observer.startObserving();
	}
}