package org.ralit.ofutonreading;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ReadingActivity_20131221_1642 extends Activity implements LineEndListener, LayoutFinishedListener, RecognizeFinishedListener{

	private BookManager mBook;
	// レイアウトとビュー
	private LinearLayout mLinearLayout;
	private TickerView mTickerView;
	private PageView mPageView;
	// その他
	private float mRW;
	private float mRH;
	private boolean isWindowFocusChanged = false;
	private Handler handler = new Handler();
	private GestureDetector gesture;
	private Timer timerForSetImageToTickerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 寝落ちしたときのことも考えたい

		Intent intent = getIntent();
		if (intent != null) {
			Fun.log("intent != null");
			String fileName = intent.getStringExtra("fileName");
			String filePath = intent.getStringExtra("filePath");
			mBook = new BookManager(fileName, filePath, this, this);
			mLinearLayout = new LinearLayout(this);
			setContentView(mLinearLayout);
			{
				LayoutParams params = mLinearLayout.getLayoutParams();
				params.width = LayoutParams.MATCH_PARENT;
				params.height = LayoutParams.MATCH_PARENT;
				mLinearLayout.setLayoutParams(params);
			}
			gesture = new GestureDetector(this, gestureListener);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.reading, menu);
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
		mLinearLayout.removeAllViews();
		mTickerView = new TickerView(this, mBook, this, mRW, mRH);
		mPageView = new PageView(this, mBook, this, mRW, mRH, mBook.getBitmap(mBook.getCurPage()));
		mLinearLayout.addView(mTickerView);
		mLinearLayout.addView(mPageView);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Fun.log("onWindowFocusChanged() in ReadingActivity");
		super.onWindowFocusChanged(hasFocus);
		layout();
	}
	
	private void layout() {
		if (mRW != mLinearLayout.getWidth() || mRH != mLinearLayout.getHeight()) {
			mRW = mLinearLayout.getWidth();
			mRH = mLinearLayout.getHeight();
			Fun.log(mRW);
			Fun.log(mRH);

			mTickerView = new TickerView(this, mBook, this, mRW, mRH);
			mPageView = new PageView(this, mBook, this, mRW, mRH, mBook.getBitmap(mBook.getCurPage()));
			mLinearLayout.setOrientation(LinearLayout.VERTICAL);
			mLinearLayout.addView(mTickerView);
			mLinearLayout.addView(mPageView);

			{
				LayoutParams params = mTickerView.getLayoutParams();
				params.width = (int)mRW;
				params.height = (int)mRH / 2;
				mTickerView.setLayoutParams(params);
			}
			{
				LayoutParams params = mPageView.getLayoutParams();
				params.width = (int)mRW;
				params.height = (int)mRH / 2;
				mPageView.setLayoutParams(params);
			}
		}
		isWindowFocusChanged = true;
	}

	@Override
	public void onLineEnd() {
		Fun.log("onLineEnd");
		mPageView.scrollToCurrentLine();
	}

	@Override
	public void onPageViewLayoutFinished() {

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		super.dispatchTouchEvent(ev);    
		return gesture.onTouchEvent(ev); 
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Fun.log("onTouchEvent in activity");
		return gesture.onTouchEvent(ev);
	}

	private final SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent ev1, MotionEvent ev2, float vx, float vy) {
			DisplayMetrics metrics = new DisplayMetrics();  
		    getWindowManager().getDefaultDisplay().getMetrics(metrics);
		    
			if (ev1.getY() < mRH / 2) {
				Fun.log("Ticker系Gesture");
				if (ev2.getX() - ev1.getX() > 120 && Math.abs(vx) > 200) {
					// 1行戻る
					//					if (set.getChildAnimations().get(0).isRunning()) { 
					//						if (index > 0) { --index; }
					//					}
					//					set.cancel();
				} else if (ev1.getX() - ev2.getX() > 120 && Math.abs(vx) > 400) {
					// 1行進む
					Fun.log("1行進む in activity");
					mTickerView.mAnimatorList.getFirst().end();
				}
			} else {
				Fun.log("PageView系Gesture");
				Fun.log(ev1.getX());
				if (mRW - metrics.density * 14 < ev1.getX() && Math.abs(vx) > 400) {
					mBook.setCurPage(mBook.getCurPage() + 1);
//					mPageView.setImage(mBook.getBitmap(mBook.getCurPage()));
//					mTickerView.destroy();
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
//							initialize();
							mLinearLayout.removeAllViews();
							layout();
						}
					});
					
					timerForSetImageToTickerView = new Timer();
					timerForSetImageToTickerView.schedule(new TimerTask() {
						@Override
						public void run() {
							if(mBook.isRecognized()) {
								timerForSetImageToTickerView.cancel();
								mTickerView.setImage(mPageView.getImage());			
							}
						}
					}, 0, 100);
				} else if (ev1.getX() < metrics.density * 14 && Math.abs(vx) > 400) {
					mBook.setCurPage(mBook.getCurPage() - 1);
//					mPageView.setImage(mBook.getBitmap(mBook.getCurPage()));
//					mTickerView.destroy();
//					
					initialize();
					timerForSetImageToTickerView = new Timer();
					timerForSetImageToTickerView.schedule(new TimerTask() {
						@Override
						public void run() {
							if(mBook.isRecognized()) {
								timerForSetImageToTickerView.cancel();
								mTickerView.setImage(mPageView.getImage());			
							}
						}
					}, 0, 100);
				}
			}
			return false;
		}
	};


	@Override
	public void onRecognizeFinished() {
		Fun.log("onRecognizeFinished");
		timerForSetImageToTickerView = new Timer();
		timerForSetImageToTickerView.schedule(new TimerTask() {
			@Override
			public void run() {
				if(isWindowFocusChanged) {
					timerForSetImageToTickerView.cancel();
					mTickerView.setImage(mPageView.getImage());			
				}
			}
		}, 0, 100);
	}

}
