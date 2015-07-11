package com.dgmltn.pimatic.device;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dgmltn.pimatic.model.Device;

/**
 * Created by doug on 6/7/15.
 */
public class DeviceViewMapper {

	public abstract static class Matcher {

		public abstract boolean matches(Device d);

		public abstract	@LayoutRes int getLayoutResId();

		public DeviceView inflate(ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			@LayoutRes int layoutResId = getLayoutResId();
			return (DeviceView) inflater.inflate(layoutResId, parent, false);
		}
	}

	private static Matcher[] matchers;

	private static void ensureMatchers() {
		if (matchers == null) {
			matchers = new Matcher[] {
				SwitchDeviceView.matcher,
				DimmerDeviceView.matcher,
				PresenceDeviceView.matcher,
				ContactDeviceView.matcher,
				ButtonsDeviceView.matcher,
				ShutterDeviceView.matcher,
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

	public static DeviceView instantiate(ViewGroup parent, int index) {
		return matchers[index].inflate(parent);
	}
}
