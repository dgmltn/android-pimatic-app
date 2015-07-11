/*
 * Copyright (C) 2015 Doug Melton
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

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.device.DeviceView;
import com.dgmltn.pimatic.device.DeviceViewMapper;
import com.dgmltn.pimatic.model.Device;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int VIEW_TYPE_GROUP = 0;

	private LayoutInflater inflater;
	private ArrayList<Object> mItems;
	private final TypedValue mTypedValue = new TypedValue();
	private int mBackground;

	public DeviceAdapter(Context context) {
		super();
		inflater = LayoutInflater.from(context);
		mItems = new ArrayList<>();
		setHasStableIds(false);

		context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
		mBackground = mTypedValue.resourceId;
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		Object item = getItem(position);
		if (item instanceof Device) {
			Device d = (Device) item;
			//TODO: cache this result
			int viewType = DeviceViewMapper.find(d) + 1;
			return viewType;
		}

		return VIEW_TYPE_GROUP;
	}

	public Object getItem(int position) {
		return mItems.get(position);
	}

	public void clear() {
		int count = mItems.size();
		mItems.clear();
		notifyItemRangeRemoved(0, count);
	}

	public void addGroup(String name) {
		mItems.add(name);
		notifyItemChanged(mItems.size() - 1);
	}

	public void addDevice(Device item) {
		mItems.add(item);
		notifyItemChanged(mItems.size() - 1);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return viewType == VIEW_TYPE_GROUP
			? new GroupViewHolder(parent)
			: new DeviceViewHolder(parent, viewType);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof DeviceViewHolder) {
			Device d = (Device) getItem(position);
			DeviceView v = (DeviceView) holder.itemView;
			v.setDevice(d);
		}
		else if (holder instanceof GroupViewHolder) {
			String s = (String) getItem(position);
			GroupViewHolder g = (GroupViewHolder) holder;
			g.text.setText(s);
		}
	}

	class DeviceViewHolder extends RecyclerView.ViewHolder {

		public DeviceViewHolder(ViewGroup parent, int viewType) {
			super(DeviceViewMapper.instantiate(parent, viewType - 1));
		}
	}

	class GroupViewHolder extends RecyclerView.ViewHolder {
		@InjectView(android.R.id.text1)
		TextView text;

		public GroupViewHolder(ViewGroup parent) {
			super(inflater.inflate(R.layout.row_group, parent, false));

			ButterKnife.inject(this, itemView);
		}
	}
}
