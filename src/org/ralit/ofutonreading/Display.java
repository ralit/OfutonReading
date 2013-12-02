package org.ralit.ofutonreading;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
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
	private BookManager mBook;
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
	private AnimatorSet mAnimation;
	private float mTextZoom;
	private int mTickerWidth;
	private int mTickerHeight;
	private ImageView mAnimatingTicker; 
	private Bitmap mPageBitmap;
	private Bitmap mScaledPageBitmap;
	private int mDuration;
	private int mW;
	private int mH;
	private float mRW;
	private float mRH;
	
	public Display(Context context, FrameLayout rootFrame, BookManager bookManager) {
//		super(context);
		mContext = context;
		mRootFrame = rootFrame;
		mBook = bookManager;
		
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
		mScrollView.setSmoothScrollingEnabled(true);
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
		mRW = mRootFrame.getWidth();
		mRH = mRootFrame.getHeight();
		mTickerFrame.setLayoutParams(new LayoutParams((int)mRW, (int)mRH / 2));
		mScrollView.setLayoutParams(new LayoutParams((int)mRW, (int)mRH / 2));
	}
	
	public void setImage() {
		if(!mBook.isRecognized()) {
			// レイアウト認識がまだだったらレイアウト認識を行う。
			// レイアウト認識中は全画面でページを表示してあげる。
			mTickerFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
			mScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)mRH));
			mPageFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)mRH));
			mPageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)mRH));
			mPageView.setImageResource(R.drawable.ofuton);
			// ページを表示
			mPageBitmap = mBook.getBitmap();
			float w = (float) mPageBitmap.getWidth();
			float h = (float) mPageBitmap.getHeight();
			float ratio = mRH / h;
			float small_w = w * ratio;
			float scale_ratio = mRW / small_w;
			log("mRW: " + mRW);
			mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (h/w)), false);
			mPageView.setImageBitmap(mScaledPageBitmap);
			// マーカーの処理
//			markedPage = Bitmap.createScaledBitmap(markerBitmap, (int)dW, (int)(dW * (h/w)), false);
//			markerview.setImageBitmap(markedPage);
			mPageFrame.setScaleX(scale_ratio);
			mPageFrame.setScaleY(scale_ratio);
			// Docomoによる認識を開始
			mBook.recognize();
			// 認識終了後
			float linemid = (mBook.getPageLayout().get(mBook.getCurLine()).get(3) + mBook.getPageLayout().get(mBook.getCurLine()).get(1)) / 2;
			float distance = h / 2 - linemid;
			float i = distance * (mRW / w);
			mPageFrame.setY(i);
			AnimatorSet set = new AnimatorSet();
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(mTickerFrame, "height", mRH / 2);
			ObjectAnimator anim2 = ObjectAnimator.ofFloat(mScrollView, "height", mRH / 2);
			ObjectAnimator anim3 = ObjectAnimator.ofFloat(mPageFrame, "y", i);
			set.playTogether(anim1, anim2, anim3);
			set.setDuration(500);
			set.start();
		}
		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
		else { mAnimatingTicker = mTicker1; }
		mW = mBook.getPageLayout().get(mBook.getCurLine()).get(2) - mBook.getPageLayout().get(mBook.getCurLine()).get(0);
		mH = mBook.getPageLayout().get(mBook.getCurLine()).get(3) - mBook.getPageLayout().get(mBook.getCurLine()).get(1);
		mAnimatingTicker.setImageBitmap(Bitmap.createBitmap(mPageBitmap, mBook.getPageLayout().get(mBook.getCurLine()).get(0), mBook.getPageLayout().get(mBook.getCurLine()).get(1), mW, mH));
		mTextZoom = ((float)mRH / 2f) / ((float)mH * ((float)mRW / (float)mW));
		mAnimatingTicker.setScaleX(mTextZoom);
		mAnimatingTicker.setScaleY(mTextZoom);
		mAnimatingTicker.setX(mRW * mTextZoom / (float)2);
		mAnimatingTicker.setY(0);
		// アニメーション開始
		animation(0);
	}
	
	public void paintPosition() {
		
	}
	
	public void animation(long startDelay) {
		mAnimation = new AnimatorSet();
		ObjectAnimator move = null;
		mDuration = 530;
		
		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
		else { mAnimatingTicker = mTicker1; }

		mTickerWidth = (int) (mRW * mTextZoom * ((float)mW/(float)mH));
		mTickerHeight = (int) (mRH * mTextZoom / 2);
		log("mTextZoom"+mTextZoom);
		log("mTickerWidth: "+mTickerWidth);
		log("mTickerHeight: "+mTickerHeight);
		move = ObjectAnimator.ofFloat(mAnimatingTicker, "x", mTickerWidth, -mTickerWidth);
		if (mAnimatingTicker.getWidth() > mAnimatingTicker.getHeight()) {
			mDuration *= (int)((float)mTickerWidth / (float)mTickerHeight);
		} else {
			mDuration *= (int)((float)mTickerHeight / (float)mTickerWidth);
		}
		move.setDuration(mDuration);
		move.setInterpolator(new LinearInterpolator());
		mAnimation.addListener(this);
		mAnimation.setStartDelay(startDelay);
		mAnimation.start();
	}
	
	public void finishAnimation() {
		mAnimation.end();
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
		log("mTickerFrame.getWidth(): "+mTickerFrame.getWidth());
		log("mDuration"+mDuration);
		log("startdelay: " + (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));
		animation((long)(animation.getDuration() * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));
	}
}
