package com.dgmltn.pimatic.util;

import java.lang.reflect.Array;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by doug on 6/1/15.
 */
public class JSONUtils {

	public static <T extends FromJson> T[] toFromJson(JSONArray arr, Class<T> c) {
		if (arr != null) {
			int length = arr.length();
			final T[] data = (T[]) Array.newInstance(c, length);
			for (int i = 0; i < length; i++) {
				Object v;
				try {
					v = arr.get(i);
				}
				catch (JSONException e) {
					v = null;
				}
				data[i] = toFromJson((JSONObject) v, c);
			}
			return data;
		}
		return null;
	}

	public static <T extends FromJson> T toFromJson(JSONObject obj, Class<T> c) {
		if (obj != null) {
			try {
				FromJson jsonable = c.newInstance();
				if (jsonable.from(obj)) {
					return c.cast(jsonable);
				}
			}
			catch (Exception e) {
				Timber.e("Could not convert JSONObject to FromJson of type \"" + c + "\": " + e);
			}
		}

		return null;
	}

	public static String[] toStringArray(JSONArray arr) {
		if (arr != null) {
			int length = arr.length();
			final String[] data = new String[length];
			for (int i = 0; i < length; i++) {
				data[i] = arr.optString(i);
			}
			return data;
		}
		return null;
	}
}
