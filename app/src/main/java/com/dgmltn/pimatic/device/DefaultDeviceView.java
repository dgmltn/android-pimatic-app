package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.DeviceAttribute;
import com.dgmltn.pimatic.util.Events;
import com.dgmltn.pimatic.util.SpannableBuilder;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doug on 6/6/15.
 */
public class DefaultDeviceView extends DeviceView {

	@Bind(R.id.device_name)
	TextView vName;

	@Bind(R.id.device_content)
	TextView vContent;

	public static DeviceViewMapper.Matcher matcher = new DeviceViewMapper.Matcher() {

		@Override
		public boolean matches(Device d) {
			return true;
		}

		@Override
		public int getLayoutResId() {
			return R.layout.view_default_device;
		}

	};

	public DefaultDeviceView(Context context) {
		super(context);
	}

	public DefaultDeviceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DefaultDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public DefaultDeviceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int nameWidth = vName.getMeasuredWidth();
		int nameHeight = vName.getMeasuredHeight();
		int contWidth = vContent.getMeasuredWidth();
		int contHeight = vContent.getMeasuredHeight();
		int availWidth = MeasureSpec.getSize(widthMeasureSpec);

		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = nameWidth + contWidth < availWidth
			? nameHeight + getPaddingTop() + getPaddingBottom()
			: nameHeight + contHeight + getPaddingTop() + getPaddingBottom();

		setMeasuredDimension(
			MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
			MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
		);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int nameWidth = vName.getMeasuredWidth();
		int contWidth = vContent.getMeasuredWidth();
		int availWidth = right - left;

		// This is the collapsed row -- everything on the same line
		if (nameWidth + contWidth < availWidth) {
			final int parentTop = getPaddingTop();
			final int parentBottom = bottom - top - getPaddingBottom();
			final int parentHeight = parentBottom - parentTop;

			// Layout Name left + vertically centered
			int l = getPaddingLeft();
			int r = l + vName.getMeasuredWidth();
			int t = (parentHeight - vName.getMeasuredHeight()) / 2 + parentTop;
			int b = t + vName.getMeasuredHeight();
			vName.layout(l, t, r, b);

			r = right - left - getPaddingRight();
			l = r - vContent.getMeasuredWidth();
			t = (parentHeight - vContent.getMeasuredHeight()) / 2 + parentTop;
			b = t + vContent.getMeasuredHeight();
			vContent.layout(l, t, r, b);
		}

		// Don't need to collapse this row, so let's just the normal
		// FrameLayout layout_gravity layouts
		else {
			super.onLayout(changed, left, top, right, bottom);
		}
	}

	@Subscribe
	public void otto(Events.DeviceChanged e) {
		super.otto(e);
	}

	public void bind() {
		vName.setText(device.name);
		vContent.setText(getSpannedString(getContext()));
	}

	public SpannableStringBuilder getSpannedString(Context context) {
		SpannableBuilder builder = new SpannableBuilder(context);

		for (DeviceAttribute attr : device.attributes) {
			if (attr.hidden) {
				continue;
			}

			String unit = attr.unit;

			if (!TextUtils.isEmpty(attr.acronym)) {
				builder.append(attr.acronym.toUpperCase() + "\u00A0",
					new StyleSpan(android.graphics.Typeface.BOLD),
					new ForegroundColorSpan(Color.GRAY),
					new RelativeSizeSpan(0.8f));
			}
			if (TextUtils.isEmpty(attr.value)) {
				builder.append(context.getString(R.string.Unknown),
					new ForegroundColorSpan(Color.GRAY));
			}
			else if (attr.type.equals("number")) {
				double d = Double.parseDouble(attr.value);
				d = Math.round(d * 100) / 100d;
				if (attr.unit != null && attr.unit.equals("B")) {
					long bytes = (long) d;
					int base = 1000;
					if (bytes < base) {
						builder.append(attr.value);
					}
					else {
						// http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
						int exp = (int) (Math.log(bytes) / Math.log(base));
						unit = "KMGTPE".charAt(exp - 1) + "B";
						builder.append(String.format("%.2f", bytes / Math.pow(base, exp)));
					}
				}
				else {
					builder.append(Double.toString(d));
				}
			}
			else {
				builder.append(attr.value);
			}
			if (!TextUtils.isEmpty(unit)) {
				builder.append(unit, new ForegroundColorSpan(Color.GRAY));
			}
			builder.append(" ");
		}
		return builder.build();
	}
}
