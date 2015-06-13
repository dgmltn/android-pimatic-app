package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;
import com.dgmltn.pimatic.util.JSONUtils;

/**
 * Created by doug on 6/2/15.
 */
public class Group implements FromJson {
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
	@Override
	public boolean from(JSONObject object) {
		id = object.optString("id");
		name = object.optString("name");
		devices = JSONUtils.toStringArray(object.optJSONArray("devices"));
		rules = JSONUtils.toStringArray(object.optJSONArray("rules"));
		variables = JSONUtils.toStringArray(object.optJSONArray("variables"));
		return true;
	}
}
