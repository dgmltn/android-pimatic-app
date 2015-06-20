package com.dgmltn.pimatic.model;

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
