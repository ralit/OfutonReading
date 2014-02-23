package org.ralit.ofutonreading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class MarkedListActivity extends Activity implements MarkerEndListener{

	private ListView listview;
	private String bookName;
	private float mRW;
	private float mRH;
	private LinearLayout root;
	private boolean isWindowFocusChanged = false;
	private MarkerTickerView markerTickerView;
	private int mPosition;
	private CountDownTimer keyEventTimer; // BackボタンPress時の有効タイマー
	private boolean pressed = false; // 一度目のBackボタンが押されたかどうかを判定するフラグ
	private boolean isPaused = false;
	private ImageAdapter adapter;

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
		Fun.log("onCreate");

		Intent intent = getIntent();
		if (intent != null) {
			bookName = intent.getStringExtra("bookName");
		}

		root = new LinearLayout(this);
		root.setBackgroundColor(Color.DKGRAY);
		root.setOrientation(LinearLayout.VERTICAL);
		setContentView(root);

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

		handler_ = new Handler();
		buttonStart_ = new Button(this);
		progressLevel_ = new ProgressBar(this);
		textResult_ = new TextView(this);
		activity_ = this;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Fun.log("onWindowFocusChanged() in ReadingActivity");
		super.onWindowFocusChanged(hasFocus);
		layout();
		//		listview.setFilterText("情報");
		//		Filter filter = adapter.getFilter();
		//		filter.filter("となり");
	}

	private void layout() {
		if (mRW != root.getWidth() || mRH != root.getHeight()) {
			mRW = root.getWidth();
			mRH = root.getHeight();
			Fun.log(mRW);
			Fun.log(mRH);


			//			markerTickerView = new MarkerTickerView(this, this, mRW, mRH);
			//			root.addView(markerTickerView);


			listview = new ListView(this);
			listview.setTextFilterEnabled(true);
			listview.setDividerHeight(0);
			root.addView(listview);
			createImageList();


			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					//					TextView textView = (TextView) ((LinearLayout)view).getChildAt(1);
					//					String fileName = String.valueOf(textView.getText());
					if(root.getChildCount() < 2) {
						markerTickerView = new MarkerTickerView(MarkedListActivity.this, MarkedListActivity.this, mRW, mRH);
						root.addView(markerTickerView);
						{
							LayoutParams params = markerTickerView.getLayoutParams();
							params.width = (int)mRW;
							params.height = (int)mRH / 2;
							markerTickerView.setLayoutParams(params);
						}
						{
							LayoutParams params = listview.getLayoutParams();
							params.width = (int)mRW;
							params.height = (int)mRH / 2;
							listview.setLayoutParams(params);
						}
					}

					mPosition = position;
					File dir = new File(Fun.DIR + bookName + Fun.MARKER);
					File[] filelist = dir.listFiles();
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(filelist[mPosition]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Bitmap bmp = BitmapFactory.decodeStream(fis);
					markerTickerView.destroy();
					markerTickerView.setImage(bmp);
				}
			});
			
			listview.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					LinearLayout item = (LinearLayout)view;
					TextView textView = (TextView)(item.getChildAt(1));
					ArrayList<ArrayList<Integer>> pageList =  Fun.matchGroupInt(textView.getText().toString(), "([0-9]+?)_", false);
					int page = pageList.get(0).get(0);
					Intent intent = new Intent();
					intent.putExtra("page", page);
					setResult(RESULT_OK, intent);
					finish();
					return false;
				}
			});
		}
		isWindowFocusChanged = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(markerTickerView != null) {
			markerTickerView.destroy();
		}

	}

	private void createImageList() {
		File dir = new File(Fun.DIR + bookName + Fun.MARKER);
		File[] filelist = dir.listFiles();
		try {
			ArrayList<ImageItem> array = new ArrayList<ImageItem>();
			for (int i = 0; i < filelist.length; i++) {
				FileInputStream fis = new FileInputStream(filelist[i]);
				Bitmap bmp = BitmapFactory.decodeStream(fis);
				ImageItem item = new ImageItem(bmp, filelist[i].getName());
				Fun.log(item.toString());
				array.add(item);
			}
			adapter = new ImageAdapter(this, 0, array);
			listview.setAdapter(adapter);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.marked_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.ofuton_search:
			Fun.log("検索");
			controller_ = new fsrController();
			controller_.start();
			////			searchPopup search = new searchPopup(this);
			//			SearchView search = new SearchView(this);
			////			search.setIconifiedByDefault(false);
			//			search.setOnQueryTextListener(this);
			//			PopupWindow popup = new PopupWindow(this);
			//			popup.setWindowLayoutMode(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
			//			popup.setContentView(search);
			////			popup.showAsDropDown(listview);
			//			popup.showAtLocation(root, Gravity.TOP, 0, 100);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMarkerEnd() {
		if(mPosition + 1 < listview.getCount()) {
			mPosition++;
		} else {
			mPosition = 0;
		}

		File dir = new File(Fun.DIR + bookName + Fun.MARKER);
		File[] filelist = dir.listFiles();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filelist[mPosition]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Bitmap bmp = BitmapFactory.decodeStream(fis);
		markerTickerView.setImage(bmp);
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Fun.log("dispatchKeyEvent");
		// Backボタン検知
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
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
					if(markerTickerView != null) {
						markerTickerView.destroy();
					}

					return super.dispatchKeyEvent(event);
				}
			}
		}
		// Backボタンに関わらないボタンが押された場合は、通常処理.
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		Fun.log("onUserLeaveHint");
		if(markerTickerView != null) {
			markerTickerView.destroy();
		}

		isPaused = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Fun.log("onResume");
		if(isPaused) {
			File dir = new File(Fun.DIR + bookName + Fun.MARKER);
			File[] filelist = dir.listFiles();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(filelist[mPosition]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Bitmap bmp = BitmapFactory.decodeStream(fis);
			markerTickerView.destroy();
			markerTickerView.setImage(bmp);
			isPaused = false;
		}	
	}

	//	@Override
	//	public boolean onQueryTextSubmit(String query) {
	//		Fun.log("onQueryTextSubmit");
	//		return false;
	//	}
	//
	//	@Override
	//	public boolean onQueryTextChange(String newText) {
	//		Fun.log("onQueryTextChange");
	//		Fun.log(newText);
	//		return false;
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
				Filter filter = adapter.getFilter();
				filter.filter(speakmessage);
				toast(speakmessage);
				// <<< 
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

class SyncObj{
	boolean isDone=false;

	synchronized void wait_(){
		try {
			// wait_()より前にnotify_()が呼ばれた場合の対策としてisDoneフラグをチェックしている
			while(isDone==false){
				wait(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	synchronized void notify_(){
		isDone=true;
		notify();
	}
}


class ImageItem {
	private Bitmap bitmap_;
	private String fileName_;
	public ImageItem(Bitmap bitmap, String fileName) {
		this.bitmap_ = bitmap;
		fileName_ = fileName;
	}
	public Bitmap getBitmap() {
		return bitmap_;
	}
	public String getFileName() {
		return fileName_;
	}
	@Override
	public String toString() {
		return Fun.matchGroup(fileName_, "[0-9]+?_[0-9]+?_[0-9]+?_[0-9]+?_[0-9]+?_?(.+)?\\.jpg", true).get(0).get(0);
	}

}


class ImageAdapter extends ArrayAdapter<ImageItem> {
	private LayoutInflater layoutInflater_;

	public ImageAdapter(Context context, int textViewResourceId, List<ImageItem> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 特定の行(position)のデータを得る
		ImageItem item = (ImageItem)getItem(position);
		// convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		if (null == convertView) {
			convertView = layoutInflater_.inflate(R.layout.marked_list, null);
		}
		// ImageItemのデータをViewの各Widgetにセットする
		ImageView imageView;
		imageView = (ImageView)convertView.findViewById(R.id.image);
		imageView.setImageBitmap(item.getBitmap());
		TextView textView;
		textView = (TextView)convertView.findViewById(R.id.fileName);
		textView.setText(item.getFileName());
		return convertView;
	}
}

//class searchPopup extends LinearLayout {
//
//	public searchPopup(Context context) {
//		super(context);
//		SearchView search = new SearchView(context);
//		addView(search);
//	}
//	
//}