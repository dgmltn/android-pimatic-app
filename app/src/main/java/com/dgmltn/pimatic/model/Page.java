package com.dgmltn.pimatic.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.dgmltn.pimatic.util.FromJson;
import com.dgmltn.pimatic.util.JSONUtils;

/**
 * Created by doug on 6/1/15.
 */
public class Page implements FromJson {
	public String id;
	public String name;
	public DeviceId[] devices;

	@Override
	public boolean from(JSONObject object) {
		id = object.optString("id");
		name = object.optString("name");
		devices = JSONUtils.toFromJson(object.optJSONArray("devices"), DeviceId.class);
		return true;
	}
}
