package iot.unipi.it;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.CaliforniumLogger;

public class Server extends CoapServer {
	static{
		CaliforniumLogger.disableLogging();
	} 
	public void startServer() {
		System.out.println("Server started...");
		this.add(new RegistrationResource("registration"));
		this.start();
	}
}
