package iot.unipi.it;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.CaliforniumLogger;

public class CollectorServer extends CoapServer {
	static{
		CaliforniumLogger.disableLogging();
	} 
	public void startServer() {
		System.out.println("Server is up");
		this.add(new ResourceRegistrationHandler("registration"));
		this.start();
	}
}