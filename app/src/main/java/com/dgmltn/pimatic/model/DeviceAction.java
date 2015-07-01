package com.dgmltn.pimatic.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceAction implements FromJson {
	public String description;
	public Map<String, DeviceActionParam> params;
	public String name;

	@Override
	public boolean from(JSONObject object) {
		description = object.optString("description");

		JSONObject jsonParams = object.optJSONObject("params");
		if (jsonParams != null) {
			params = new HashMap<>();
			Iterator<String> iterator = object.keys();
			while (iterator.hasNext()) {
				String paramName = iterator.next();
				if (paramName != null) {
					JSONObject paramObject = object.optJSONObject(paramName);
					if (paramObject != null) {
						DeviceActionParam p = new DeviceActionParam();
						p.from(paramObject);
						params.put(paramName, p);
					}
				}
			}
		}

		name = object.optString("name");
		return true;
	}
}
