package org.ralit.ofutonreading;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;



/**
 * ViewGroupの作成は以下のページが参考になりました。
 * http://ga29.blog.fc2.com/blog-entry-7.html
 */

interface LineEndListener {
	void onLineEnd();
}

public class TickerView extends FrameLayout{

	private ImageView mTicker1;
	private ImageView mTicker2;

	public TickerView(Context context, LineEndListener _lineEndListener) {
		super(context);
		mTicker1 = new ImageView(context);
		mTicker2 = new ImageView(context);
		addView(mTicker1);
		addView(mTicker2);
		mTicker1.setBackgroundColor(Color.DKGRAY);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		final int count  = getChildCount();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			view.setLayoutParams(params);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		final int count = getChildCount();
		final int left = getLeft();
		final int top = getTop();
		final int right = getRight();
		final int bottom = getBottom();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (view.getVisibility() != View.GONE) {
				view.layout(left, top, right, bottom);
			}
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
