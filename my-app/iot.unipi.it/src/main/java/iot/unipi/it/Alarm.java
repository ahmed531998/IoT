package iot.unipi.it;

public class Alarm extends Resource {
	private boolean on = false;
	public Alarm(String address, String path) {
		super(address, path);
	}
	public boolean getState() {
		return on;
	}
	public void setState(boolean o) {
		this.on = o;
	}
}
