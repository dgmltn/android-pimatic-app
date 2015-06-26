package com.dgmltn.pimatic.model;

import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.network.Network;
import com.dgmltn.pimatic.util.Events;

/**
 * Created by doug on 6/2/15.
 */
public class Model {
	//TODO: public Variable[] variables;

	private static Model sInstance = new Model();

	private Model() {
	}

	public static Model getInstance() {
		return sInstance;
	}

	///////////////////////////////////////////////////////////////////////////
	// Network and ConnectionOptions
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
	// Groups
	///////////////////////////////////////////////////////////////////////////

	private Group[] groups;

	public void setGroups(Group[] groups) {
		this.groups = groups;
	}

	public Group[] getGroups() {
		return groups;
	}

	///////////////////////////////////////////////////////////////////////////
	// Pages
	///////////////////////////////////////////////////////////////////////////

	private Page[] pages;

	public void setPages(Page[] pages) {
		this.pages = pages;
		Events.post(new Events.PagesChanged());
	}

	public Page[] getPages() {
		return pages;
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

	///////////////////////////////////////////////////////////////////////////
	// Devices
	///////////////////////////////////////////////////////////////////////////

	private Device[] devices;

	public void setDevices(Device[] devices) {
		this.devices = devices;
		Events.post(new Events.DevicesChanged());
	}

	public Device[] getDevices() {
		return devices;
	}

	public Device findDevice(String id) {
		for (Device d : devices) {
			if (id.equals(d.id)) {
				return d;
			}
		}
		return null;
	}
}
