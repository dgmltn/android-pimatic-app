/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dgmltn.pimatic.ui;

import java.util.HashSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceId;
import com.dgmltn.pimatic.model.Group;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.model.Page;

public class PageFragment extends Fragment {

	private static final String ARGUMENT_PAGE = "ARGUMENT_PAGE";

	private DeviceAdapter mAdapter;
	private Page mPage;

	public static PageFragment getInstance(Page p) {
		PageFragment f = new PageFragment();
		Bundle args = new Bundle();
		args.putString(ARGUMENT_PAGE, p.id);
		f.setArguments(args);
		return f;
	}

	public PageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mPage = Model.getInstance().findPage(args.getString(ARGUMENT_PAGE));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RecyclerView rv = (RecyclerView) inflater.inflate(
			R.layout.fragment_page, container, false);
		setupRecyclerView(rv);
		return rv;
	}

	private void setupRecyclerView(RecyclerView recyclerView) {
		Context context = recyclerView.getContext();

		mAdapter = new DeviceAdapter(context);
		Model model = Model.getInstance();

		// A truth table of what devices should be listed on this page
		HashSet<String> presentDevices = new HashSet<>();
		for (DeviceId did : mPage.devices) {
			presentDevices.add(did.deviceId);
		}

		// Now let's add them, ordered by group
		for (Group group : model.groups) {
			boolean addedGroup = false;
			for (String deviceId : group.devices) {
				if (presentDevices.contains(deviceId)) {
					if (!addedGroup) {
						mAdapter.addGroup(group.name);
						addedGroup = true;
					}
					mAdapter.addDevice(model.findDevice(deviceId));
					presentDevices.remove(deviceId);
				}
			}
		}

		// Adding the ungrouped devices
		for (DeviceId deviceId : mPage.devices) {
			boolean addedGroup = false;
			if (presentDevices.contains(deviceId.deviceId)) {
				if (!addedGroup) {
					mAdapter.addGroup("Ungrouped");
					addedGroup = true;
				}
				mAdapter.addDevice(model.findDevice(deviceId.deviceId));
			}
		}

		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		recyclerView.setAdapter(mAdapter);
	}
}
