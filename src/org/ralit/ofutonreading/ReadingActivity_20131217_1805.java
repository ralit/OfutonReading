//package org.ralit.ofutonreading;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.animation.Animator;
//import android.animation.Animator.AnimatorListener;
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.support.v4.app.NavUtils;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.ViewGroup.LayoutParams;
//import android.view.Window;
//import android.view.animation.LinearInterpolator;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//
//public class ReadingActivity_20131217_1805 extends Activity implements LineEndListener, LayoutFinishedListener{
//
//	private BookManager mBook;
//	// レイアウトとビュー
//	private LinearLayout mLinearLayout;
//	private TickerView mTickerView;
//	private ScrollView mScrollView;
//	private PageView mPageView;
//	// その他
//	//	private AnimatorSet mAnimation;
//	private float mTextZoom;
//	private int mTickerWidth;
//	private int mTickerHeight;
//	private ImageView mAnimatingTicker; 
//	
//	private int mDuration;
//	private int mLineW;
//	private int mLineH;
//	private float mRW;
//	private float mRH;
//	private boolean mPending = false;
//	private CountDownTimer initialSetImagetimer;
//	private CountDownTimer waitForRecognize;
//	private boolean isWindowFocusChanged = false;
//	private Timer timer;
//	
//	
//	private Handler handler = new Handler();
//	
//	
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setRequestedOrientation(Configuration.ORIENTATION_LANDSCAPE);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		Fun.log(getResources().getConfiguration().orientation);
//
//		
//		
//		Intent intent = getIntent();
//		if (intent != null) {
//			String fileName = intent.getStringExtra("fileName");
//			String filePath = intent.getStringExtra("filePath");
//			mBook = new BookManager(fileName, filePath, this);
//			initialize();
////			mPageView.setImage(mBook.getBitmap(mBook.getCurPage()));
//			
//			timer = new Timer();
//			timer.schedule(new TimerTask() {
//				@Override public void run() {
//					if(mBook.getReadyForSetImage() && isWindowFocusChanged) {
//						timer.cancel();
//						handler.post(new Runnable() {
//							@Override public void run() {
//								Fun.log("TickerViewのsetimageを呼び出すタイマー");
//								mTickerView.setImage(mPageView.getImage());
//							}
//						});
//					}
//				}
//			}, 0, 100);
//		}
//		
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		//		getMenuInflater().inflate(R.menu.reading, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
//			NavUtils.navigateUpFromSameTask(this);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	private void initialize() {
//		mLinearLayout = new LinearLayout(this);
//		mTickerView = new TickerView(this, mBook, this);
//		mScrollView = new ScrollView(this);
//		mPageView = new PageView(this, mBook, this);
//		mLinearLayout.addView(mTickerView);
//		mLinearLayout.addView(mScrollView);
//		mScrollView.addView(mPageView);
//		setContentView(mLinearLayout);
//	}
//
//
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		Fun.log("onWindowFocusChanged()");
//		super.onWindowFocusChanged(hasFocus);
//		mRW = mLinearLayout.getWidth();
//		mRH = mLinearLayout.getHeight();
//		
//		mPageView.setMinimumHeight((int)mRH/2);
//		
//		final int count  = mLinearLayout.getChildCount();
//		for (int i = 0; i < count; i++) {
//			View view = mLinearLayout.getChildAt(i);
//			LayoutParams params = view.getLayoutParams();
//			params.width = LayoutParams.MATCH_PARENT;
//			params.height = LayoutParams.MATCH_PARENT;
//			view.setLayoutParams(params);
//		}
//		{
//			LayoutParams params = mTickerView.getLayoutParams();
//			params.width = (int)mRW;
//			params.height = (int)mRH / 2;
//			mTickerView.setLayoutParams(params);
//		}
//		{
//			LayoutParams params = mScrollView.getLayoutParams();
//			params.width = (int)mRW;
//			params.height = (int)mRH / 2;
//			mScrollView.setLayoutParams(params);
//		}
//		
//		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
//		
//		{
//			LayoutParams params = mPageView.getLayoutParams();
//			params.width = LayoutParams.MATCH_PARENT;
//			params.height = LayoutParams.WRAP_CONTENT;
//			mPageView.setLayoutParams(params);
//		}
//		Fun.log("onWindowFocusChanged");
//		Fun.log(mPageView.getHeight());
//		Fun.log(mPageView.getWidth());
//		isWindowFocusChanged = true;
//	}
//
//	@Override
//	public void onLineEnd() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPageViewLayoutFinished() {
////		mPageView.setimage(BitmapFactory.decodeResource(getResources(), R.drawable.usagi));
//		mPageView.setImage(mBook.getBitmap(mBook.getCurPage()));
//		mScrollView.invalidate();
//		Fun.log("onPageViewLayoutFinished");
//		Fun.log(mPageView.getHeight());
//		Fun.log(mPageView.getWidth());
//		Fun.log(mScrollView.getHeight());
//		Fun.log(mScrollView.getWidth());
//		Fun.log(mTickerView.getHeight());
//		Fun.log(mTickerView.getWidth());
//	}
//
//}
