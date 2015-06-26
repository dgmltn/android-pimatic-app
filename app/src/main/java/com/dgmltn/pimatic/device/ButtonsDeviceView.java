package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.ActionResponse;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.model.DeviceButton;
import com.dgmltn.pimatic.model.Model;

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
public class ButtonsDeviceView extends DeviceView implements View.OnClickListener {

	@InjectView(android.R.id.text1)
	TextView vText;

	@InjectView(R.id.buttons)
	ViewGroup vButtons;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("buttons");
		}

		@Override
		public DeviceView create(Context context) {
			return new ButtonsDeviceView(context);
		}
	};

	public ButtonsDeviceView(Context context) {
		super(context);
		init();
	}

	public ButtonsDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ButtonsDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(21)
	public ButtonsDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(R.layout.view_buttons_device, this);
		ButterKnife.inject(v);
		if (device != null) {
			bind();
		}
	}

	public void bind() {
		vText.setText(device.name);
		vButtons.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getContext());
		for (DeviceButton b : device.config.buttons) {
			TextView v = (TextView) inflater.inflate(R.layout.view_button, vButtons, false);
			v.setText(b.text);
			v.setTag(b.id);
			v.setOnClickListener(this);
			vButtons.addView(v);
		}
	}

	@Override
	public void onClick(View v) {
		pushButton(v.getTag().toString());
	}

	private void pushButton(String buttonId) {
		Model.getInstance().getNetwork().getRest()
			.buttonPressed(device.id, buttonId, new Callback<ActionResponse>() {
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
