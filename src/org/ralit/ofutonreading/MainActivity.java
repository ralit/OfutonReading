package org.ralit.ofutonreading;

import java.io.File;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OpenFileListener {

	private FrameLayout rootFrame;
	private BookManager manager;
	private BookView bookView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log("onCreate()");
		super.onCreate(savedInstanceState);
		rootFrame = new FrameLayout(this);
		setContentView(rootFrame);
		
		TextView splash = new TextView(this);
		splash.setText("おふとんリーディング");
		splash.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		splash.setBackgroundColor(Color.GREEN);
		rootFrame.addView(splash);
//		BookView bookView = new BookView(this);
//		bookView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		rootFrame.addView(bookView);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		log("onWindowFocusChanged()");
		super.onWindowFocusChanged(hasFocus);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ofuton_open_file: {
			OpenFile openFile = new OpenFile(this, this);
			openFile.openDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove");
			return true;
		}
		}
		return false;
	}

	@Override
	public void onFileSelected(File file) {
		manager = new BookManager(file.getName(), file.getAbsolutePath(), this);
//		Display display = new Display(this, rootFrame, manager);
//		display.setImage();
		bookView = new BookView(this);
		bookView.setBookManager(manager);
		rootFrame.addView(bookView);
//		bookView.setImage();
		
	}
	
	private void log(String log) {
		Log.i("ralit", log);
	}
}
