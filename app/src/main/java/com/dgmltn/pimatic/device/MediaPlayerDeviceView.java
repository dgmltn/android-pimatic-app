package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgmltn.pimatic.PimaticApp;
import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.ActionResponse;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.model.DeviceButton;
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
public class MediaPlayerDeviceView extends DeviceView {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.btn_play)
	ImageView vPlay;

	@Bind(R.id.btn_pause)
	ImageView vPause;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {
		@Override
		public boolean matches(Device d) {
			return d.template.equals("musicplayer");
		}

		@Override
		public
		@LayoutRes
		int getLayoutResId() {
			return R.layout.view_media_player_device;
		}
	};

	public MediaPlayerDeviceView(Context context) {
		super(context);
	}

	public MediaPlayerDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaPlayerDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public MediaPlayerDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
		boolean isPlaying = isPlaying();
		vPlay.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
		vPause.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
	}

	@OnClick(R.id.btn_prev)
	public void onClickBtnPrev(View v) {
		pushButton("previous");
	}

	@OnClick(R.id.btn_next)
	public void onClickBtnNext(View v) {
		pushButton("next");
	}

	@OnClick(R.id.btn_stop)
	public void onClickBtnStop(View v) {
		pushButton("stop");
	}

	@OnClick(R.id.btn_next)
	public void onClickBtnPlay(View v) {
		pushButton("play");
	}

	@OnClick(R.id.btn_pause)
	public void onClickBtnPause(View v) {
		pushButton("pause");
	}

	private boolean isPlaying() {
		DeviceAttribute attr = device.findAttribute("state");
		return attr != null && "playing".equals(attr.value);
	}

	private void pushButton(String action) {
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
