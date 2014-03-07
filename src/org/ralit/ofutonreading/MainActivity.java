package org.ralit.ofutonreading;

import java.io.File;
import java.io.IOException;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
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
		Fun.log("getRequestedOrientation: " + getRequestedOrientation());
		frameLayout = new FrameLayout(this);
		setContentView(frameLayout);
		FileListView fileListView = new FileListView(this, this);
		frameLayout.addView(fileListView);

//		try {
//			ZIP.addZip(Fun.getExternalStoragePath() + "/ESFTP/RaspberryPi.zip", Fun.getExternalStoragePath() + "/ESFTP/speech.png");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			Fun.log("addZipError");
//			e.printStackTrace();
//		}
		
		DisplayMetrics metrics = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(metrics);  
		Log.d("test", "density=" + metrics.density);  
		Log.d("test", "densityDpi=" + metrics.densityDpi);  
		Log.d("test", "scaledDensity=" + metrics.scaledDensity);  
		Log.d("test", "widthPixels=" + metrics.widthPixels);  
		Log.d("test", "heightPixels=" + metrics.heightPixels);  
		Log.d("test", "xDpi=" + metrics.xdpi);  
		Log.d("test", "yDpi=" + metrics.ydpi);  

		Log.d("build","BOARD:" + Build.BOARD);
		Log.d("build","BOOTLOADER:" + Build.BOOTLOADER);    //Android 1.6未対応
		Log.d("build","BRAND:" + Build.BRAND);
		Log.d("build","CPU_ABI:" + Build.CPU_ABI);
		Log.d("build","CPU_ABI2:" + Build.CPU_ABI2);        //Android 1.6未対応
		Log.d("build","DEVICE:" + Build.DEVICE);
		Log.d("build","DISPLAY:" + Build.DISPLAY);
		Log.d("build","FINGERPRINT:" + Build.FINGERPRINT);
		Log.d("build","HARDWARE:" + Build.HARDWARE);        //Android 1.6未対応
		Log.d("build","HOST:" + Build.HOST);
		Log.d("build","ID:" + Build.ID);
		Log.d("build","MANUFACTURER:" + Build.MANUFACTURER);
		Log.d("build","MODEL:" + Build.MODEL);
		Log.d("build","PRODUCT:" + Build.PRODUCT);
		Log.d("build","RADIO:" + Build.RADIO);              //Android 1.6未対応
		Log.d("build","TAGS:" + Build.TAGS);
		Log.d("build","TIME:" + Build.TIME);
		Log.d("build","TYPE:" + Build.TYPE);
		Log.d("build","UNKNOWN:" + Build.UNKNOWN);          //Android 1.6未対応
		Log.d("build","USER:" + Build.USER);
		Log.d("build","VERSION.CODENAME:" + Build.VERSION.CODENAME);
		Log.d("build","VERSION.INCREMENTAL:" + Build.VERSION.INCREMENTAL);
		Log.d("build","VERSION.RELEASE:" + Build.VERSION.RELEASE);
		Log.d("build","VERSION.SDK:" + Build.VERSION.SDK);
		Log.d("build","VERSION.SDK_INT:" + Build.VERSION.SDK_INT);

		//		new Label(BitmapFactory.decodeResource(getResources(), R.drawable.splash), new Foreground() {
		//			@Override
		//			public boolean evaluate(int pixel) {
		//				if(Color.red(pixel) == 255 && Color.green(pixel) == 255 && Color.blue(pixel) == 255) {
		//					return false;
		//				}
		//				return true;
		//			}
		//		}).start();

		//		new Line(BitmapFactory.decodeResource(getResources(), R.drawable.reportjpeg), new Foreground() { // この書き方だと勝手にスケールされる
		//			@Override
		//			public boolean evaluate(int pixel) {
		////				if(Color.red(pixel) > 200 && Color.green(pixel) > 200 && (pixel&0xff) > 200) {
		//				if((pixel&0xff) > 200) {
		//					return false;
		//				}
		//				return true;
		//			}
		//		}).start();

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
			if(event.getAction() == KeyEvent.ACTION_DOWN) {
				if(Build.MODEL.equals("M100")) {
					File file = new File(Environment.getExternalStorageDirectory() + "/finalreport.pdf");
					Fun.log(file.getAbsolutePath());
					onFileClicked(file);
				}
			}
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
