package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doug on 6/7/15.
 */
public class PresenceDeviceView extends DeviceView {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.device_content)
	SaturableView vContent;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("presence");
		}

		@Override
		public
		@LayoutRes
		int getLayoutResId() {
			return R.layout.view_presence_device;
		}
	};

	public PresenceDeviceView(Context context) {
		super(context);
	}

	public PresenceDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PresenceDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public PresenceDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void bind() {
		vName.setText(device.name);

		boolean enabled = getDeviceState();
		vContent.setEnabled(enabled);
		vContent.setSaturation(enabled ? 1.0f : 0f);
		vContent.setAlpha(enabled ? 1f : 0.2f);
		if (Build.VERSION.SDK_INT >= 21) {
			float density = getResources().getDisplayMetrics().density;
			vContent.setTranslationZ(enabled ? 2 * density : 0f);
		}
	}

	private boolean getDeviceState() {
		DeviceAttribute a = device.findAttribute("presence");
		return a != null && "true".equals(a.value);
	}
}
