package org.ralit.ofutonreading;

import java.util.ArrayList;

import org.ralit.ofutonreading.MarkedListActivity.fsrController;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fuetrek.fsr.FSRServiceEventListener;
import com.fuetrek.fsr.FSRServiceOpen;
import com.fuetrek.fsr.FSRServiceEnum.BackendType;
import com.fuetrek.fsr.FSRServiceEnum.EventType;
import com.fuetrek.fsr.FSRServiceEnum.Ret;
import com.fuetrek.fsr.entity.AbortInfoEntity;
import com.fuetrek.fsr.entity.ConstructorEntity;
import com.fuetrek.fsr.entity.RecognizeEntity;
import com.fuetrek.fsr.entity.ResultInfoEntity;
import com.fuetrek.fsr.entity.StartRecognitionEntity;

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

	public State state;
	private static final int FlingSpeed = 100;
	private static final int FlingFromEdgePixels = 14;
	private static final int FlingMinDistance = 80;
	private CountDownTimer keyEventTimer; // BackボタンPress時の有効タイマー
	private boolean pressed = false; // 一度目のBackボタンが押されたかどうかを判定するフラグ
	private String fileName;
	//	private HomeButtonReceiver mHomeButtonReceiver;
	private boolean isPaused = false;

	// >>> 音声認識
	private Handler handler_;
	private Button buttonStart_;
	private ProgressBar progressLevel_;
	private TextView textResult_;
	private fsrController controller_ = new fsrController();
	private static final BackendType backendType_ = BackendType.D;
	private Activity activity_ = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 寝落ちしたときのことも考えたい
		//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
			changeState(StateRecognize.getInstance());

			//			mHomeButtonReceiver = new HomeButtonReceiver();
			//			IntentFilter filter = new IntentFilter();
			//			filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			//			registerReceiver(mHomeButtonReceiver, filter);
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

		// 音声認識用
		handler_ = new Handler();
		buttonStart_ = new Button(this);
		progressLevel_ = new ProgressBar(this);
		textResult_ = new TextView(this);
		activity_ = this;

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
			startActivityForResult(intent, 2014);
			return true;
		case R.id.ofuton_command:
			controller_ = new fsrController();
			controller_.start();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2014) {
			if(resultCode == RESULT_OK) {
				int page = data.getIntExtra("page", -1);
				if (page != -1) {
					//					mTickerView.destroy();
					//					if(mBook.mRecognized) {
					//						mTickerView.setImage(null);
					//					}
					toast("p." + page);
					//					state.onChangePage(ReadingActivity.this, page);
					new SimpleTimer(this).start(200, String.valueOf(page));
				}
			}
		}
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
			Fun.log("onRecognizeFinished(state): " + state);
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
	public void changePage(int page) {
		Fun.log("Con.changePage");
		if (page < 0) {
			Toast.makeText(this, getString(R.string.ofuton_first_page), Toast.LENGTH_SHORT).show();
		} else if (mBook.getPageMax() < page) {
			Toast.makeText(this, getString(R.string.ofuton_last_page), Toast.LENGTH_SHORT).show();
		} else {
			changeState(StateRecognize.getInstance());
			
			int previousPage = mBook.getCurPage();			
			mBook.setCurPage(page);
			mPageView.setImage(mBook.getBitmap(mBook.getCurPage()));

			if(previousPage < page) { new SimpleAnimator(this).start("changePage", 500, mPageView, "x", mRW, 0); } 
			else { new SimpleAnimator(this).start("changePage", 500, mPageView, "x", -mRW, 0); }

			mTickerView.destroy();
			if(mBook.isRecognized()) {
				mTickerView.setImage(mPageView.getImage(), mBook.getMarginRatio());
			}
		}
	}

	@Override
	public void changeState(State state) {
		this.state = state;
	}

	@Override
	public void timerCallback(final String message, SimpleTimer timer) {
		if (message.equals("onRecognizeFinished")) {
			if(isWindowFocusChanged) {
				timer.cancel();
				mTickerView.setImage(mPageView.getImage(), mBook.getMarginRatio());
				changeState(StateNormal.getInstance());
			}
		} else { // もっときれいに
			timer.cancel();
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (Fun.match(message, "[0-9]+", false)) {
						state.onChangePage(ReadingActivity.this, Integer.valueOf(message));
					}
				}
			});
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
		Fun.log("dispatchKeyEvent");
		// Backボタン検知
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
				if(Build.MODEL.equals("M100")) {
					controller_ = new fsrController();
					controller_.start();
					return true;
				}
				if(!pressed) {
					Fun.log("!pressed");
					// Timerを開始
					keyEventTimer.cancel(); // いらない？
					keyEventTimer.start();
					// 終了する場合, もう一度タップするようにメッセージを出力する
					Toast.makeText(this, getString(R.string.ofuton_back_key), Toast.LENGTH_SHORT).show();
					pressed = true;
					return false;
				} else {
					Fun.log("else");
					// pressed=trueの時、通常のBackボタンで終了処理.
					mTickerView.destroy();
					return super.dispatchKeyEvent(event);
				}
			}
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
				state.onChangeLine(ReadingActivity.this, 0);
				return true;
			}
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
				state.onChangeLine(ReadingActivity.this, 1);
				return true;
			}
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
				controller_ = new fsrController();
				controller_.start();
				return true;
			}
		}
		// Backボタンに関わらないボタンが押された場合は、通常処理.
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		Fun.log("onUserLeaveHint");
		mTickerView.destroy();
		isPaused = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Fun.log("onResume");
		if(isPaused) {
			if(mBook.mRecognized) {
				mTickerView.setImage(null, mBook.getMarginRatio());
			}
			isPaused = false;
		}

	}

	@Override
	public void onRecognizeFinished(int recognizingPage) {
		Fun.log("onRecognizeFinished");
		changeState(StateNormal.getInstance());
		//		なぜかmBook.getCurPage()がNullPointerExceptionになる
		//		Fun.log(mBook.getCurPage());
		//		if(recognizingPage == mBook.getCurPage()) {
		new SimpleTimer(this).start(100, "onRecognizeFinished");
		//		}
	}



	/*
	 * http://wada811.blogspot.com/2013/08/home-button-pressing-detection-in-android.html
	 */
	//	private class HomeButtonReceiver extends BroadcastReceiver {
	//		@Override
	//		public void onReceive(Context context, Intent intent){
	//			Toast.makeText(getApplicationContext(), "ホームボタンが押されました", Toast.LENGTH_LONG).show();
	//			mTickerView.destroy();
	//			finish();
	//		}
	//	}
	//
	//	@Override
	//	public void onPause(){
	//		super.onPause();
	//		unregisterReceiver(mHomeButtonReceiver);
	//	}

	// 音声認識用
	public final void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	// FSRServiceの待ち処理でブロッキングする実装としている為、
	// UI更新を妨げないよう別スレッドとしている。
	public class fsrController extends Thread implements FSRServiceEventListener {
		FSRServiceOpen fsr_;
		SyncObj event_CompleteConnect_ = new SyncObj();
		SyncObj event_CompleteDisconnect_ = new SyncObj();
		SyncObj event_EndRecognition_ = new SyncObj();
		Ret ret_;
		String result_;

		// 認識完了時の処理
		// (UIスレッドで動作させる為にRunnable()を使用している)
		final Runnable notifyFinished = new Runnable() {
			public void run() {
				try {
					// 念のためスレッドの完了を待つ
					controller_.join();
				} catch (InterruptedException e) {
				}
				textResult_.append("***Result***" + System.getProperty("line.separator"));
				textResult_.append(controller_.result_);
				// >>> saito
				Log.d("debug","controller_.result_=" + controller_.result_);
				//               mOverlayView.setMsg(controller_.result_);

				String speakmessage = controller_.result_;
				//				Filter filter = adapter.getFilter();
				//				filter.filter(speakmessage);
				//				toast(speakmessage);
				// <<< 
				if (speakmessage.contains("戻れ")) {
					toast("戻れ");
					state.onChangePage(ReadingActivity.this, mBook.getCurPage() - 1);
				} else if (speakmessage.contains("進め")) {
					toast("進め");
					state.onChangePage(ReadingActivity.this, mBook.getCurPage() + 1);
				} else if (speakmessage.contains("ページ")) {
					toast(speakmessage);
					ArrayList<ArrayList<String>> voicePageList = Fun.matchGroup(speakmessage, "([０１２３４５６７８９]+)", false);
					String voicePageStr = voicePageList.get(0).get(0);
					int dif = '０' - '0';
					char[] charArray = voicePageStr.toCharArray();
					StringBuilder builder = new StringBuilder();
					for (char c : charArray) {
						builder.append((char)(c - dif));
					}
					int voicePage = Integer.valueOf(builder.toString());
					if(voicePage <= mBook.getPageMax()) {
						state.onChangePage(ReadingActivity.this, voicePage);
					} else {
						toast(getString(R.string.ofuton_exceeds_maxpage));
					}
				}
				buttonStart_.setEnabled(true);

			}
		};


		// 認識処理
		@Override
		public void run() {
			result_ = "";
			try {
				result_=execute();
			} catch (Exception e) {
				result_ = "(error)";
				e.printStackTrace();
			}
			handler_.post(notifyFinished);
		}

		/**
		 * 認識処理
		 *
		 * 現状は毎回インスタンス生成～destroy()を実施しているが、
		 * 繰り返し認識させる場合は、以下のように制御した方がオーバーヘッドが少なくなる
		 * アプリ起動時：インスタンス生成～connectSession()
		 * 認識要求時　：startRecognition()～getSessionResult()
		 * アプリ終了時：destroy()
		 *
		 * @throws Exception
		 */
		public String execute() throws Exception {

			try{
				final ConstructorEntity construct = new ConstructorEntity();
				construct.setContext(activity_);

				// 別途発行されるAPIキーを設定してください(以下の値はダミーです)
				construct.setApiKey(ApiKey.getApiKey());

				construct.setSpeechTime(10000);
				construct.setRecordSize(240);
				construct.setRecognizeTime(10000);

				// インスタンス生成
				// (thisは FSRServiceEventListenerをimplementsしている。)
				if( null == fsr_ ){
					fsr_ = new FSRServiceOpen(this, this, construct);
				}

				// connect
				fsr_.connectSession(backendType_);
				event_CompleteConnect_.wait_();
				if( ret_ != Ret.RetOk ){
					Exception e = new Exception("filed connectSession.");
					throw e;
				}

				// 認識開始

				final StartRecognitionEntity startRecognitionEntity = new StartRecognitionEntity();
				startRecognitionEntity.setAutoStart(true);
				startRecognitionEntity.setAutoStop(true);				// falseにする場合はUIからstopRecognition()実行する仕組みが必要
				startRecognitionEntity.setVadOffTime((short) 500);
				startRecognitionEntity.setListenTime(0);
				startRecognitionEntity.setLevelSensibility(10);

				// 認識開始
				fsr_.startRecognition(backendType_, startRecognitionEntity);
				handler_.post(new Runnable() {
					@Override
					public void run() {
						toast("音声認識開始");
					}
				});

				// 認識完了待ち
				// (setAutoStop(true)なので発話終了を検知して自動停止する)
				event_EndRecognition_.wait_();

				// 認識結果の取得
				RecognizeEntity recog = fsr_.getSessionResultStatus(backendType_);
				String result="(no result)";
				if( recog.getCount()>0 ){
					ResultInfoEntity info=fsr_.getSessionResult(backendType_, 1);
					result = info.getText();
				}

				// 切断
				fsr_.disconnectSession(backendType_);
				event_CompleteDisconnect_.wait_();

				return result;
			} catch (Exception e) {
				//                showErrorDialog(e);
				throw e;
			}finally{
				if( fsr_!=null ){
					fsr_.destroy();
					fsr_=null;
				}
			}
		}

		@Override
		public void notifyAbort(Object arg0, AbortInfoEntity arg1) {
			Exception e = new Exception("Abort!!");
			//            showErrorDialog(e);
		}

		@Override
		public void notifyEvent(final Object appHandle, final EventType eventType, final BackendType backendType, Object eventData) {

			switch(eventType){

			case CompleteConnect:
				// 接続完了
				ret_ = (Ret)eventData;
				event_CompleteConnect_.notify_();
				break;

			case CompleteDisconnect:
				// 切断完了
				event_CompleteDisconnect_.notify_();
				break;

			case NotifyEndRecognition:
				// 認識完了
				event_EndRecognition_.notify_();
				Log.d("debug", "認識終了");
				break;

			case NotifyLevel:
				// レベルメータ更新
				int level = (Integer)eventData;
				progressLevel_.setProgress(level);
				break;
			}
		}
	}
}
