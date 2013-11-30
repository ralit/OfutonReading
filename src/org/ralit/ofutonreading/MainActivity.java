package org.ralit.ofutonreading;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity implements OpenFileListener {

	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageView = new ImageView(this);
		setContentView(mImageView);
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
		PDF pdf = new PDF(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove/file.pdf");
		DocomoOld docomo = new DocomoOld(pdf.getBitmap(1));
		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
		docomo.getPos(list, 1.2f);
		log(list.toString());
		

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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onFileSelected(File file) {
		
	}
	
	private void log(String log) {
		Log.i("ralit", log);
	}

}
