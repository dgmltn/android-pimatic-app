package com.dgmltn.pimatic.model;

import android.support.annotation.Nullable;

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

	@Nullable
	public DeviceAttribute findAttribute(String name) {
		if (name != null) {
			for (DeviceAttribute a : attributes) {
				if (a != null && name.equals(a.name)) {
					return a;
				}
			}
		}
		return null;
	}
}
