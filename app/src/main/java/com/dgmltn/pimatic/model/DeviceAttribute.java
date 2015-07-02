package com.dgmltn.pimatic.model;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceAttribute {
	public String description;
	public String type;
	public String unit;
	public String label;
	public String acronym;
	boolean discrete;
	public String name;
	public String value;
	public DeviceAttributeValue[] history;
	long lastUpdate;
}
