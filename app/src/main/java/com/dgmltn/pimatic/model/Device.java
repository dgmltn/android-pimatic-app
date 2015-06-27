package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import com.dgmltn.pimatic.util.FromJson;
import com.dgmltn.pimatic.util.JSONUtils;

import timber.log.Timber;

/**
 * Created by doug on 5/31/15.
 */
public class Device implements FromJson {
	public String id;
	public String name;
	public String template;
	public DeviceAttribute[] attributes;
	public DeviceAction[] actions;
	public DeviceConfig config;
	public DeviceConfig configDefaults;

	@Override
	public boolean from(JSONObject object) {
		Timber.d("Device: " + object.toString());
		id = object.optString("id");
		name = object.optString("name");
		template = object.optString("template");
		attributes = JSONUtils.toFromJson(object.optJSONArray("attributes"), DeviceAttribute.class);
		actions = JSONUtils.toFromJson(object.optJSONArray("actions"), DeviceAction.class);
		config = JSONUtils.toFromJson(object.optJSONObject("config"), DeviceConfig.class);
		configDefaults = JSONUtils.toFromJson(object.optJSONObject("configDefaults"), DeviceConfig.class);
		return true;
	}
}
