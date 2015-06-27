package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by doug on 6/6/15.
 */
public class DefaultDeviceView extends DeviceView {

	@InjectView(android.R.id.text1)
	TextView vText;

	@InjectView(R.id.template)
	TextView vTemplate;

	@InjectView(R.id.attributes)
	ViewGroup vAttributes;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return true;
		}

		@Override
		public DeviceView create(Context context) {
			return new DefaultDeviceView(context);
		}
	};

	public DefaultDeviceView(Context context) {
		super(context);
		init();
	}

	public DefaultDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DefaultDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(21)
	public DefaultDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(R.layout.view_default_device, this);
		ButterKnife.inject(v);
		if (device != null) {
			bind();
		}
	}

	@Subscribe
	public void otto(Events.DeviceChanged e) {
		super.otto(e);
	}

	public void bind() {
		vText.setText(device.name);
		vTemplate.setText(device.template);
		vAttributes.removeAllViews();
		for (DeviceAttribute attr : device.attributes) {
			AttributeView v = new AttributeView(getContext());
			v.setAttribute(attr);
			vAttributes.addView(v);
		}

	}

}
