package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.ActionResponse;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.network.Network;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by doug on 6/7/15.
 */
public class SwitchDeviceView extends DeviceView {

	@InjectView(android.R.id.text1)
	TextView vText;

	@InjectView(R.id.switch_view)
	SwitchCompat vSwitch;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("switch");
		}

		@Override
		public DeviceView create(Context context) {
			return new SwitchDeviceView(context);
		}
	};

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

		// Respond to switches by emitting the desired state
		vSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "clicked!", Toast.LENGTH_SHORT).show();
				pushDeviceState(vSwitch.isChecked());
			}
		});

		// Make the entire row clickable
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vSwitch.performClick();

			}
		});
	}

	@Subscribe
	public void otto(Events.DeviceChanged e) {
		super.otto(e);
	}

	@Override
	public void bind() {
		vText.setText(device.name);
		vSwitch.setChecked(getDeviceState());
	}

	private boolean getDeviceState() {
		for (DeviceAttribute a : device.attributes) {
			if (a != null && a.name.equals("state")) {
				return a.value.equals("true");
			}
		}
		return false;
	}

	private void pushDeviceState(boolean isChecked) {
		String action = isChecked ? "turnOn" : "turnOff";
		Model.getInstance().getNetwork().getRest()
			.deviceAction(device.id, action, new Callback<ActionResponse>() {
			@Override
			public void success(ActionResponse actionResponse, Response response) {
				//TODO
			}

			@Override
			public void failure(RetrofitError error) {
				//TODO
			}
		});
	}
}
