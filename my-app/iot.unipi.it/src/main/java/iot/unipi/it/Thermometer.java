package iot.unipi.it;

import java.util.ArrayList;

public class Thermometer extends Resource {
	private ArrayList<Integer> temperatures = new ArrayList<Integer>();

	public Thermometer(String address, String path) {
		super(address, path);
	}

	public ArrayList<Integer> getTemperatureValues() {
		return this.temperatures;
	}

	public void setTemperatureValues(ArrayList<Integer> list) {
		this.temperatures = list;
	}
	
	public Integer getTemperatureValue(int i) {
		if (i < temperatures.size())
			return temperatures.get(i);
		return null;
	}

}