package org.ralit.ofutonreading;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;



/**
 * ViewGroupの作成は以下のページが参考になりました。
 * http://ga29.blog.fc2.com/blog-entry-7.html
 */

public class PageView_gomi extends ViewGroup{

	private ScrollView mScrollView;
	private ImageView mPageView;
	private ImageView mMarkerView;
	private FrameLayout mPageFrame;

	public PageView_gomi(Context context) {
		super(context);
		mScrollView = new ScrollView(context);
		mScrollView.setSmoothScrollingEnabled(true);
		mScrollView.setPadding(0, 0, 0, 0);
		mScrollView.setFillViewport(true);
		addView(mScrollView);
		mPageFrame = new FrameLayout(context);
		mScrollView.addView(mPageFrame);
		
		
		mPageView = new ImageView(context);
		mMarkerView = new ImageView(context);
		mPageFrame.addView(mPageView);
		mPageFrame.addView(mMarkerView);
		mPageFrame.setBackgroundColor(Color.GREEN);
		mPageView.setBackgroundColor(Color.BLACK);
		mScrollView.setBackgroundColor(Color.RED);
		setBackgroundColor(Color.YELLOW);
		TextView view = new TextView(context);
		view.setText("hello");
		mPageFrame.addView(view);
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
		mPageFrame.setMinimumHeight((int)h / 2);
		mScrollView.setMinimumHeight((int)h / 2);
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
