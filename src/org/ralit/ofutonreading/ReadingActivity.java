package org.ralit.ofutonreading;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ReadingActivity extends Activity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		setContentView(R.layout.activity_reading);
		// Show the Up button in the action bar.
		//		setupActionBar();
//		
		Intent intent = getIntent();
		if (intent != null) {
			String fileName = intent.getStringExtra("fileName");
			String filePath = intent.getStringExtra("filePath");
			BookManager manager = new BookManager(fileName, filePath, this);
//			BookView bookView = new BookView(this, manager);
//			setContentView(bookView);
//			manager.setBookView(bookView);
		}
		
		initialize();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//		getMenuInflater().inflate(R.menu.reading, menu);
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
	}

}
