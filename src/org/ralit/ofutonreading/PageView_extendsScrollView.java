package org.ralit.ofutonreading;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;



/**
 * ViewGroupの作成は以下のページが参考になりました。
 * http://ga29.blog.fc2.com/blog-entry-7.html
 */

public class PageView_extendsScrollView extends ScrollView{

	private FrameLayout mTickerFrame;
	private ImageView mTicker1;
	private ImageView mTicker2;
	private ImageView mPageView;
	private ImageView mMarkerView;
	private FrameLayout mPageFrame;

	public PageView_extendsScrollView(Context context) {
		super(context);
		mPageFrame = new FrameLayout(context);
		addView(mPageFrame);
		setSmoothScrollingEnabled(true);
		setPadding(0, 0, 0, 0);
		mPageView = new ImageView(context);
		mMarkerView = new ImageView(context);
		mPageFrame.addView(mPageView);
		mPageFrame.addView(mMarkerView);
		mPageFrame.setBackgroundColor(Color.GREEN);
		mPageView.setBackgroundColor(Color.BLACK);
		setBackgroundColor(Color.RED);
		mPageFrame.setMinimumHeight(1000);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
//		mPageFrame.setMinimumHeight((int)h / 2);
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
