package org.ralit.ofutonreading;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends Activity implements OpenFileListener, FileOpenDialogListener, FileClickListener {
	
	private FileClickListener fileClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Fun.log("onCreate()");
		super.onCreate(savedInstanceState);
//		Fun.log(String.valueOf(Fun.match("abcde", "bcd", true)));
//		Fun.log(Fun.matchGroup("01234", "([0-9])([0-9])", true).toString());
//		Fun.log(String.valueOf(Fun.matchGroup("chuhcewuihwe", "([0-9])", true)));
//		ArrayList<ArrayList<String>> tmp = Fun.matchGroup("chuhcewuihwe", "([a-z])", true);
//		for(int i = 0; i < tmp.size(); i++) {
//			ArrayList<String> tmp2 = tmp.get(i);
//			for(int j = 0; j < tmp2.size(); j++) {
//				String str = tmp2.get(j);
//				Fun.log(str);
//			}
//		}
//		Fun.log(String.valueOf(Fun.matchGroup("/storage/sdcard0/imagemove", "(/storage/sdcard0/imagemove\\/.+?)(\\/|$)", false)));
//		Fun.log(Fun.getExternalStoragePath());
//		LinearLayout layout = new LinearLayout(this);
//		setContentView(layout);
		
//		ListView mListView = new ListView(this);
//		setContentView(mListView);
//		String[] test = {"111", "fhwuei", "iowceow", "jifoewjf", "111", "fhwuei", "iowceow", "jifoewjf", "111", "fhwuei", "iowceow", "jifoewjf" };
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test);
//		mListView.setAdapter(adapter);
//		BookView view = new BookView(this);
//		setContentView(view);
//
//		LayoutParams params = view.getLayoutParams();
//		params.width = LayoutParams.MATCH_PARENT;
//		params.height = LayoutParams.MATCH_PARENT;
//		view.setLayoutParams(params);

		FileListView fileListView = new FileListView(this, this);
		setContentView(fileListView);
//		CardListView cardListView = new CardListView(this);
//		setContentView(cardListView);
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
		case R.id.ofuton_open_file: {
//			OpenFile openFile = new OpenFile(this, this);
//			openFile.openDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove");
//			openFile.openDir(null, true);
			FileOpenDialog fileOpenDialog = new FileOpenDialog(this, this, false);
			fileOpenDialog.openDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove");
			return true;
		}
		}
		return false;
	}

	@Override
	public void onFileSelected(File file) {
		Fun.log("onFileSelected()");
//		Fun.log(file.getParentFile().getAbsolutePath());
		Fun.saveRoot(file.getParentFile().getAbsolutePath(), "lastDir.txt");
	}

	@Override
	public void onFileClicked(File file) {
		Fun.log("open!");
		
	}
}
