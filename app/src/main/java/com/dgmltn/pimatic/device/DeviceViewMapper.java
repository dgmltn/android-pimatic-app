package com.dgmltn.pimatic.device;

import android.content.Context;

import com.dgmltn.pimatic.model.Device;

/**
 * Created by doug on 6/7/15.
 */
public class DeviceViewMapper {

	public interface Matcher {
		boolean matches(Device d);
		DeviceView create(Context context);
	}

	private static Matcher[] matchers;

	private static void ensureMatchers() {
		if (matchers == null) {
			matchers = new Matcher[] {
				SwitchDeviceView.matcher,
				DimmerDeviceView.matcher,
				PresenceDeviceView.matcher,
				ButtonsDeviceView.matcher,
				DefaultDeviceView.matcher
			};
		}
	}

	public static int find(Device d) {
		ensureMatchers();
		for (int i = 0; i < matchers.length; i++) {
			if (matchers[i].matches(d)) {
				return i;
			}
		}
		return -1;
	}

	public static DeviceView instantiate(Context context, int index) {
		return matchers[index].create(context);
	}
}
