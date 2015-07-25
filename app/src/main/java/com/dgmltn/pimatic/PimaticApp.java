package com.dgmltn.pimatic;

import android.app.Application;

import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.network.Network;
import com.dgmltn.pimatic.util.Events;

import timber.log.Timber;

/**
 * Created by doug on 6/1/15.
 */
public class PimaticApp extends Application {

	/////////////////////////////////////////////////////////////////////////
	// Application lifecycle
	/////////////////////////////////////////////////////////////////////////

	@Override public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}
		else {
			// Do not plant a Timber tree if not debug
		}
	}

	/////////////////////////////////////////////////////////////////////////
	// Controller
	/////////////////////////////////////////////////////////////////////////

	private static Model sModel = new Model();
	private static Network sNetwork = null;

	public static Model getModel() {
		return sModel;
	}

	public static void configureNetwork(ConnectionOptions cxn) {
		if (sNetwork == null || !sNetwork.getConnection().equals(cxn)) {
			sModel.reset();
		}

		if (sNetwork != null) {
			sNetwork.teardown();
			sNetwork = null;
		}

		sNetwork = new Network(cxn);
		Events.post(new Events.NetworkChanged());
	}

	public static Network getNetwork() {
		return sNetwork;
	}

}
