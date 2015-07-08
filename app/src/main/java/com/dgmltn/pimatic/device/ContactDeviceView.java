package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
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
public class ContactDeviceView extends DeviceView {

	@InjectView(android.R.id.text1)
	TextView vText;

	@InjectView(R.id.state)
	TextView vState;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("contact");
		}

		@Override
		public DeviceView create(Context context) {
			return new ContactDeviceView(context);
		}
	};

	public ContactDeviceView(Context context) {
		super(context);
		init();
	}

	public ContactDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ContactDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(21)
	public ContactDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(R.layout.view_contact_device, this);
		ButterKnife.inject(v);
	}

	@Subscribe
	public void otto(Events.DeviceChanged e) {
		super.otto(e);
	}

	@Override
	public void bind() {
		vText.setText(device.name);
		vState.setText(getDeviceState());
	}

	private String getDeviceState() {
		for (DeviceAttribute a : device.attributes) {
			if (a != null && a.name.equals("contact")) {
				if (a.value == null || a.labels == null || a.labels.length < 2) {
					return "";
				}
				int index = a.value.equals("true") ? 0 : 1;
				return a.labels[index];
			}
		}
		return "";
	}
}
