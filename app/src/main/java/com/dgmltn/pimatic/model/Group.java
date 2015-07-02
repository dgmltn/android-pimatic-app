package com.dgmltn.pimatic.model;

/**
 * Created by doug on 6/2/15.
 */
public class Group {
	public String id;
	public String name;
	public String[] devices;
	public String[] rules;
	public String[] variables;

	public boolean containsDevice(String deviceId) {
		for (String d : devices) {
			if (d == null && deviceId == null || d != null && d.equals(deviceId)) {
				return true;
			}
		}
		return false;
	}
}
