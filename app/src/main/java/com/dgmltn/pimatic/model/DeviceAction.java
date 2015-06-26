package com.dgmltn.pimatic.model;

import java.util.Iterator;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;

/**
 * Created by doug on 5/31/15.
 */
public class DeviceAction implements FromJson {
	public String description;
	public DeviceActionParam[] params;
	public String name;

	@Override
	public boolean from(JSONObject object) {
		description = object.optString("description");

		JSONObject jsonParams = object.optJSONObject("params");
		if (jsonParams != null) {
			params = new DeviceActionParam[jsonParams.length()];
			Iterator<String> iterator = object.keys();
			int i = 0;
			while (iterator.hasNext()) {
				String paramName = iterator.next();
				if (paramName != null) {
					JSONObject paramObject = object.optJSONObject(paramName);
					if (paramObject != null) {
						DeviceActionParam p = new DeviceActionParam(paramName);
						p.from(paramObject);
						params[i++] = p;
					}
				}
			}
		}

		name = object.optString("name");
		return true;
	}
}
