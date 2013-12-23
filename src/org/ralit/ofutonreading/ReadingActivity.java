package org.ralit.ofutonreading;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ReadingActivity extends Activity implements LineEndListener, RecognizeFinishedListener, Con, TimerCallbackListener, AnimatorCallbackListener{

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

	protected State state;
	private static final int FlingSpeed = 100;
	private static final int FlingFromEdgePixels = 14;
	private static final int FlingMinDistance = 80;
	private CountDownTimer keyEventTimer; // BackボタンPress時の有効タイマー
	private boolean pressed = false; // 一度目のBackボタンが押されたかどうかを判定するフラグ
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 寝落ちしたときのことも考えたい

		Intent intent = getIntent();
		if (intent != null) {
			Fun.log("intent != null");
			fileName = intent.getStringExtra("fileName");
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
		
		keyEventTimer = new CountDownTimer(1000, 100) {
			@Override
			public void onTick(long millisUntilFinished) {
				Fun.log("CountDown");
			}
			@Override
			public void onFinish() {
				pressed = false;
			}
		};
		
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
		case R.id.ofuton_markedlist:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MarkedListActivity.class);
			intent.putExtra("bookName", fileName);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
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
			mPageView = new PageView(this, mBook, mRW, mRH, mBook.getBitmap(mBook.getCurPage()));
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
		mPageView.scrollToCurrentLine();
	}

	@Override
	public void onPageEnd() {
		state.onChangePage(ReadingActivity.this, mBook.getCurPage() + 1);
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
				if (ev2.getX() - ev1.getX() > metrics.density * FlingMinDistance && Math.abs(vx) > metrics.density * FlingSpeed) {
					state.onChangeLine(ReadingActivity.this, 0);
				} else if (ev1.getX() - ev2.getX() > metrics.density * FlingMinDistance && Math.abs(vx) > metrics.density * FlingSpeed) {
					state.onChangeLine(ReadingActivity.this, 1); // 第2引数は行番号を指定するつもりだったが、1行ずつ移動することにして、1は進む、0は戻るとする。
				}
			} else {
				if (mRW - metrics.density * FlingFromEdgePixels < ev1.getX() && Math.abs(vx) > metrics.density * FlingSpeed) {
					state.onChangePage(ReadingActivity.this, mBook.getCurPage() + 1);
				} else if (ev1.getX() < metrics.density * FlingFromEdgePixels && Math.abs(vx) > metrics.density * FlingSpeed) {
					state.onChangePage(ReadingActivity.this, mBook.getCurPage() - 1);
				} else if (Math.abs(vy) < Math.abs(vx)) {
					state.onMark(ReadingActivity.this, ev1, ev2);
				}
			}
			return false;
		}
	};

	@Override
	public void onRecognizeFinished() {
		Fun.log("onRecognizeFinished");
		changeState(StateNormal.getInstance());
		new SimpleTimer(this).start(100, "onRecognizeFinished");
	}

	@Override
	public void changePage(int page) {
		Fun.log("Con.changePage");
		if (page < 0) {
			Toast.makeText(this, "最初のページです", Toast.LENGTH_SHORT).show();
		} else if (mBook.getPageMax() < page) {
			Toast.makeText(this, "最後のページです", Toast.LENGTH_SHORT).show();
		} else {
			int previousPage = mBook.getCurPage();			
			mBook.setCurPage(page);
			mPageView.setImage(mBook.getBitmap(mBook.getCurPage()));
			
			if(previousPage < page) { new SimpleAnimator(this).start("changePage", 500, mPageView, "x", mRW, 0); } 
			else { new SimpleAnimator(this).start("changePage", 500, mPageView, "x", -mRW, 0); }
			
			mTickerView.destroy();
			if(mBook.isRecognized()) {
				mTickerView.setImage(mPageView.getImage());
			}
		}
	}

	@Override
	public void changeState(State state) {
		this.state = state;
	}

	@Override
	public void timerCallback(String message, SimpleTimer timer) {
		if (message.equals("onRecognizeFinished")) {
			if(isWindowFocusChanged) {
				timer.cancel();
				mTickerView.setImage(mPageView.getImage());			
			}
		}
	}

	@Override
	public void changeLine(int line) {
		if (line == 1) {
			mTickerView.nextLine();
		} else if (line == 0) {
			mTickerView.previousLine();
		}
	}

	@Override
	public void mark(MotionEvent ev1, MotionEvent ev2) {
		Fun.log("mark");
		Point screen = new Point();
//		getWindowManager().getDefaultDisplay().getSize(screen);
		screen = Fun.getDisplaySize(ReadingActivity.this);
		Fun.log("screen: " + screen.toString());
		mPageView.mark(ev1, ev2, screen);
	}

	@Override
	public void animatorCallBack(String message, SimpleAnimator animator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Backボタン検知
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(!pressed) {
				// Timerを開始
				keyEventTimer.cancel(); // いらない？
				keyEventTimer.start();
				// 終了する場合, もう一度タップするようにメッセージを出力する
				Toast.makeText(this, "終了する場合は、もう一度バックボタンを押してください", Toast.LENGTH_SHORT).show();
				pressed = true;
				return false;
			}
			// pressed=trueの時、通常のBackボタンで終了処理.
			return super.dispatchKeyEvent(event);
		}
		// Backボタンに関わらないボタンが押された場合は、通常処理.
		return super.dispatchKeyEvent(event);
	}

}
