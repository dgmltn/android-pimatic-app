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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.device.DeviceViewMapper;
import com.dgmltn.pimatic.model.Message;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

	private ArrayList<Message> mMessages;

	public MessageAdapter(Context context) {
		super();
		mMessages = new ArrayList<>();
		setHasStableIds(false);
	}

	@Override
	public int getItemCount() {
		return mMessages.size();
	}

	public Message getItem(int position) {
		return mMessages.get(position);
	}

	public void clear() {
		int count = mMessages.size();
		mMessages.clear();
		notifyItemRangeRemoved(0, count);
	}

	public void add(Message message) {
		mMessages.add(message);
		notifyItemChanged(mMessages.size() - 1);
	}

	public void set(Message[] messages) {
		clear();
		for (Message m : messages) {
			add(m);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message, null);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.bind(getItem(position));
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.time)
		protected TextView time;

		@Bind(R.id.icon)
		protected ImageView icon;

		@Bind(R.id.text)
		protected TextView text;

		@Bind(R.id.tags)
		protected TextView tags;

		public ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}

		protected void bind(Message message) {
			time.setText(Long.toString(message.time));
			if (TextUtils.isEmpty(message.text)) {
				text.setVisibility(View.GONE);
			}
			else {
				text.setVisibility(View.VISIBLE);
				text.setText(message.text);
			}
			if (message.tags == null || message.tags.size() == 0) {
				tags.setVisibility(View.GONE);
			}
			else {
				tags.setVisibility(View.VISIBLE);
				tags.setText(message.tags.get(0));
			}
			icon.setImageResource(
				message.level.equals(Message.INFO) ? R.drawable.ic_info
					: message.level.equals(Message.ERROR) ? R.drawable.ic_error
						: message.level.equals(Message.WARN) ? R.drawable.ic_warning
							: message.level.equals(Message.DEBUG) ? R.drawable.ic_bug_report
								: 0
			);

		}
	}
}
