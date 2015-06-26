package com.dgmltn.pimatic.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.View;

/**
 * A view whose background can have its color saturation set (and animated).
 */
public class SaturableView extends View {
	public SaturableView(Context context) {
		super(context);
	}

	public SaturableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SaturableView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(21)
	public SaturableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	private float mSaturation = 1f;
	private ColorFilter mColorFilter = null;

	public void setSaturation(float saturation) {
		if (saturation == mSaturation) {
			return;
		}

		mSaturation = saturation;
		if (mSaturation == 1f) {
			mColorFilter = null;
		}
		else {
			ColorMatrix matrix = new ColorMatrix();
			matrix.setSaturation(mSaturation);
			mColorFilter = new ColorMatrixColorFilter(matrix);
		}
		invalidate();
	}

	public float getSaturation() {
		return mSaturation;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		getBackground().setColorFilter(mColorFilter);
		super.onDraw(canvas);
	}
}
