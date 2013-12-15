package org.ralit.ofutonreading;

import java.io.File;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements FileClickListener {

	private boolean isLaunch = true;
	private CountDownTimer keyEventTimer; // BackボタンPress時の有効タイマー
	private boolean pressed = false; // 一度目のBackボタンが押されたかどうかを判定するフラグ
	private FrameLayout frameLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Fun.log("onCreate()");
		super.onCreate(savedInstanceState);
		frameLayout = new FrameLayout(this);
		setContentView(frameLayout);
		FileListView fileListView = new FileListView(this, this);
		frameLayout.addView(fileListView);

		if(isLaunch) {
			SplashView splashView = new SplashView(this);
			frameLayout.addView(splashView);

			AnimatorSet set = new AnimatorSet();
			ObjectAnimator animator = ObjectAnimator.ofFloat(splashView, "alpha", 1f);
			ObjectAnimator animator2 = ObjectAnimator.ofFloat(splashView, "alpha", 0f);
			animator.setDuration(1500);
			animator2.setDuration(500);
			set.playSequentially(animator, animator2);
			set.start();
		}
		//		Configuration config = getResources().getConfiguration();
		//		config.orientation = Configuration.ORIENTATION_LANDSCAPE;
		
		// Backキーのカウントダウン
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
		
		isLaunch = false;
	}

	//	@Override
	//	public void onWindowFocusChanged(boolean hasFocus) {
	//		Fun.log("onWindowFocusChanged()");
	//		super.onWindowFocusChanged(hasFocus);
	//	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		isLaunch = savedInstanceState.getBoolean("isLaunch", true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isLaunch", isLaunch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Fun.log("onCreateOptionsMenu()");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Fun.log("onOptionsItemSelected()");
		switch (item.getItemId()) {
		}
		return false;
	}

	@Override
	public void onFileClicked(File file) {
		BookManager manager = new BookManager(file.getName(), file.getAbsolutePath(), this);
		BookView bookView = new BookView(this, manager);
		frameLayout.addView(bookView);
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
