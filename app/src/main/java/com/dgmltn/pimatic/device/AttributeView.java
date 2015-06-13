package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.wefika.flowlayout.FlowLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by doug on 6/5/15.
 */
public class AttributeView extends LinearLayout {

	private DeviceAttribute mAttribute;

	@InjectView(R.id.label)
	TextView vLabel;

	@InjectView(R.id.value)
	TextView vValue;

	@InjectView(R.id.unit)
	TextView vUnit;

	public AttributeView(Context context) {
		super(context);
		init();
		onFinishInflate();
	}

	public AttributeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		onFinishInflate();
	}

	@TargetApi(11)
	public AttributeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(21)
	public AttributeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_attribute, this);
		FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.inject(this);
		populate();
	}

	public void setAttribute(DeviceAttribute attribute) {
		mAttribute = attribute;
		populate();
	}

	private void populate() {
		if (mAttribute != null && vLabel != null) {
			vLabel.setText(!TextUtils.isEmpty(mAttribute.acronym) ? mAttribute.acronym : mAttribute.label);
			vValue.setText(mAttribute.value);
			vUnit.setText(mAttribute.unit);
		}
	}
}
