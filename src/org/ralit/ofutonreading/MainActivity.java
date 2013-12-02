package org.ralit.ofutonreading;

import java.io.File;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OpenFileListener {

//	private ImageView mImageView;
//	private LinearLayout layout;
//	private RelativeLayout relative;
	private FrameLayout rootFrame;
	private PDF pdf;
	private BookManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		log("onCreate()");
		super.onCreate(savedInstanceState);
		rootFrame = new FrameLayout(this);
		TextView splash = new TextView(this);
		splash.setText("おふとんリーディング");
		rootFrame.addView(splash);
		setContentView(rootFrame);
//		Display display = new Display(this, rootFrame);
//		ObjectAnimator animator = ObjectAnimator.ofFloat(display, "alpha", 0f);
//		animator.setDuration(500).start();
//		mImageView = new ImageView(this);
//		setContentView(mImageView);
//		layout = new LinearLayout(this);
//		layout.setOrientation(LinearLayout.VERTICAL);
//		relative = new RelativeLayout(this);
//		setContentView(layout);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ImageView image1 = new ImageView(this);
//		image1.setImageResource(R.drawable.ofuton);
//		image1.setAlpha(0f);
//		layout.addView(image1);
//		ObjectAnimator anim = ObjectAnimator.ofFloat(image1, "alpha", 1f);
//		anim.setDuration(1000).start();
//		layout.invalidate();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ImageView image2 = new ImageView(this);
//		image2.setImageResource(R.drawable.ofuton);
//		layout.addView(image2);
//		layout.refreshDrawableState();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ImageView image3 = new ImageView(this);
//		image3.setImageResource(R.drawable.ofuton);
//		layout.addView(image3);
		
//		setContentView(R.layout.activity_main);
//		readPDF();
		
//		mImageView.setImageBitmap(pdf.getBitmap(0));
		
/*
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				PDF pdf = new PDF(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove/file.pdf");
				ArrayList<ArrayList<RecognizePointData>> point = new ArrayList<ArrayList<RecognizePointData>>();
				ArrayList<RecognizeWordData> word = new ArrayList<RecognizeWordData>();
				Docomo docomo = new Docomo(pdf.getBitmap(0));
				docomo.recognize();
				point = docomo.getShape();
				word = docomo.getWord();
				log(point.toString());
				log(word.toString());
			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
*/
//		PDF pdf = new PDF(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove/file.pdf");
//		DocomoOld docomo = new DocomoOld(pdf.getBitmap(1));
//		ArrayList<ArrayList<Integer>> list = docomo.getPos(1.2f);
//		log(list.toString());
//		PageLayout pageLayout = new PageLayout("file", 1, list, pdf.getSize(1));
//		pageLayout.savePageLayout();
		

//		mImageView.setImageBitmap(pdf.getBitmap(0));
	}
	
//	private void readPDF() {
//		OpenFile open = new OpenFile(this, this);
//		open.openDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove");
//		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove/file.pdf";
//		PDF pdf = new PDF(getApplicationContext(), filePath);
//		PointF size = pdf.getSize(0);
//		mImageView.setImageBitmap(pdf.getBitmap(0, size));
//	}

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
			openFile.openDir(Environment.getExternalStorageDirectory().getAbsolutePath());
			return true;
		}
		}
		return false;
	}

	@Override
	public void onFileSelected(File file) {
		manager = new BookManager(file.getName(), file.getAbsolutePath(), this);
		Display display = new Display(this, rootFrame, manager);
		display.setTicker();
	}
	
	private void log(String log) {
		Log.i("ralit", log);
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		log("onTouch");
//		ImageView image1 = new ImageView(this);
//		image1.setImageResource(R.drawable.ofuton);
//		image1.setAlpha(0f);
//		layout.addView(image1);
//		ObjectAnimator anim = ObjectAnimator.ofFloat(image1, "alpha", 1f);
//		anim.setDuration(1000).start();
//		return false;
//	}

}
