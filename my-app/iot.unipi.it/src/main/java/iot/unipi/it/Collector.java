package iot.unipi.it;

import java.sql.SQLException;
import java.util.ArrayList;

public class Collector {
	
	//DB CONFIG
	private static String DB_URL = "jdbc:mysql://localhost/ahmed?serverTimezone=UTC";
	private static String table = "patients";
	private static String tableAttributes[] = {"id", "URI", "timestamp", "value", "state"};
	
	//RESOURCE STORAGE
	protected static ArrayList<Thermometer> thermometers = new ArrayList<Thermometer>();
	protected static ArrayList<Alarm> alarms = new ArrayList<Alarm>();
	
	//DB CONTROLLER
	protected static DBControl db; 
	
	//MQTT CLIENT
	private static CollectorMqttClient mc = null;
	
	//START THREAD FOR THE SERVER THAT CREATES THE CoAP OBSERVER CLIENTS
	public static void runServer() {
		new Thread() {
			public void run() {
				CollectorServer server = new CollectorServer();
				server.startServer();
			}
		}.start();
	}
	
	public static void createMqttClient() throws InterruptedException{
			mc = new CollectorMqttClient();
	}
	
	
	public static void main(String[] args) throws SQLException, InterruptedException{
		db = new DBControl(DB_URL, "root", "root", table, tableAttributes);
		runServer();
		createMqttClient();
	}
		
}