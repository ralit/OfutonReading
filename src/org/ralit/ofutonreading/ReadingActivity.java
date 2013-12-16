package org.ralit.ofutonreading;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ReadingActivity extends Activity implements LineEndListener, LayoutFinishedListener{

	private BookManager mBook;
	// レイアウトとビュー
	private LinearLayout mLinearLayout;
	private TickerView mTickerView;
	private ScrollView mScrollView;
	private PageView mPageView;
	// その他
	//	private AnimatorSet mAnimation;
	private float mTextZoom;
	private int mTickerWidth;
	private int mTickerHeight;
	private ImageView mAnimatingTicker; 
	
	private int mDuration;
	private int mLineW;
	private int mLineH;
	private float mRW;
	private float mRH;
	private boolean mPending = false;
	private CountDownTimer initialSetImagetimer;
	private CountDownTimer waitForRecognize;
	private boolean isWindowFocusChanged = false;
	private Timer timer;
	
	private Timer animationTimer;
	private Handler handler = new Handler();
	private ObjectAnimator move;
	private long animationDelay = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(Configuration.ORIENTATION_LANDSCAPE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Fun.log(getResources().getConfiguration().orientation);

		Intent intent = getIntent();
		if (intent != null) {
			String fileName = intent.getStringExtra("fileName");
			String filePath = intent.getStringExtra("filePath");
			mBook = new BookManager(fileName, filePath, this);
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override public void run() {
					if(mBook.getReadyForSetImage() && isWindowFocusChanged) {
						handler.post(new Runnable() {
							@Override public void run() {
								timer.cancel();
//								setImage(mBook.getBitmap(mBook.getCurPage()));
							}
						});
					}
				}
			}, 0, 100);
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
		mLinearLayout = new LinearLayout(this);
		mTickerView = new TickerView(this, this);
		mScrollView = new ScrollView(this);
		mPageView = new PageView(this, mBook, this);
		mLinearLayout.addView(mTickerView);
		mLinearLayout.addView(mScrollView);
		mScrollView.addView(mPageView);
		setContentView(mLinearLayout);
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Fun.log("onWindowFocusChanged()");
		super.onWindowFocusChanged(hasFocus);
		mRW = mLinearLayout.getWidth();
		mRH = mLinearLayout.getHeight();
		
		mPageView.setMinimumHeight((int)mRH/2);
		
		final int count  = mLinearLayout.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mLinearLayout.getChildAt(i);
			LayoutParams params = view.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
			view.setLayoutParams(params);
		}
		{
			LayoutParams params = mTickerView.getLayoutParams();
			params.width = (int)mRW;
			params.height = (int)mRH / 2;
			mTickerView.setLayoutParams(params);
		}
		{
			LayoutParams params = mScrollView.getLayoutParams();
			params.width = (int)mRW;
			params.height = (int)mRH / 2;
			mScrollView.setLayoutParams(params);
		}
		
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		
		{
			LayoutParams params = mPageView.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.WRAP_CONTENT;
			mPageView.setLayoutParams(params);
		}
		
		isWindowFocusChanged = true;
	}

	@Override
	public void onLineEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageViewLayoutFinished() {
//		mPageView.setimage(BitmapFactory.decodeResource(getResources(), R.drawable.usagi));
		mPageView.setimage(mBook.getBitmap(mBook.getCurPage()));
	}

//
//	private void afterRecognized(float pageH, float pageW) {
//
//		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
//		else { mAnimatingTicker = mTicker1; }
//		mLineW = mBook.getPageLayout().get(mBook.getCurLine()).getRight() - mBook.getPageLayout().get(mBook.getCurLine()).getLeft();
//		Fun.log("mLineW:"+mLineW);
//		mLineH = mBook.getPageLayout().get(mBook.getCurLine()).getBottom() - mBook.getPageLayout().get(mBook.getCurLine()).getTop();
//		Fun.log("mLineH:"+mLineH);
//		mAnimatingTicker.setImageBitmap(Bitmap.createBitmap(mPageBitmap, mBook.getPageLayout().get(mBook.getCurLine()).getLeft(), mBook.getPageLayout().get(mBook.getCurLine()).getTop(), mLineW, mLineH));
//		mTextZoom = ((float)mRH / 2f) / ((float)mLineH * ((float)mRW / (float)mLineW));
//		Fun.log("mTextZoom:"+mTextZoom);
//		mAnimatingTicker.setScaleX(mTextZoom);
//		mAnimatingTicker.setScaleY(mTextZoom);
//		mTickerWidth = (int) (mRW * ((float)mLineW/(float)mLineH)); // 修正
//		mTickerHeight = (int) (mRH / 2); // 修正
//		Fun.log("mTickerWidth: "+mTickerWidth);
//		Fun.log("mTickerHeight: "+mTickerHeight);
//		mAnimatingTicker.setX(mTickerWidth);
//		mAnimatingTicker.setY(0);
//		// アニメーション開始
//		animation();
//	}
//
//	//	public void animation(long startDelay) {
//	//		if(mPending) { return; }
//	//		mAnimation = new AnimatorSet();
//	//		ObjectAnimator move = null;
//	//		mDuration = 530;
//	//		Fun.log("mDuration:"+mDuration);
//	//		
//	//		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
//	//		else { mAnimatingTicker = mTicker1; }
//	//
//	//		move = ObjectAnimator.ofFloat(mAnimatingTicker, "x", mTickerWidth, -mTickerWidth);
//	//		if (mTickerWidth > mTickerHeight) {
//	//			mDuration *= ((float)mTickerWidth / (float)mTickerHeight); // intへのキャストを削除
//	//		} else {
//	//			mDuration *= ((float)mTickerHeight / (float)mTickerWidth); // intへのキャストを削除
//	//		}
//	//		Fun.log("mDuration:"+mDuration);
//	//		move.setDuration(mDuration);
//	//		move.setInterpolator(new LinearInterpolator());
//	//		mAnimation.addListener(this);
//	////		mAnimationFlag = AnimationFlag.loop;
//	//		mAnimation.setStartDelay(startDelay);
//	//		mAnimation.start();
//	//	}
//
//	public void animation() {
//		if(mPending) { return; }
//		Fun.log("animation()");
//		Fun.log(animationDelay);
//
//		move = null;
//		mDuration = 530;
//		Fun.log("mDuration:"+mDuration);
//
////		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
////		else { mAnimatingTicker = mTicker1; }
//
//		move = ObjectAnimator.ofFloat(mAnimatingTicker, "x", mTickerWidth/2, -mTickerWidth/2);
//		Fun.log(mTickerWidth);
//		if (mTickerWidth > mTickerHeight) {
//			mDuration *= ((float)mTickerWidth / (float)mTickerHeight); // intへのキャストを削除
//		} else {
//			mDuration *= ((float)mTickerHeight / (float)mTickerWidth); // intへのキャストを削除
//		}
//		Fun.log("mDuration:"+mDuration);
//		move.setDuration(mDuration);
//		move.addListener(this);
//		move.setInterpolator(new LinearInterpolator());
//		//		CountDownTimer nextAnimationTimer = new CountDownTimer(startDelay, startDelay) {
//		//
//		//			@Override
//		//			public void onTick(long millisUntilFinished) {
//		//				// TODO Auto-generated method stub
//		//
//		//			}
//		//
//		//			@Override
//		//			public void onFinish() {
//		//				// TODO Auto-generated method stub
//		//				mAnimation.start();
//		//			}
//		//		}.start();
////		move.start();
//		animationTimer = new Timer();
//		animationTimer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						move.start();
//						mBook.setCurLine(mBook.getCurLine() + 1);
//					}
//				});
//			}
//		}, animationDelay);
//	}
//
//	@Override
//	public void onAnimationCancel(Animator animation) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onAnimationEnd(Animator animation) {
//		// TODO Auto-generated method stub
//		//		if (mAnimationFlag == AnimationFlag.loop) {
//		mPending = false;
//		//		} else if (mAnimationFlag == AnimationFlag.layout) {
//		//			updateLayout();
//		//		}
//	}
//
//	@Override
//	public void onAnimationRepeat(Animator animation) {
//		// TODO Auto-generated method stub
//
//	}
//
//	//	@Override
//	//	public void onAnimationStart(Animator animation) {
//	//		// TODO Auto-generated method stub
//	////		if (mAnimationFlag == AnimationFlag.loop) {
//	//			if(0 < animation.getStartDelay()) { mPending = true; } 
//	//			Fun.log("startdelay: " + (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));
//	//			animation((long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));	
//	////		}
//	//	}
//
//	@Override
//	public void onAnimationStart(Animator animation) {
//		animationDelay = (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth));
//		setImage(null);
//	}
//
//	public void finishAnimation() {
//		move.end();
//	}

}
