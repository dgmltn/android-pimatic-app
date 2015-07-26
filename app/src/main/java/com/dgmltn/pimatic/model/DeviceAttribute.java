package com.dgmltn.pimatic.model;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceAttribute {
	public String description;
	public String type;
	public String unit;
	public String label;
	public String[] labels;
	public String acronym;
	public boolean discrete;
	public boolean hidden;
	public String name;
	public String value;
	public DeviceAttributeValue[] history;
	public long lastUpdate;

	public int valueAsInt(int defaultValue) {
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
