package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;
import com.dgmltn.pimatic.util.JSONUtils;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceAttribute implements FromJson {
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


	@Override
	public boolean from(JSONObject object) {
		description = object.optString("description");
		type = object.optString("type");
		unit = object.optString("unit");
		label = object.optString("label");
		acronym = object.optString("acronym");
		discrete = object.optBoolean("discrete");
		name = object.optString("name");
		value = object.optString("value");
		history = JSONUtils.toFromJson(object.optJSONArray("history"), DeviceAttributeValue.class);
		lastUpdate = object.optLong("lastUpdate");
		return true;
	}
}
