package com.dgmltn.pimatic.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by doug on 6/6/15.
 */
public class CollapsingFrameLayout extends FrameLayout {

	public CollapsingFrameLayout(Context context) {
		super(context);
	}

	public CollapsingFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CollapsingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		View child0 = getChildAt(0);
		View child1 = getChildAt(1);
		int nameWidth = child0.getMeasuredWidth();
		int nameHeight = child0.getMeasuredHeight();
		int contWidth = child1.getMeasuredWidth();
		int contHeight = child1.getMeasuredHeight();
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
		View child0 = getChildAt(0);
		View child1 = getChildAt(1);

		int nameWidth = child0.getMeasuredWidth();
		int contWidth = child1.getMeasuredWidth();
		int availWidth = right - left;

		// This is the collapsed row -- everything on the same line
		if (nameWidth + contWidth < availWidth) {
			final int parentTop = getPaddingTop();
			final int parentBottom = bottom - top - getPaddingBottom();
			final int parentHeight = parentBottom - parentTop;

			// Layout Name left + vertically centered
			int l = getPaddingLeft();
			int r = l + child0.getMeasuredWidth();
			int t = (parentHeight - child0.getMeasuredHeight()) / 2 + parentTop;
			int b = t + child0.getMeasuredHeight();
			child0.layout(l, t, r, b);

			r = right - left - getPaddingRight();
			l = r - child1.getMeasuredWidth();
			t = (parentHeight - child1.getMeasuredHeight()) / 2 + parentTop;
			b = t + child1.getMeasuredHeight();
			child1.layout(l, t, r, b);
		}

		// Don't need to collapse this row, so let's just the normal
		// FrameLayout layout_gravity layouts
		else {
			super.onLayout(changed, left, top, right, bottom);
		}
	}
}
