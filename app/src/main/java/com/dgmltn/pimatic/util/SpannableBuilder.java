package com.dgmltn.pimatic.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;

// Much more usable SpannableStringBuilder
public class SpannableBuilder {
	private SpannableStringBuilder mBuilder;
	private Context mContext;

	public SpannableBuilder(Context context) {
		mBuilder = new SpannableStringBuilder();
		mContext = context;
	}

	public void append(CharSequence seq) {
		mBuilder.append(seq);
	}

	public void append(CharSequence seq, CharacterStyle... whats) {
		append(seq, 0, whats);
	}

	public void append(CharSequence seq, int flags, CharacterStyle... whats) {
		int start = mBuilder.length();
		int end = start + seq.length();

		mBuilder.append(seq);

		for (Object what : whats) {
			mBuilder.setSpan(what, start, end, flags);
		}
	}

	public void append(@DrawableRes int drawableResId) {
		Drawable d = mContext.getResources().getDrawable(drawableResId);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		append(" ", new ImageSpan(d, ImageSpan.ALIGN_BASELINE));
	}

	public SpannableStringBuilder build() {
		return mBuilder;
	}

	public int length() {
		return mBuilder.length();
	}
}