package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by doug on 6/7/15.
 */
public class PresenceDeviceView extends DeviceView {

	@InjectView(R.id.device_name)
	TextView vName;

	@InjectView(R.id.device_content)
	View vContent;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("presence");
		}

		@Override
		public @LayoutRes int getLayoutResId() {
			return R.layout.view_presence_device;
		}
	};

	public PresenceDeviceView(Context context) {
		super(context);
	}

	public PresenceDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PresenceDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public PresenceDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.inject(this);
	}

	@Subscribe
	public void otto(Events.DeviceChanged e) {
		super.otto(e);
	}

	@Override
	public void bind() {
		vName.setText(device.name);
		vContent.setEnabled(getDeviceState());
	}

	private boolean getDeviceState() {
		for (DeviceAttribute a : device.attributes) {
			if (a != null && a.name.equals("presence")) {
				return a.value.equals("true");
			}
		}
		return false;
	}
}
