package org.ralit.ofutonreading;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Display implements AnimatorListener{

	// コンストラクタ
	private Context mContext;
	private FrameLayout mRootFrame;
	private BookManager mBookManager;
	// レイアウトとビュー
	private LinearLayout mLinearLayout;
	private FrameLayout mTickerFrame;
	private ScrollView mScrollView;
	private ImageView mTicker1;
	private ImageView mTicker2;
	private FrameLayout mPageFrame;
	private ImageView mPageView;
	private ImageView mMarkerView;
	// その他
	private AnimatorSet mCrossFade;
	private float mLineZoom;
	private int mTickerWidth;
	private int mTickerHeight;
	private ImageView mAnimatingTicker; 
	
	public Display(Context context, FrameLayout rootFrame, BookManager bookManager) {
		mContext = context;
		mRootFrame = rootFrame;
		mBookManager = bookManager;
		
		// 2画面の基本となる一番下の枠を作る
		mLinearLayout = new LinearLayout(mContext);
		mTickerFrame = new FrameLayout(mContext);
		mScrollView = new ScrollView(mContext);
		mLinearLayout.addView(mTickerFrame);
		mLinearLayout.addView(mScrollView);
		// 上画面の電光掲示板を作る
		mTicker1 = new ImageView(mContext);
		mTicker2 = new ImageView(mContext);
		mTickerFrame.addView(mTicker1);
		mTickerFrame.addView(mTicker2);
		// 下画面にFrameLayoutを入れる(ページとマーカーを重ねるため)
		mPageFrame = new FrameLayout(mContext);
		mScrollView.addView(mPageFrame);
		// 下画面を作る
		mPageView = new ImageView(mContext);
		mMarkerView = new ImageView(mContext);
		mPageFrame.addView(mPageView);
		mPageFrame.addView(mMarkerView);
		
		// スプラッシュ表示のため
		mLinearLayout.setAlpha(0f);
		
		// ここで初めて描画
		mRootFrame.addView(mLinearLayout);
		
		// パラメータ設定
		mRootFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		// mTickerFrameとmScrollView以外は、MATCH_PARENT
		mTicker1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mTicker2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mPageFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mMarkerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mPageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		// 画像表示テスト
		mTicker1.setImageResource(R.drawable.ofuton);
		mPageView.setImageResource(R.drawable.usagi);
		
		// スプラッシュ画面に重ねてスプラッシュを消す(スプラッシュは後から表示されてもいいように、背景画像にしたらいいんじゃないかな。
		ObjectAnimator animator = ObjectAnimator.ofFloat(mLinearLayout, "alpha", 1f);
		animator.setDuration(500).start();
		
		updateLayout();
		
	}
	
	public void updateLayout() {
		log("updateLayout()");
		log("mRootFrame.getWidth(): " + mRootFrame.getWidth() + ", mRootFrame.getHeight(): " + mRootFrame.getHeight()); 
		mTickerFrame.setLayoutParams(new LayoutParams(mRootFrame.getWidth(), mRootFrame.getHeight() / 2));
		mScrollView.setLayoutParams(new LayoutParams(mRootFrame.getWidth(), mRootFrame.getHeight() / 2));
	}
	
	public void setTicker() {
		
	}
	
	public void setPage() {
		
	}
	
	public void paintPosition() {
		
	}
	
	public void crossFade(long startDelay) {
		mCrossFade = new AnimatorSet();
		ObjectAnimator move = null;
		int duration = 530;
		
		if(mAnimatingTicker == mTicker1) {
			mAnimatingTicker = mTicker2;
		} else {
			mAnimatingTicker = mTicker1;
		}

		mTickerWidth = mAnimatingTicker.getWidth();
		mTickerHeight = mAnimatingTicker.getHeight();
		move = ObjectAnimator.ofFloat(mAnimatingTicker, "x", mTickerFrame.getWidth() * mLineZoom, -mTickerFrame.getWidth() * mLineZoom);
		if (mAnimatingTicker.getWidth() > mAnimatingTicker.getHeight()) {
			duration *= (int)((float)mAnimatingTicker.getWidth() / (float)mAnimatingTicker.getHeight());
		} else {
			duration *= (int)((float)mAnimatingTicker.getHeight() / (float)mAnimatingTicker.getWidth());
		}
		move.setDuration(duration);
		move.setInterpolator(new LinearInterpolator());
		mCrossFade.addListener(this);
		mCrossFade.setStartDelay(startDelay);
		mCrossFade.start();
	}
	
	public void finishCrossFade() {
		mCrossFade.end();
	}
	
	public void mark() {
		
	}

	private void log(String log) {
		Log.i("ralit", log);
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animator animation) {
		// TODO Auto-generated method stub
		crossFade((long)(animation.getDuration() * ((float)(mTickerWidth - mTickerFrame.getWidth()) / (float)mTickerWidth)));
	}
}
