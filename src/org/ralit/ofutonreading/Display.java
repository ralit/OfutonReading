package org.ralit.ofutonreading;

import android.R.array;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
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
	private float mLineZoom;
	private int mTickerWidth;
	private int mTickerHeight;
	private ImageView mAnimatingTicker; 
	private Bitmap mPageBitmap;
	private Bitmap mScaledPageBitmap;
	
	public Display(Context context, FrameLayout rootFrame, BookManager bookManager) {
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
	
	public void setImage() {
		if(!mBook.isRecognized()) {
			// レイアウト認識がまだだったらレイアウト認識を行う。
			// レイアウト認識中は全画面でページを表示してあげる。
			mTickerFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
			mScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mLinearLayout.getHeight()));
			// ページを表示
			mPageBitmap = mBook.getBitmap();
			float w = (float) mPageBitmap.getWidth();
			float h = (float) mPageBitmap.getHeight();
			float ratio = mPageFrame.getHeight() / h;
			float small_w = w * ratio;
			float scale_ratio = mPageFrame.getWidth() / small_w;
			mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mPageFrame.getWidth(), (int)(mPageFrame.getWidth() * (h/w)), false);
			mPageView.setImageBitmap(mScaledPageBitmap);
			// マーカーの処理
//			markedPage = Bitmap.createScaledBitmap(markerBitmap, (int)dW, (int)(dW * (h/w)), false);
//			markerview.setImageBitmap(markedPage);
			mPageFrame.setScaleX(scale_ratio);
			mPageFrame.setScaleY(scale_ratio);
			float linemid = (mBook.getPageLayout().get(mBook.getCurLine()).get(3) + mBook.getPageLayout().get(mBook.getCurLine()).get(1)) / 2;
			float distance = h / 2 - linemid;
			float i = distance * (mPageFrame.getWidth() / w);
			mPageFrame.setY(i);
			// Docomoによる認識を開始
			mBook.recognize();
			// 認識終了後
			AnimatorSet set = new AnimatorSet();
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(mTickerFrame, "height", mLinearLayout.getHeight() / 2);
			ObjectAnimator anim2 = ObjectAnimator.ofFloat(mScrollView, "height", mLinearLayout.getHeight() / 2);
			set.playTogether(anim1, anim2);
			set.setDuration(500);
			set.start();
		}
		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
		else { mAnimatingTicker = mTicker1; }
		int w = mBook.getPageLayout().get(mBook.getCurLine()).get(2) - mBook.getPageLayout().get(mBook.getCurLine()).get(0);
		int h = mBook.getPageLayout().get(mBook.getCurLine()).get(3) - mBook.getPageLayout().get(mBook.getCurLine()).get(1);
		mAnimatingTicker.setImageBitmap(Bitmap.createBitmap(mPageBitmap, mBook.getPageLayout().get(mBook.getCurLine()).get(0), mBook.getPageLayout().get(mBook.getCurLine()).get(1), w, h));
		int cW = (mBook.getPageLayout().get(mBook.getCurLine()).get(2) - mBook.getPageLayout().get(mBook.getCurLine()).get(0));
		int cH = (mBook.getPageLayout().get(mBook.getCurLine()).get(3) - mBook.getPageLayout().get(mBook.getCurLine()).get(1));
		float textZoom = mTickerFrame.getHeight() / (cH * (mTickerFrame.getWidth()/cW));
		mAnimatingTicker.setScaleX(textZoom);
		mAnimatingTicker.setScaleY(textZoom);
		mAnimatingTicker.setX(mTickerFrame.getWidth() * textZoom / (float)2);
		mAnimatingTicker.setY(0);
		// アニメーション開始
		animation(0);
	}
	
	public void paintPosition() {
		
	}
	
	public void animation(long startDelay) {
		mAnimation = new AnimatorSet();
		ObjectAnimator move = null;
		int duration = 530;
		
		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
		else { mAnimatingTicker = mTicker1; }

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
		animation((long)(animation.getDuration() * ((float)(mTickerWidth - mTickerFrame.getWidth()) / (float)mTickerWidth)));
	}
}
