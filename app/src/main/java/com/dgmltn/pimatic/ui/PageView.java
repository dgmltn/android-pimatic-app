package com.dgmltn.pimatic.ui;

import java.util.HashSet;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.dgmltn.pimatic.model.DeviceId;
import com.dgmltn.pimatic.model.Group;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.model.Page;

import timber.log.Timber;

/**
 * Displays a list of Device that are associated with a given Page. This is a RecyclerView
 */
public class PageView extends RecyclerView {

	private Model mModel;
	private Page mPage;
	private DeviceAdapter mAdapter;

	public PageView(Context context) {
		super(context);
		init(context);
	}

	public PageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mAdapter = new DeviceAdapter(getContext());
		setLayoutManager(new LinearLayoutManager(context));
		setAdapter(mAdapter);
	}

	public void setPage(Model model, Page page) {
		mModel = model;
		mPage = page;
		mAdapter.clear();
		bind();
	}

	public Page getPage() {
		return mPage;
	}

	private void bind() {
		if (mPage == null || mModel == null) {
			Timber.e("cannot bind()");
			return;
		}

		// A truth table of what devices should be listed on this page
		HashSet<String> presentDevices = new HashSet<>();
		for (DeviceId did : mPage.devices) {
			presentDevices.add(did.deviceId);
		}

		// Now let's add them, ordered by group
		for (Group group : mModel.groups) {
			boolean addedGroup = false;
			for (String deviceId : group.devices) {
				if (presentDevices.contains(deviceId)) {
					if (!addedGroup) {
						mAdapter.addGroup(group.name);
						addedGroup = true;
					}
					mAdapter.addDevice(mModel.findDevice(deviceId));
					presentDevices.remove(deviceId);
				}
			}
		}

		// Adding the ungrouped devices
		boolean addedGroup = false;
		for (DeviceId deviceId : mPage.devices) {
			if (presentDevices.contains(deviceId.deviceId)) {
				if (!addedGroup) {
					mAdapter.addGroup("Ungrouped");
					addedGroup = true;
				}
				mAdapter.addDevice(mModel.findDevice(deviceId.deviceId));
			}
		}
	}

}
