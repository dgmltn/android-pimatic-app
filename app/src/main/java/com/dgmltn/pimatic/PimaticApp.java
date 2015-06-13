package com.dgmltn.pimatic;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by doug on 6/1/15.
 */
public class PimaticApp extends Application {
	@Override public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}
		else {
			// Do not plant a Timber tree if not debug
		}
	}
}
