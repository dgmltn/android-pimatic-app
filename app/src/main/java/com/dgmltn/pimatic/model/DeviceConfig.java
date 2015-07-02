package com.dgmltn.pimatic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceConfig {
	public String id;

	@SerializedName("class")
	public String clazz;

	public String name;
	public DeviceButton[] buttons;
}
