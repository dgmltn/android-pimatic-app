package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doug on 6/7/15.
 */
public class ContactDeviceView extends DeviceView {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.device_content)
	TextView vContent;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("contact");
		}

		@Override
		public int getLayoutResId() {
			return R.layout.view_contact_device;
		}
	};

	public ContactDeviceView(Context context) {
		super(context);
	}

	public ContactDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContactDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public ContactDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
	}

	@Subscribe
	public void otto(Events.DeviceChanged e) {
		super.otto(e);
	}

	@Override
	public void bind() {
		vName.setText(device.name);
		vContent.setText(getDeviceState());
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
