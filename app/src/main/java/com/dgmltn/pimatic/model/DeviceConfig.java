package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;
import com.dgmltn.pimatic.util.JSONUtils;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceConfig implements FromJson {
	public String id;
	public String clazz;
	public String name;
	public DeviceButton[] buttons;

	@Override
	public boolean from(JSONObject object) {
		id = object.optString("id");
		clazz = object.optString("class");
		name = object.optString("name");
		buttons = JSONUtils.toFromJson(object.optJSONArray("buttons"), DeviceButton.class);
		return true;
	}
}
