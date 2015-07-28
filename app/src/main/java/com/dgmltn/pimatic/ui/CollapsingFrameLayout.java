package com.dgmltn.pimatic.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.dgmltn.pimatic.R;

/**
 * Created by doug on 6/6/15.
 */
public class CollapsingFrameLayout extends FrameLayout {

	private int mPaddingSibling;
	private int mMinHeight;

	public CollapsingFrameLayout(Context context) {
		super(context);
		init(context, null);
	}

	public CollapsingFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CollapsingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CollapsingFrameLayout);
			mPaddingSibling = a.getDimensionPixelSize(R.styleable.CollapsingFrameLayout_paddingSibling, 0);
			mMinHeight = a.getDimensionPixelSize(R.styleable.CollapsingFrameLayout_minHeight, 0);
			a.recycle();
		}
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
		int availWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();

		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = Math.max(mMinHeight,
			nameWidth + contWidth < availWidth
			? Math.max(nameHeight, contHeight) + getPaddingTop() + getPaddingBottom()
			: nameHeight + contHeight + getPaddingTop() + getPaddingBottom() + mPaddingSibling
		);

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

		// Collapse this row -- put everything on the same line
		if (nameWidth + contWidth < availWidth) {
			final int parentTop = getPaddingTop();
			final int parentBottom = bottom - top - getPaddingBottom();
			final int parentHeight = parentBottom - parentTop;

			// Layout Device Name: left + vertically centered
			int l = getPaddingLeft();
			int r = l + child0.getMeasuredWidth();
			int t = (parentHeight - child0.getMeasuredHeight()) / 2 + parentTop;
			int b = t + child0.getMeasuredHeight();
			child0.layout(l, t, r, b);

			// Layout Device Content: Right + vertically centered
			r = right - left - getPaddingRight();
			l = r - child1.getMeasuredWidth();
			t = (parentHeight - child1.getMeasuredHeight()) / 2 + parentTop;
			b = t + child1.getMeasuredHeight();
			child1.layout(l, t, r, b);
		}

		// Don't collapse this row -- put the content below the title
		else {
			// Layout Device Name: left + top (observing padding)
			int l = getPaddingLeft();
			int r = l + child0.getMeasuredWidth();
			int t = getPaddingTop();
			int b = t + child0.getMeasuredHeight();
			child0.layout(l, t, r, b);

			// Layout Device Content: Right + below Name
			r = right - left - getPaddingRight();
			l = r - child1.getMeasuredWidth();
			t = b + mPaddingSibling;
			b = t + child1.getMeasuredHeight();
			child1.layout(l, t, r, b);
		}
	}
}
