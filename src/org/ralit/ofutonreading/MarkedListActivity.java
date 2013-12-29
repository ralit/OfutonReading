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
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
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
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Fun.log("onWindowFocusChanged() in ReadingActivity");
		super.onWindowFocusChanged(hasFocus);
		layout();
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

	void createImageList() {
		File dir = new File(Fun.DIR + bookName + Fun.MARKER);
		File[] filelist = dir.listFiles();
		try {
			ArrayList<ImageItem> array = new ArrayList<ImageItem>();
			for (int i = 0; i < filelist.length; i++) {
				FileInputStream fis = new FileInputStream(filelist[i]);
				Bitmap bmp = BitmapFactory.decodeStream(fis);
				ImageItem item = new ImageItem(bmp, filelist[i].getName());
				array.add(item);
			}
			ImageAdapter adapter = new ImageAdapter(this, 0, array);
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