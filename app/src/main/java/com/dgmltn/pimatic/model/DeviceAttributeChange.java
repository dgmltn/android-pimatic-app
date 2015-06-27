package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;
import com.dgmltn.pimatic.util.JSONUtils;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceAttributeChange implements FromJson {
	public String deviceId;
	public String attributeName;
	public long time;
	public String value;


	@Override
	public boolean from(JSONObject object) {
		deviceId = object.optString("deviceId");
		attributeName = object.optString("attributeName");
		value = object.optString("value");
		time = object.optLong("time");
		return true;
	}
}
