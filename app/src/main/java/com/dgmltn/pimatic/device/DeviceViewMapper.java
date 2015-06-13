package com.dgmltn.pimatic.device;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import android.content.Context;

import com.dgmltn.pimatic.model.Device;

import timber.log.Timber;

/**
 * Created by doug on 6/7/15.
 */
public class DeviceViewMapper {

	public interface Matcher {
		boolean matches(Device d);
	}

	// TODO: let's not use reflection if possible
	private static class Item {
		public Matcher matcher;
		public Class<? extends DeviceView> clazz;

		public Item(Class<? extends DeviceView> c, Matcher m) {
			matcher = m;
			clazz = c;
		}
	}

	private static ArrayList<Item> matchers;

	private static void ensureMatchers() {
		if (matchers == null) {
			matchers = new ArrayList<>();

			register(SwitchDeviceView.class, new DeviceViewMapper.Matcher() {
				@Override
				public boolean matches(Device d) {
					return d.template.equals("switch");
				}
			});
		}
	}

	public static void register(Class<? extends DeviceView> clazz, Matcher m) {
		matchers.add(new Item(clazz, m));
	}

	public static Class<? extends DeviceView> get(int index) {
		ensureMatchers();
		return index == matchers.size() ? DefaultDeviceView.class : matchers.get(index).clazz;
	}

	public static int find(Device d) {
		ensureMatchers();
		for (int i = 0; i < matchers.size(); i++) {
			if (matchers.get(i).matcher.matches(d)) {
				return i;
			}
		}
		return matchers.size();
	}

	public static DeviceView instantiate(Context context, int index) {
		Class<? extends DeviceView> clazz = get(index);
		try {
			Constructor<? extends DeviceView> constructor = clazz.getConstructor(Context.class);
			DeviceView v = constructor.newInstance(context);
			return v;
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}
		return null;
	}
}
