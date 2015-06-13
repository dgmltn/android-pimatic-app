package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.util.Network;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by doug on 6/7/15.
 */
public class SwitchDeviceView extends DeviceView {

	@InjectView(android.R.id.text1)
	TextView vText;

	@InjectView(R.id.switch_view)
	Switch vSwitch;

	public SwitchDeviceView(Context context) {
		super(context);
		init();
	}

	public SwitchDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwitchDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(21)
	public SwitchDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(R.layout.view_switch_device, this);
		ButterKnife.inject(v);
	}

	@Override
	public void bind() {
		vText.setText(device.name);
		vSwitch.setChecked(getDeviceState());

		// Make the entire row clickable
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vSwitch.performClick();

			}
		});

		// Respond to switches by emitting the desired state
		vSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (DeviceAttribute a : device.attributes) {
					Timber.e("Attribute: " + (a == null ? "null" : a.name + " = " + a.value));
				}
				Toast.makeText(v.getContext(), "clicked!", Toast.LENGTH_SHORT).show();
				String path = "/api/device/" + device.id + "/";
				String action = vSwitch.isChecked() ? "turnOn" : "turnOff";
				Network.rest(path + action);
			}
		});
	}

	private boolean getDeviceState() {
		for (DeviceAttribute a : device.attributes) {
			if (a != null && a.name.equals("state")) {
				return a.value.equals("true");
			}
		}
		return false;
	}
}
