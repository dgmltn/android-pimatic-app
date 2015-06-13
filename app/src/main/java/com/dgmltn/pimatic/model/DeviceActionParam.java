package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;

/**
 * Created by doug on 5/31/15.
 *
 * "params":{
 *     "dimlevel":{
 *         "type":"number"
 *     }
 * }
 */
public class DeviceActionParam implements FromJson {
	public String name;
	public String type;

	public DeviceActionParam(String paramName) {
		name = paramName;
	}

	@Override
	public boolean from(JSONObject object) {
		type = object.optString("type");
		return true;
	}
}
