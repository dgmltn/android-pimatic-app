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
	public boolean discrete;
	public boolean hidden;
	public String name;
	public String value;
	public DeviceAttributeValue[] history;
	public long lastUpdate;
}
