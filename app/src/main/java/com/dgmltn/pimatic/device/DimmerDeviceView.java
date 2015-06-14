package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.util.Network;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by doug on 6/7/15.
 */
public class DimmerDeviceView extends DeviceView {

	@InjectView(android.R.id.text1)
	TextView vText;

	@InjectView(R.id.seek)
	SeekBar vSeek;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("dimmer");
		}

		@Override
		public DeviceView create(Context context) {
			return new DimmerDeviceView(context);
		}
	};

	public DimmerDeviceView(Context context) {
		super(context);
		init();
	}

	public DimmerDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DimmerDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(21)
	public DimmerDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		View v = LayoutInflater.from(getContext()).inflate(R.layout.view_dimmer_device, this);
		ButterKnife.inject(v);
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

	@Override
	public void bind() {
		vText.setText(device.name);
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
		// /api/device/my-device-id/changeDimlevelTo?dimlevel=15
		String path = "/api/device/" + device.id + "/";
		String action = "changeDimlevelTo?dimlevel=" + to;
		Network.rest(path + action);
	}
}
