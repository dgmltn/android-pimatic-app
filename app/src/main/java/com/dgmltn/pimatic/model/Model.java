package com.dgmltn.pimatic.model;

import com.dgmltn.pimatic.util.Events;

import timber.log.Timber;

/**
 * Created by doug on 6/2/15.
 */
public class Model {

	public Model() {
	}

	public void reset() {
		groups = null;
		pages = null;
		devices = null;
		messages = null;
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
		if (device == null) {
			// Device not found... throw this away
			return;
		}
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
		if (devices != null) {
			for (Device d : devices) {
				if (id.equals(d.id)) {
					return d;
				}
			}
		}
		return null;
	}

	///////////////////////////////////////////////////////////////////////////
	// Rules
	///////////////////////////////////////////////////////////////////////////

	private Rule[] rules;

	public void setRules(Rule[] rules) {
		this.rules = rules;
		Events.post(new Events.RulesChanged());
	}

	public Rule[] getRules() {
		return rules;
	}

	///////////////////////////////////////////////////////////////////////////
	// Variables
	///////////////////////////////////////////////////////////////////////////

	private Variable[] variables;

	public void setVariables(Variable[] variables) {
		this.variables = variables;
		Events.post(new Events.VariablesChanged());
	}

	public Variable[] getVariables() {
		return variables;
	}

	///////////////////////////////////////////////////////////////////////////
	// Messages
	///////////////////////////////////////////////////////////////////////////

	private Message[] messages;

	public void setMessages(Message[] messages) {
		this.messages = messages;
		Events.post(new Events.MessagesChanged());
	}

	public Message[] getMessages() {
		return messages;
	}

}
