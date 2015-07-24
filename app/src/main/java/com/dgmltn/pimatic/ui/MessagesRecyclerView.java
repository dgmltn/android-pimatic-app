package com.dgmltn.pimatic.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Displays a list of Device that are associated with a given Page. This is a RecyclerView
 */
public class MessagesRecyclerView extends RecyclerView {

	private MessageAdapter mAdapter;

	public MessagesRecyclerView(Context context) {
		super(context);
		init(context);
	}

	public MessagesRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MessagesRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mAdapter = new MessageAdapter(context);
		setLayoutManager(new LinearLayoutManager(context));
		setAdapter(mAdapter);
	}

	public MessageAdapter getAdapter() {
		return mAdapter;
	}
}
