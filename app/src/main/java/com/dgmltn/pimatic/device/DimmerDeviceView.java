package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import timber.log.Timber;

/**
 * Created by doug on 6/7/15.
 */
public class DimmerDeviceView extends DeviceView {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.seek)
	SeekBar vSeek;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("dimmer");
		}

		@Override
		public int getLayoutResId() {
			return R.layout.view_dimmer_device;
		}
	};

	public DimmerDeviceView(Context context) {
		super(context);
	}

	public DimmerDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DimmerDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public DimmerDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
		vSeek.setMax(100);
		vSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// Nothing to do here
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Nothing to do here
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				Toast.makeText(getContext(), "dimlevel " + progress, Toast.LENGTH_SHORT).show();
				pushDimlevel(progress);
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
		vSeek.setProgress(getDeviceDimlevel());
	}

	private int getDeviceDimlevel() {
		for (DeviceAttribute a : device.attributes) {
			if (a.name.equals("dimlevel")) {
				return Integer.parseInt(a.value);
			}
		}
		return 0;
	}

	private void pushDimlevel(int to) {
		Model.getInstance().getNetwork().getRest()
			.changeDimlevelTo(device.id, to, new Callback<ActionResponse>() {
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
