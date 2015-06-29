package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.ActionResponse;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.model.DeviceButton;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by doug on 6/6/15.
 */
public class ShutterDeviceView extends DeviceView {

	@InjectView(android.R.id.text1)
	TextView vText;

	@InjectView(R.id.up)
	ToggleButton vUp;

	@InjectView(R.id.down)
	ToggleButton vDown;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("shutter");
		}

		@Override
		public DeviceView create(Context context) {
			return new ShutterDeviceView(context);
		}
	};

	public ShutterDeviceView(Context context) {
		super(context);
		init();
	}

	public ShutterDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShutterDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(21)
	public ShutterDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(R.layout.view_shutter_device, this);
		ButterKnife.inject(v);
		if (device != null) {
			bind();
		}
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
		vText.setText(device.name);
		String state = getDeviceState();
		vUp.setChecked(state.equals("up"));
		vDown.setChecked(state.equals("down"));
		//TODO: up/down
	}

	// Returns "up", "down", "stopped"
	private String getDeviceState() {
		for (DeviceAttribute a : device.attributes) {
			if (a != null && a.name.equals("position")) {
				return a.value;
			}
		}
		return "stopped";
	}

	private void doAction(String action) {
		Model.getInstance().getNetwork().getRest()
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
