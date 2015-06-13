package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceAttributeValue implements FromJson {
	public String t;
	public String v;

	@Override
	public boolean from(JSONObject object) {
		t = object.optString("t");
		v = object.optString("v");
		return true;
	}
}
