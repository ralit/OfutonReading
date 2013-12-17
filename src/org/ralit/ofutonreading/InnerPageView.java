package org.ralit.ofutonreading;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;


public class InnerPageView extends FrameLayout{

	private ImageView mPageView;
	private ImageView mMarkerView;

	
	public InnerPageView(Context context, Bitmap bmp) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
//		final int count  = getChildCount();
//		for (int i = 0; i < count; i++) {
//			View view = getChildAt(i);
//			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
//			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//			params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//			view.setLayoutParams(params);
//		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// TODO Auto-generated method stub
		//		final int count = getChildCount();
		//		final int left = getLeft();
		//		final int top = getTop();
		//		final int right = getRight();
		//		final int bottom = getBottom();
		//		for (int i = 0; i < count; i++) {
		//			View view = getChildAt(i);
		//			if (view.getVisibility() != View.GONE) {
		//				view.layout(left, top, right, bottom);
		//			}
		//		}
		//		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//		final int count = getChildCount();
		//		for(int i = 0; i < count; i++) {
		//			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		//		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
