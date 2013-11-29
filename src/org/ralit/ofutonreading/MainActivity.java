package org.ralit.ofutonreading;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.widget.ImageView;

import com.artifex.mupdfdemo.MuPDFCore;

public class MainActivity extends Activity implements OpenFileListener {

	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageView = new ImageView(this);
		setContentView(mImageView);
//		setContentView(R.layout.activity_main);
//		readPDF();
		try {
			MuPDFCore core = new MuPDFCore(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove/file.pdf");
			core.countPages();
			PointF size = core.getPageSize(1);
//			Bitmap bmp = Bitmap.createBitmap((int)size.x, (int)size.y, android.graphics.Bitmap.Config.ARGB_8888);
//			core.drawPage(bmp, 1, (int)size.x, (int)size.y, 0, 0, (int)size.x, (int)size.y);
//			mImageView.setImageBitmap(bmp);
			mImageView.setImageBitmap(core.drawPage(1, (int)size.x, (int)size.y, 0, 0, (int)size.x, (int)size.y));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

}
