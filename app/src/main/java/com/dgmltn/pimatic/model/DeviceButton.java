package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceButton implements FromJson {
	public String id;
	public String text;

	@Override
	public boolean from(JSONObject object) {
		id = object.optString("id");
		text = object.optString("text");
		return true;
	}
}
