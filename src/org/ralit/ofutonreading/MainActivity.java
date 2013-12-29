package org.ralit.ofutonreading;

import java.io.File;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements FileClickListener, AnimatorListener {

	private boolean isLaunch = true;
	private CountDownTimer keyEventTimer; // BackボタンPress時の有効タイマー
	private boolean pressed = false; // 一度目のBackボタンが押されたかどうかを判定するフラグ
	private FrameLayout frameLayout;
	private SplashView splashView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Fun.log("onCreate()");
		super.onCreate(savedInstanceState);
		frameLayout = new FrameLayout(this);
		setContentView(frameLayout);
		FileListView fileListView = new FileListView(this, this);
		frameLayout.addView(fileListView);
		
	    DisplayMetrics metrics = new DisplayMetrics();  
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);  
	    Log.d("test", "density=" + metrics.density);  
	    Log.d("test", "densityDpi=" + metrics.densityDpi);  
	    Log.d("test", "scaledDensity=" + metrics.scaledDensity);  
	    Log.d("test", "widthPixels=" + metrics.widthPixels);  
	    Log.d("test", "heightPixels=" + metrics.heightPixels);  
	    Log.d("test", "xDpi=" + metrics.xdpi);  
	    Log.d("test", "yDpi=" + metrics.ydpi);  

//		if(isLaunch) {
//			splashView = new SplashView(this);
//			frameLayout.addView(splashView);
//
//			AnimatorSet set = new AnimatorSet();
//			ObjectAnimator animator = ObjectAnimator.ofFloat(splashView, "alpha", 1f);
//			ObjectAnimator animator2 = ObjectAnimator.ofFloat(splashView, "alpha", 0f);
//			animator.setDuration(2000);
//			animator2.setDuration(500);
//			set.playSequentially(animator, animator2);
//			set.addListener(this);
//			set.start();
//		}
		
		
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
		return false;
	}

	@Override
	public void onFileClicked(File file) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), ReadingActivity.class);
		intent.putExtra("fileName", file.getName());
		intent.putExtra("filePath", file.getAbsolutePath());
		startActivity(intent);
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
				Toast.makeText(this, getString(R.string.ofuton_back_key), Toast.LENGTH_SHORT).show();
				pressed = true;
				return false;
			}
			// pressed=trueの時、通常のBackボタンで終了処理.
			return super.dispatchKeyEvent(event);
		}
		// Backボタンに関わらないボタンが押された場合は、通常処理.
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		ViewGroup parent = (ViewGroup)splashView.getParent();
		parent.removeView(splashView);
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animator animation) {
		// TODO Auto-generated method stub
		
	}
}
