package org.ralit.ofutonreading;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OpenFileListener {

	private FrameLayout rootFrame;
	private BookManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log("onCreate()");
		super.onCreate(savedInstanceState);
		rootFrame = new FrameLayout(this);
		setContentView(rootFrame);
		
		TextView splash = new TextView(this);
		splash.setText("おふとんリーディング");
		rootFrame.addView(splash);
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
		Display display = new Display(this, rootFrame, manager);
		display.setImage();
	}
	
	private void log(String log) {
		Log.i("ralit", log);
	}
}
