package org.ralit.ofutonreading;

import java.io.File;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends Activity implements FileClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Fun.log("onCreate()");
		super.onCreate(savedInstanceState);
		FrameLayout frameLayout = new FrameLayout(this);
		setContentView(frameLayout);
		FileListView fileListView = new FileListView(this, this);
		frameLayout.addView(fileListView);
		SplashView splashView = new SplashView(this);
		frameLayout.addView(splashView);
		
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator animator = ObjectAnimator.ofFloat(splashView, "alpha", 1f);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(splashView, "alpha", 0f);
		animator.setDuration(2000);
		animator2.setDuration(500);
		set.playSequentially(animator, animator2);
		set.start();
//		Configuration config = getResources().getConfiguration();
//		config.orientation = Configuration.ORIENTATION_LANDSCAPE;
	}

//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		Fun.log("onWindowFocusChanged()");
//		super.onWindowFocusChanged(hasFocus);
//	}

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
		Fun.log("open!");
		
	}
}
