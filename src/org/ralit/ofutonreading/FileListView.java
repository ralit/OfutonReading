package org.ralit.ofutonreading;

import java.io.File;
import java.util.ArrayList;

import android.R.string;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * ViewGroupの作成は以下のページが参考になりました。
 * http://ga29.blog.fc2.com/blog-entry-7.html
 */

interface FileClickListener {
	void onFileClicked(final File file);
}

public class FileListView extends ViewGroup{

	private Context mContext;
	private LinearLayout layout;
	private ListView fileListView;
	private ListView recentListView;
	private String lastDir;
	private FileClickListener fileClickListener;
	private ArrayList<String> recentList;
	//	CardListView fileListView;
	//	CardListView recentListView;

	public FileListView(Context context, FileClickListener _fileClickListener) {
		super(context);
		Fun.log("FileListView()");
		mContext = context;
		fileClickListener = _fileClickListener;
		recentList = new ArrayList<String>();
		layout();
		showFileList();
		showRecentList();
		fileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				changeFileListDirectory(parent, position);
			}
		});
	}

	// fileList
	private void changeFileListDirectory(AdapterView<?> parent, int position) {
		String item = null;
		if((item = parent.getItemAtPosition(position).toString()).equals("↑")) {
			Fun.log("equals(↑)");
			ArrayList<ArrayList<String>> array = Fun.matchGroup(lastDir, "(^.+)(/.+$)", false);
			File file = new File(array.get(0).get(0));
			updateFileList(lastDir = file.getAbsolutePath());
			Fun.saveRoot(file.getAbsolutePath(), "lastDir.txt");
		} else {
			Fun.log("!equals(↑)");
			File file = new File(lastDir + "/" + item);
			if (file.isDirectory()) {
				updateFileList(lastDir = file.getAbsolutePath());
				Fun.saveRoot(file.getAbsolutePath(), "lastDir.txt");
			} else {
				fileClickListener.onFileClicked(file);
				recentList.add(file.getAbsolutePath());
				String recentFiles = null;
				int i = 0;
				if ((recentList.size()) > 20) {
					i = recentList.size() - 20;
				}
				for( ; i < recentList.size(); i++) {
					if(recentFiles == null) {
						recentFiles = recentList.get(i) + "\n";
						continue;
					}
					recentFiles = recentFiles + recentList.get(i) + "\n";
				}
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		final int count  = getChildCount();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			LayoutParams params = view.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
			view.setLayoutParams(params);
		}
		if (w < h) {
			LayoutParams params = fileListView.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = h / 2;
			fileListView.setLayoutParams(params);
			params = recentListView.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = h / 2;
			recentListView.setLayoutParams(params);
			layout.setOrientation(LinearLayout.VERTICAL);
		} else {
			LayoutParams params = fileListView.getLayoutParams();
			params.width = w / 2;
			params.height = LayoutParams.MATCH_PARENT;
			fileListView.setLayoutParams(params);
			params = recentListView.getLayoutParams();
			params.width = w / 2;
			params.height = LayoutParams.MATCH_PARENT;
			recentListView.setLayoutParams(params);
			layout.setOrientation(LinearLayout.HORIZONTAL);
		}
	}

	private void layout() {
		Fun.log("FileListView.initialize()");
		setBackgroundColor(Color.WHITE);
		layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		fileListView = new ListView(mContext);
//		fileListView.setSmoothScrollbarEnabled(true);
		fileListView.setDividerHeight(0);
		recentListView = new ListView(mContext);
		recentListView.setDividerHeight(0);
		//		fileListView = new CardListView(mContext);
		//		recentListView = new CardListView(mContext);
		addView(layout);
		layout.addView(fileListView);
		layout.addView(recentListView);
	}

	//fileList
	private void showFileList() {
		String initialDir = readLastDir();
		updateFileList(initialDir);
	}
	
	//recent
	private void showRecentList() {
		readRecent();
		updateRecentList();
	}

	//fileList
	private String readLastDir() {
		try {
			lastDir = null;
			lastDir = Fun.read(Fun.DIR + "lastDir.txt");
			if (lastDir == null) { lastDir = Fun.getExternalStoragePath(); }
			return lastDir;
		} catch (Exception e) {
			return Fun.getExternalStoragePath();
		}
	}
	
	//recent
	private void readRecent() {
		try {
			ArrayList<String> tmp = Fun.readLines(Fun.DIR + "recentFiles.txt"); 
			if(tmp != null) {
				recentList = tmp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//fileList
	private void updateFileList(String dirPath) {
		File[] fileList = new File(dirPath).listFiles();
		String[] fileNameList = null;
		int listCount = 0;
		if(Fun.match(dirPath, "/.+/", false)) {
			fileNameList = new String[fileList.length + 1];
			fileNameList[listCount] = "↑";
			listCount++;
		} else {
			fileNameList = new String[fileList.length];
		}

		for (File file : fileList) {
			if(file.isDirectory()) {
				fileNameList[listCount] = file.getName() + "/";
			} else {
				fileNameList[listCount] = file.getName();
			}
			++listCount;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, fileNameList);
		fileListView.setAdapter(adapter);
	}
	
	//recentList
	private void updateRecentList() {
		if(recentList != null) {
			String[] reverse = new String[recentList.size()];
			int upCount = 0;
			for (int i = recentList.size() - 1; 0 <= i; i--) {
				reverse[upCount] = recentList.get(i);
				upCount++;
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, reverse);
			recentListView.setAdapter(adapter);
		}
	}

	//	@Override
	//	public void addView(View view) {
	//		super.addView(view);
	//		int currentScreen = -1;
	//		final int index = indexOfChild(view);
	//		if (index > currentScreen) {
	//			if (currentScreen > 0) {
	//				view.setVisibility(View.GONE);//★非表示
	//			}
	//			currentScreen = index;
	//			view.setVisibility(View.VISIBLE);//★表示
	//		}
	//	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		final int count = getChildCount();
		final int left = getLeft();
		final int top = getTop();
		final int right = getRight();
		final int bottom = getBottom();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (view.getVisibility() != View.GONE) {
				view.layout(left, top, right, bottom);
			}
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
