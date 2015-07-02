package com.dgmltn.pimatic.model;

/**
 * Created by doug on 5/31/15.
 */
public class Device {
	public String id;
	public String name;
	public String template;
	public DeviceAttribute[] attributes;
	public DeviceAction[] actions;
	public DeviceConfig config;
	public DeviceConfig configDefaults;
}
