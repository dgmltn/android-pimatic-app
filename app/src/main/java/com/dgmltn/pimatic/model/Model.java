package com.dgmltn.pimatic.model;

import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.network.Network;

/**
 * Created by doug on 6/2/15.
 */
public class Model {
	public Group[] groups;
	public Page[] pages;
	public Device[] devices;
	//public Variable[] variables;

	private static Model sInstance = new Model();

	private Model() {
	}

	public static Model getInstance() {
		return sInstance;
	}

	///////////////////////////////////////////////////////////////////////////
	// Handle changing the network connection carefully
	///////////////////////////////////////////////////////////////////////////

	private ConnectionOptions connection;
	private Network network;

	public void configureNetwork(ConnectionOptions connection) {
		if (connection != null && !connection.equals(this.connection)) {
			groups = null;
			pages = null;
			devices = null;
			this.connection = connection;
			if (network != null) {
				network.teardown();
			}
			network = new Network(connection);
		}
	}

	public void deconfigureNetwork() {
		if (network != null) {
			network.teardown();
			network = null;
		}
	}

	public Network getNetwork() {
		return network;
	}

	public ConnectionOptions getConnection() {
		return connection;
	}

	///////////////////////////////////////////////////////////////////////////

	public Device findDevice(String id) {
		for (Device d : devices) {
			if (id.equals(d.id)) {
				return d;
			}
		}
		return null;
	}

	public Page findPage(String id) {
		if (pages == null) {
			return null;
		}
		for (Page p : pages) {
			if (id.equals(p.id)) {
				return p;
			}
		}
		return null;
	}
}
