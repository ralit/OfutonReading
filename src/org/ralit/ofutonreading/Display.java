package org.ralit.ofutonreading;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Display {

	private Context mContext;
	private FrameLayout mRootFrame;
	
	private LinearLayout mLinearLayout;
	private FrameLayout mTickerFrame;
	private ScrollView mScrollView;
	private ImageView mTicker1;
	private ImageView mTicker2;
	private FrameLayout mPageFrame;
	private ImageView mPageView;
	private ImageView mMarkerView;
	
	public Display(Context context, FrameLayout rootFrame) {
		mContext = context;
		mRootFrame = rootFrame;
		
		mLinearLayout = new LinearLayout(mContext);
		mTickerFrame = new FrameLayout(mContext);
		mScrollView = new ScrollView(mContext);
		mLinearLayout.addView(mTickerFrame);
		mLinearLayout.addView(mScrollView);
		
		mTicker1 = new ImageView(mContext);
		mTicker2 = new ImageView(mContext);
		mTickerFrame.addView(mTicker1);
		mTickerFrame.addView(mTicker2);
		
		mPageFrame = new FrameLayout(mContext);
		mScrollView.addView(mPageFrame);
		
		mPageView = new ImageView(mContext);
		mMarkerView = new ImageView(mContext);
		mPageFrame.addView(mPageView);
		mPageFrame.addView(mMarkerView);
		
//		mTicker1.setImageResource(R.drawable.ofuton);
		mLinearLayout.setAlpha(0f);
		
		mRootFrame.addView(mLinearLayout);
		
		// 画像表示テスト
		mPageView.setImageResource(R.drawable.usagi);
		
		// スプラッシュ画面に重ねてスプラッシュを消す(スプラッシュは後から表示されてもいいように、背景画像にしたらいいんじゃないかな。
		ObjectAnimator animator = ObjectAnimator.ofFloat(mLinearLayout, "alpha", 1f);
		animator.setDuration(500).start();
		
	}
	
	public void updateLayout() {
		
	}

}
