package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dgmltn.pimatic.PimaticApp;
import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.ActionResponse;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by doug on 6/6/15.
 */
public class ShutterDeviceView extends DeviceView {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.up)
	ToggleButton vUp;

	@Bind(R.id.down)
	ToggleButton vDown;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("shutter");
		}

		@Override
		public int getLayoutResId() {
			return R.layout.view_shutter_device;
		}
	};

	public ShutterDeviceView(Context context) {
		super(context);
	}

	public ShutterDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ShutterDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public ShutterDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

	@OnClick(R.id.up)
	public void onClickUp() {
		vUp.setChecked(true);
		vDown.setChecked(false);
		doAction("moveUp");
	}

	@OnClick(R.id.down)
	public void onClickDown() {
		vDown.setChecked(true);
		vUp.setChecked(false);
		doAction("moveDown");
	}

	@Override
	public void bind() {
		vName.setText(device.name);
		String state = getDeviceState();
		vUp.setChecked(state.equals("up"));
		vDown.setChecked(state.equals("down"));
		//TODO: up/down
	}

	// Returns "up", "down", "stopped"
	private String getDeviceState() {
		DeviceAttribute a = device.findAttribute("position");
		return a == null || a.value == null ? "stopped" : a.value;
	}

	private void doAction(String action) {
		PimaticApp.getNetwork().getRest()
			.deviceAction(device.id, action, new Callback<ActionResponse>() {
				@Override
				public void success(ActionResponse actionResponse, Response response) {
					//TODO
				}

				@Override
				public void failure(RetrofitError error) {
					//TODO
					Timber.e(error.toString());
				}
			});

	}
}
