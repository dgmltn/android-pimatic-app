package com.dgmltn.pimatic.util;

import android.os.Handler;
import android.os.Looper;

import com.dgmltn.pimatic.model.Device;
import com.squareup.otto.Bus;
import timber.log.Timber;

/**
 * This singleton class houses a list of all the events currently supported on the Otto Event Bus.
 * <p>
 * Otto is a Guava-based event bus system that uses annotations and code generation.
 * Basically, it saves us from needing to create a bunch of interfaces and register them between various parts of our code.
 * Instead, all we need to do is register with the singleton bus, and subscribe to events.
 * </p>
 * <p>
 * For more information on Otto https://github.com/square/otto
 * </p>
 * <p>
 * <pre>
 * <code>
 * //To publish a new event, call the post method
 * bus.post(new Event());
 *
 * //To listen for the event published
 * (annotate)Subscribe
 * public void listenToEvent(Event event) {
 * // Do something
 * }
 *
 * //In order to receive events, a class instance needs to register with the bus.
 * //Best practice to register in the onResume() lifecycle event.
 * bus.register(this);
 *
 * //In order to unregister from the event bus,
 * bus.unregister(this);
 *
 * <strong>Note:</strong>
 * Always unregister in the onPause() to ensure that events are not consumed when not needed.
 * </code>
 * </p>
 */
public class Events {
	private static final Bus sBus = new BetterBus();

	private Events() {
		// Singleton - Cannot be instantiated
	}

	public static void register(Object obj) {
		Timber.v("Registering: " + obj);
		sBus.register(obj);
	}

	public static void unregister(Object obj) {
		Timber.v("Unregistering: " + obj);
		sBus.unregister(obj);
	}

	public static void post(Object obj) {
		Timber.v("Posting event: " + obj);
		sBus.post(obj);
	}

	private static class BetterBus extends Bus {
		private static final Handler mHandler = new Handler(Looper.getMainLooper());

		@Override
		public void register(final Object listener) {
			if (Looper.myLooper() == Looper.getMainLooper()) {
				BetterBus.super.register(listener);
			}
			else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						BetterBus.super.register(listener);
					}
				});
			}
		}

		@Override
		public void unregister(final Object listener) {
			if (Looper.myLooper() == Looper.getMainLooper()) {
				BetterBus.super.unregister(listener);
			}
			else {

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						BetterBus.super.unregister(listener);
					}
				});
			}
		}

		@Override
		public void post(final Object event) {
			if (Looper.myLooper() == Looper.getMainLooper()) {
				BetterBus.super.post(event);
			}
			else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						BetterBus.super.post(event);
					}
				});
			}
		}
	}

	// Events

	public static final class AccountsChanged {
	}

	public static final class DevicesChanged {
	}

	public static final class PagesChanged {
	}

	public static final class GroupsChanged {
	}

	public static final class RulesChanged {
	}

	public static final class VariablesChanged {
	}

	public static final class DeviceChanged {
		public String deviceId;
		public DeviceChanged(String deviceId) {
			this.deviceId = deviceId;
		}
	}

	public static final class DesiredGroupTab {
		public int tab;
		public DesiredGroupTab(int tab) {
			this.tab = tab;
		}
	}
}
