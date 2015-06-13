package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;

/**
 * Created by doug on 6/1/15.
 */
public class DeviceId implements FromJson {
	public String deviceId;

	@Override
	public boolean from(JSONObject object) {
		deviceId = object.optString("deviceId");
		return true;
	}
}
