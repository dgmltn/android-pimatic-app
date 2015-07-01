package com.dgmltn.pimatic.model;

import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.network.Network;
import com.dgmltn.pimatic.util.Events;

import timber.log.Timber;

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
			getNetwork();
		}
	}

	public void deconfigureNetwork() {
		if (network != null) {
			network.teardown();
			network = null;
		}
	}

	public Network getNetwork() {
		if (network == null && connection != null) {
			network = new Network(connection);
		}
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
		Events.post(new Events.GroupsChanged());
	}

	public Group[] getGroups() {
		return groups;
	}

	public boolean hasGroups() {
		return groups != null;
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

	public boolean hasPages() {
		return pages != null;
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

	public void updateDevice(DeviceAttributeChange change) {
		Device device = findDevice(change.deviceId);
		for (DeviceAttribute attribute : device.attributes) {
			if (attribute.name.equals(change.attributeName) && attribute.lastUpdate < change.time) {
				attribute.lastUpdate = change.time;
				attribute.value = change.value;
				Events.post(new Events.DeviceChanged(device.id));
				Timber.d("Attribute: " + device.name + "." + attribute.name + " = " + attribute.value);
				break;
			}
		}
	}

	public Device[] getDevices() {
		return devices;
	}

	public boolean hasDevices() {
		return devices != null;
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
