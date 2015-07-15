package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.ActionResponse;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by doug on 6/7/15.
 */
public class SwitchDeviceView extends DeviceView {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.device_content)
	SwitchCompat vContent;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("switch");
		}

		@Override
		public int getLayoutResId() {
			return R.layout.view_switch_device;
		}
	};

	public SwitchDeviceView(Context context) {
		super(context);
	}

	public SwitchDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SwitchDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public SwitchDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);

		// Respond to switches by emitting the desired state
		vContent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				pushDeviceState(vContent.isChecked());
			}
		});

		// Make the entire row clickable
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vContent.performClick();

			}
		});
	}

	@Subscribe
	public void otto(Events.DeviceChanged e) {
		super.otto(e);
	}

	@Override
	public void bind() {
		vName.setText(device.name);
		vContent.setChecked(getDeviceState());
	}

	private boolean getDeviceState() {
		for (DeviceAttribute a : device.attributes) {
			if (a != null && "state".equals(a.name)) {
				return "true".equals(a.value);
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
