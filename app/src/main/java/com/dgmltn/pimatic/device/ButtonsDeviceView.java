package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgmltn.pimatic.PimaticApp;
import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.ActionResponse;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceButton;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by doug on 6/6/15.
 */
public class ButtonsDeviceView extends DeviceView implements View.OnClickListener {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.device_content)
	ViewGroup vContent;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("buttons");
		}

		@Override
		public
		@LayoutRes
		int getLayoutResId() {
			return R.layout.view_buttons_device;
		}
	};

	public ButtonsDeviceView(Context context) {
		super(context);
	}

	public ButtonsDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ButtonsDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public ButtonsDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
		vContent.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getContext());
		for (DeviceButton b : device.config.buttons) {
			TextView v = (TextView) inflater.inflate(R.layout.view_button, vContent, false);
			v.setText(b.text);
			v.setTag(b.id);
			v.setOnClickListener(this);
			vContent.addView(v);
		}
	}

	@Override
	public void onClick(View v) {
		pushButton(v.getTag().toString());
	}

	private void pushButton(String buttonId) {
		PimaticApp.getNetwork().getRest()
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
