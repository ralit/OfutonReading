package org.ralit.ofutonreading;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ReadingActivity extends Activity implements AnimatorListener{

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
	private CountDownTimer initialSetImagetimer;
	private CountDownTimer waitForRecognize;
	private boolean isWindowFocusChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (intent != null) {
			String fileName = intent.getStringExtra("fileName");
			String filePath = intent.getStringExtra("filePath");
			mBook = new BookManager(fileName, filePath, this);
//			BookView bookView = new BookView(this, manager);
//			setContentView(bookView);
//			manager.setBookView(bookView);
			initialSetImagetimer = new CountDownTimer(3000, 100) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					if(mBook.getReadyForSetImage() && isWindowFocusChanged) {
						initialSetImagetimer.cancel();
						setImage(mBook.getBitmap(mBook.getCurPage()));
					}
				}
				
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					
				}
			}.start();
		}
		initialize();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//		getMenuInflater().inflate(R.menu.reading, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialize() {
		Fun.log("BookView()");
		// 2画面の基本となる一番下の枠を作る
		mLinearLayout = new LinearLayout(this);
		//		mLinearLayout.setBackgroundColor(Color.BLACK);
		mTickerFrame = new FrameLayout(this);
		mScrollView = new ScrollView(this);
		mLinearLayout.addView(mTickerFrame);
		mLinearLayout.addView(mScrollView);
		//		mTickerFrame.setBackgroundColor(Color.BLUE);
		//		mScrollView.setBackgroundColor(Color.CYAN);
		// 上画面の電光掲示板を作る
		mTicker1 = new ImageView(this);
		mTicker2 = new ImageView(this);
		mTickerFrame.addView(mTicker1);
		mTickerFrame.addView(mTicker2);
		mTicker1.setBackgroundColor(Color.DKGRAY);
		//		mTicker2.setBackgroundColor(Color.GRAY);
		// 下画面にFrameLayoutを入れる(ページとマーカーを重ねるため)
		mPageFrame = new FrameLayout(this);
		mScrollView.addView(mPageFrame);
		mScrollView.setSmoothScrollingEnabled(true);
		//		mPageFrame.setBackgroundColor(Color.GREEN);
		// 下画面を作る
		mPageView = new ImageView(this);
		mMarkerView = new ImageView(this);
		mPageFrame.addView(mPageView);
		mPageFrame.addView(mMarkerView);
		mPageView.setBackgroundColor(Color.LTGRAY);
		//		mMarkerView.setBackgroundColor(Color.MAGENTA);
		//		// スプラッシュ表示のため
		//		mLinearLayout.setAlpha(0f);
		//		// ここで初めて描画

		setContentView(mLinearLayout);
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Fun.log("onWindowFocusChanged()");
		super.onWindowFocusChanged(hasFocus);
		mRW = mLinearLayout.getWidth();
		mRH = mLinearLayout.getHeight();
		Fun.log(mRW);
		Fun.log(mRH);
		
		final int count  = mLinearLayout.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mLinearLayout.getChildAt(i);
			LayoutParams params = view.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
			view.setLayoutParams(params);
		}
		{
			LayoutParams params = mTickerFrame.getLayoutParams();
			params.width = (int)mRW;
			params.height = (int)mRH / 2;
			mTickerFrame.setLayoutParams(params);
		}
		{
			LayoutParams params = mScrollView.getLayoutParams();
			params.width = (int)mRW;
			params.height = (int)mRH / 2;
			mScrollView.setLayoutParams(params);
			mPageFrame.setMinimumHeight((int)mRH / 2);
		}
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		isWindowFocusChanged = true;
	}
	
	public void setImage(Bitmap _bmp) {
		Fun.log("setImage()");
		mPageBitmap = _bmp;
		
		final float pageW = (float) mPageBitmap.getWidth();
		Fun.log("pageW:"+pageW);
		final float pageH = (float) mPageBitmap.getHeight();
		Fun.log("pageH:"+pageH);
		final float ratio = mRH / pageH;
		Fun.log("ratio:"+ratio);
		final float small_w = pageW * ratio;
		Fun.log("small_w:"+small_w);
		final float scale_ratio = mRW / small_w;
		Fun.log("scale_ratio:"+scale_ratio);
//		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), false);
		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), false);
		Fun.log("(mRW * (pageH/pageW)):"+(mRW * (pageH/pageW)));
		mPageView.setImageBitmap(mScaledPageBitmap);
		// マーカーの処理
		//			markedPage = Bitmap.createScaledBitmap(markerBitmap, (int)dW, (int)(dW * (h/w)), false);
		//			markerview.setImageBitmap(markedPage);
		mPageFrame.setScaleX(scale_ratio);
		mPageFrame.setScaleY(scale_ratio);
		if(!mBook.isRecognized()) {
			Fun.log("mBook.isRecognized() == false");
			// レイアウト認識がまだだったらレイアウト認識を行う。
			// レイアウト認識中は全画面でページを表示してあげる。
			waitForRecognize = new CountDownTimer(20000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {
					Fun.log(String.valueOf(millisUntilFinished));
					if(mBook.isRecognized()) {
						waitForRecognize.cancel();
						float linemid = (mBook.getPageLayout().get(mBook.getCurLine()).getBottom() + mBook.getPageLayout().get(mBook.getCurLine()).getTop()) / 2;
						Fun.log("linemid:"+linemid);
						float distance = pageH / 2 - linemid;
						Fun.log("distance:"+distance);
						float i = distance * (mRW / pageW);
						Fun.log("i:"+i);
						mPageFrame.setY(i);
						AnimatorSet set = new AnimatorSet();
						ObjectAnimator anim1 = ObjectAnimator.ofFloat(mTickerFrame, "height", mRH / 2);
						ObjectAnimator anim2 = ObjectAnimator.ofFloat(mScrollView, "height", mRH / 2);
						ObjectAnimator anim3 = ObjectAnimator.ofFloat(mPageFrame, "y", i);
						set.playTogether(anim1, anim2, anim3);
						set.setDuration(500);
						set.start();
						
						if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
						else { mAnimatingTicker = mTicker1; }
						mLineW = mBook.getPageLayout().get(mBook.getCurLine()).getRight() - mBook.getPageLayout().get(mBook.getCurLine()).getLeft();
						Fun.log("mLineW:"+mLineW);
						mLineH = mBook.getPageLayout().get(mBook.getCurLine()).getBottom() - mBook.getPageLayout().get(mBook.getCurLine()).getTop();
						Fun.log("mLineH:"+mLineH);
						mAnimatingTicker.setImageBitmap(Bitmap.createBitmap(mPageBitmap, mBook.getPageLayout().get(mBook.getCurLine()).getLeft(), mBook.getPageLayout().get(mBook.getCurLine()).getTop(), mLineW, mLineH));
						mTextZoom = ((float)mRH / 2f) / ((float)mLineH * ((float)mRW / (float)mLineW));
						Fun.log("mTextZoom:"+mTextZoom);
						mAnimatingTicker.setScaleX(mTextZoom);
						mAnimatingTicker.setScaleY(mTextZoom);
						mTickerWidth = (int) (mRW * ((float)mLineW/(float)mLineH)); // 修正
						mTickerHeight = (int) (mRH / 2); // 修正
						Fun.log("mTickerWidth: "+mTickerWidth);
						Fun.log("mTickerHeight: "+mTickerHeight);
//						mAnimatingTicker.setX(mTickerWidth);
						mAnimatingTicker.setY(0);
						// アニメーション開始
//						animation(0);
					}
				}
				@Override
				public void onFinish() {
					Fun.log("20秒待ったけど終わらなかった");
				}
			}.start();
		}

	}
	
	public void animation(long startDelay) {
		if(mPending) { return; }
		mAnimation = new AnimatorSet();
		ObjectAnimator move = null;
		mDuration = 530;
		Fun.log("mDuration:"+mDuration);
		
		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
		else { mAnimatingTicker = mTicker1; }

		move = ObjectAnimator.ofFloat(mAnimatingTicker, "x", mTickerWidth, -mTickerWidth);
		if (mTickerWidth > mTickerHeight) {
			mDuration *= ((float)mTickerWidth / (float)mTickerHeight); // intへのキャストを削除
		} else {
			mDuration *= ((float)mTickerHeight / (float)mTickerWidth); // intへのキャストを削除
		}
		Fun.log("mDuration:"+mDuration);
		move.setDuration(mDuration);
		move.setInterpolator(new LinearInterpolator());
		mAnimation.addListener(this);
//		mAnimationFlag = AnimationFlag.loop;
		mAnimation.setStartDelay(startDelay);
		mAnimation.start();
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
			Fun.log("startdelay: " + (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));
			animation((long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));	
//		}
	}
	
	public void finishAnimation() {
		mAnimation.end();
	}

}
