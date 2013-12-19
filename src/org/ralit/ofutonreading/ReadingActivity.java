package org.ralit.ofutonreading;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class ReadingActivity extends Activity implements LineEndListener, LayoutFinishedListener{

	private BookManager mBook;
	// レイアウトとビュー
	private LinearLayout mLinearLayout;
	private TickerView mTickerView;
	private PageView mPageView;
	// その他
	private float mRW;
	private float mRH;
	private boolean isWindowFocusChanged = false;
	private Timer timer;
	private Handler handler = new Handler();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Intent intent = getIntent();
		if (intent != null) {
			String fileName = intent.getStringExtra("fileName");
			String filePath = intent.getStringExtra("filePath");
			mBook = new BookManager(fileName, filePath, this);
			mLinearLayout = new LinearLayout(this);
			setContentView(mLinearLayout);
			{
				LayoutParams params = mLinearLayout.getLayoutParams();
				params.width = LayoutParams.MATCH_PARENT;
				params.height = LayoutParams.MATCH_PARENT;
				mLinearLayout.setLayoutParams(params);
			}
//			LinearLayout linearLayout = new LinearLayout(this);
//			LayeredImageScrollView layeredImageScrollView1 = new LayeredImageScrollView(this, Color.rgb(240, 180, 140));
//			LayeredImageScrollView layeredImageScrollView2 = new LayeredImageScrollView(this, Color.rgb(200, 200, 255));
//			setContentView(linearLayout);
//			linearLayout.addView(layeredImageScrollView1);
//			linearLayout.addView(layeredImageScrollView2);
//			linearLayout.setOrientation(LinearLayout.VERTICAL);
//			LayoutParams params = layeredImageScrollView1.getLayoutParams();
//			params.height = 400;
//			params.width = 400;
//			layeredImageScrollView1.setLayoutParams(params);
//			params = layeredImageScrollView2.getLayoutParams();
//			params.height = 400;
//			params.width = 400;
//			layeredImageScrollView2.setLayoutParams(params);
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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Fun.log("onWindowFocusChanged()");
		super.onWindowFocusChanged(hasFocus);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageViewLayoutFinished() {
		//		mPageView.setimage(BitmapFactory.decodeResource(getResources(), R.drawable.usagi));
		//		mPageView.setImage(mBook.getBitmap(mBook.getCurPage()));

//		mPageView.setImage(mBook.getBitmap(mBook.getCurPage()));

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override public void run() {
				if(mBook.getReadyForSetImage() && isWindowFocusChanged) {
					timer.cancel();
					handler.post(new Runnable() {
						@Override public void run() {
							Fun.log("TickerViewのsetimageを呼び出すタイマー");
							mTickerView.setImage(mPageView.getImage());
						}
					});
				}
			}
		}, 0, 100);
//
//
//		Fun.log("onPageViewLayoutFinished");
//		Fun.log(mPageView.getHeight());
//		Fun.log(mPageView.getWidth());
//		Fun.log(mTickerView.getHeight());
//		Fun.log(mTickerView.getWidth());
	}

}
