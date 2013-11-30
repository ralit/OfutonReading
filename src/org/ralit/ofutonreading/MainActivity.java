package org.ralit.ofutonreading;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends Activity implements OpenFileListener, FileOpenDialogListener {

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
		BookManager manager = new BookManager(file.getName(), file.getAbsolutePath(), this);
		if (manager.getFileType() == "pdf") {
			if (manager.isReading()) {
				if (file.length() != manager.getFileSize()) {
					// ファイルが変更されたか、同じファイル名の別のファイルを開こうとしている！
				}
				if (manager.getCurPage() == -1) {
					// エラー！
				}
				PDF pdf = new PDF(this, manager.getFilePath());
				if (pdf.getPageCount() < manager.getCurPage()) {
					// なにかがおかしいよ
				}
				pdf.getBitmap(manager.getCurPage());
			} else {
				manager.saveFilePath();
				manager.saveFileSize();
				PDF pdf = new PDF(this, manager.getFilePath());
				pdf.getBitmap(0);
			}
			
			
		} else if (manager.getFileType() == "zip") {
		
		} else if (manager.getFileType() == "jpg" || manager.getFileType() == "png") {
			
		}
		if (manager.isReading()) {
			manager.getCurPage();
			PDF pdf = new PDF(this, file.getAbsolutePath());

		}
	}
	
	private void log(String log) {
		Log.i("ralit", log);
	}

}
