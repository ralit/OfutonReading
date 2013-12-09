package org.ralit.ofutonreading;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class BookViewTest extends ViewGroup implements AnimatorListener{

	// コンストラクタ
	private Context mContext;
//	private FrameLayout mRootFrame;
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
	private int mLineW;
	private int mLineH;
	private float mRW;
	private float mRH;
	private boolean mPending = false;
//	private enum AnimationFlag {layout, loop};
//	private AnimationFlag mAnimationFlag;
	
//	private LinearLayout linear;
//	private FrameLayout frame1;
//	private FrameLayout frame2;
	
	public BookViewTest(Context context) {
		super(context);
		log("BookView()");
//		linear = new LinearLayout(context);
//		frame1 = new FrameLayout(context);
//		frame2 = new FrameLayout(context);
		mContext = context;
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
		addView(mLinearLayout);
		
		// スプラッシュ画面に重ねてスプラッシュを消す(スプラッシュは後から表示されてもいいように、背景画像にしたらいいんじゃないかな。
//		ObjectAnimator animator = ObjectAnimator.ofFloat(mLinearLayout, "alpha", 1f);
//		animator.addListener(this);
//		mAnimationFlag = AnimationFlag.layout;
//		animator.setDuration(500).start();
	}
	
//	@Override
//	public void onWindowFocusChanged(boolean hasWindowFocus) {
//		// TODO Auto-generated method stub
//		log("onWindowFocusChanged in BookView");
//		super.onWindowFocusChanged(hasWindowFocus);
//		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		mLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
//		// mTickerFrameとmScrollView以外は、MATCH_PARENT
//		mTicker1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		mTicker2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		mPageFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		mMarkerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		mPageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		// 画像表示テスト
////		mTicker1.setImageResource(R.drawable.ofuton);
////		mPageView.setImageResource(R.drawable.usagi);
//		log("updateLayout()");
//		mRW = getWidth();
//		log("mRW:"+mRW);
//		mRH = getHeight();
//		log("mRH:"+mRH);
//		mTickerFrame.setLayoutParams(new LayoutParams((int)mRW, (int)mRH / 2));
//		mScrollView.setLayoutParams(new LayoutParams((int)mRW, (int)mRH / 2));
//	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		log("onSizeChanged");
		super.onSizeChanged(w, h, oldw, oldh);
//		linear.setBackgroundColor(Color.BLUE);
//		linear.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		linear.setOrientation(LinearLayout.VERTICAL);
//		frame1.setBackgroundColor(Color.BLACK);
//		frame1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, getHeight()/2));
//		frame2.setBackgroundColor(Color.RED);
//		frame2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, getHeight()/2));
//		linear.addView(frame1);
//		linear.addView(frame2);
//		setBackgroundColor(Color.GREEN);
//		addView(linear);
		// パラメータ設定
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mLinearLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		// mTickerFrameとmScrollView以外は、MATCH_PARENT
		mTicker1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mTicker2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mPageFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mMarkerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mPageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// 画像表示テスト
//		mTicker1.setImageResource(R.drawable.ofuton);
//		mPageView.setImageResource(R.drawable.usagi);
		mRW = w;
		log("mRW:"+mRW);
		mRH = h;
		log("mRH:"+mRH);
		mTickerFrame.setLayoutParams(new LayoutParams((int)mRW, (int)mRH / 2));
		mScrollView.setLayoutParams(new LayoutParams((int)mRW, (int)mRH / 2));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		log("onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		log("onLayout");
		final int count  = getChildCount();
		final int left   = getLeft();
		final int top    = getTop();
		final int right  = getRight();
		final int bottom = getBottom();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (view.getVisibility() != View.GONE) {
				view.layout(left, top, right, bottom);
			}
		}
		invalidate();
	}
	
	public void setBookManager(BookManager manager) {
		mBook = manager;
		mBook.recognize();
//		invalidate();
	}
	
	public void setImage() {
		log("setImage()");
		if(!mBook.isRecognized()) {
			log("mBook.isRecognized() == false");
			// レイアウト認識がまだだったらレイアウト認識を行う。
			// レイアウト認識中は全画面でページを表示してあげる。
			mTickerFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
			mScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)mRH));
			mPageFrame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)mRH));
			mPageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)mRH));
			mPageView.setImageResource(R.drawable.ofuton); // 後で消す
			// ページを表示
			mPageBitmap = mBook.getBitmap();
			
			float pageW = (float) mPageBitmap.getWidth();
			log("pageW:"+pageW);
			float pageH = (float) mPageBitmap.getHeight();
			log("pageH:"+pageH);
			float ratio = mRH / pageH;
			log("ratio:"+ratio);
			float small_w = pageW * ratio;
			log("small_w:"+small_w);
			float scale_ratio = mRW / small_w;
			log("scale_ratio:"+scale_ratio);
			mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), false);
			log("(mRW * (pageH/pageW)):"+(mRW * (pageH/pageW)));
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
			log("linemid:"+linemid);
			float distance = pageH / 2 - linemid;
			log("distance:"+distance);
			float i = distance * (mRW / pageW);
			log("i:"+i);
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
		mLineW = mBook.getPageLayout().get(mBook.getCurLine()).get(2) - mBook.getPageLayout().get(mBook.getCurLine()).get(0);
		log("mLineW:"+mLineW);
		mLineH = mBook.getPageLayout().get(mBook.getCurLine()).get(3) - mBook.getPageLayout().get(mBook.getCurLine()).get(1);
		log("mLineH:"+mLineH);
		mAnimatingTicker.setImageBitmap(Bitmap.createBitmap(mPageBitmap, mBook.getPageLayout().get(mBook.getCurLine()).get(0), mBook.getPageLayout().get(mBook.getCurLine()).get(1), mLineW, mLineH));
		mTextZoom = ((float)mRH / 2f) / ((float)mLineH * ((float)mRW / (float)mLineW));
		log("mTextZoom:"+mTextZoom);
		mAnimatingTicker.setScaleX(mTextZoom);
		mAnimatingTicker.setScaleY(mTextZoom);
		mTickerWidth = (int) (mRW * ((float)mLineW/(float)mLineH)); // 修正
		mTickerHeight = (int) (mRH / 2); // 修正
		log("mTickerWidth: "+mTickerWidth);
		log("mTickerHeight: "+mTickerHeight);
		mAnimatingTicker.setX(mTickerWidth);
		mAnimatingTicker.setY(0);
		// アニメーション開始
		animation(0);
	}
	
	public void paintPosition() {
		
	}
	
	public void animation(long startDelay) {
		if(mPending) { return; }
		mAnimation = new AnimatorSet();
		ObjectAnimator move = null;
		mDuration = 530;
		log("mDuration:"+mDuration);
		
		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
		else { mAnimatingTicker = mTicker1; }

		move = ObjectAnimator.ofFloat(mAnimatingTicker, "x", mTickerWidth, -mTickerWidth);
		if (mTickerWidth > mTickerHeight) {
			mDuration *= ((float)mTickerWidth / (float)mTickerHeight); // intへのキャストを削除
		} else {
			mDuration *= ((float)mTickerHeight / (float)mTickerWidth); // intへのキャストを削除
		}
		log("mDuration:"+mDuration);
		move.setDuration(mDuration);
		move.setInterpolator(new LinearInterpolator());
		mAnimation.addListener(this);
//		mAnimationFlag = AnimationFlag.loop;
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
//		if (mAnimationFlag == AnimationFlag.loop) {
			mPending = false;
//		} else if (mAnimationFlag == AnimationFlag.layout) {
//			updateLayout();
//		}
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animator animation) {
		// TODO Auto-generated method stub
//		if (mAnimationFlag == AnimationFlag.loop) {
			if(0 < animation.getStartDelay()) { mPending = true; } 
			log("startdelay: " + (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));
			animation((long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));	
//		}
	}
}
