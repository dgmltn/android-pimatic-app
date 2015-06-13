package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.dgmltn.pimatic.model.Device;

/**
 * Created by doug on 6/6/15.
 */
public abstract class DeviceView extends FrameLayout {

	protected Device device;

	public DeviceView(Context context) {
		super(context);
	}

	public DeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public DeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setDevice(Device d) {
		device = d;
		if (device != null) {
			bind();
		}
	}

	public Device getDevice() {
		return device;
	}

	public abstract void bind();

}
