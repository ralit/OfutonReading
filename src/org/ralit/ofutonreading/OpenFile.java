package org.ralit.ofutonreading;

/**
 * 以下のコードをほぼ丸ごと使いました。
 * 
 * サンプルプログラム工場
 * [Android]ファイル/フォルダ選択ダイアログのサンプル#tryFileOpenDialog00
 * http://junkcode.aakaka.com/archives/675
 * 
 */

import java.io.File;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

interface OpenFileListener {
	void onFileSelected(final File file);
}


class OpenFile implements OnClickListener{

	private Context mParent;
	private int mSelectedItemIndex = -1;
	private File[] mFileList;
	private String mCurDir;
	private Stack<String> mDirs = new Stack<String>();
	private OpenFileListener mListener;
	private File mLastSelectedItem;
	
	public OpenFile(final Context parent, final OpenFileListener listener) {
		mParent = parent;
		mListener = listener;
	}

	public void openDir(String dir) {
		mFileList = new File(dir).listFiles();
		mCurDir = dir;
		
		String[] fileNameList = null;
		int itemCount = 0;
		
		// "↑"をファイル一覧の先頭に表示する(ルートディレクトリ以外で)
		if (0 < mDirs.size()) {
			fileNameList = new String[mFileList.length + 1];
			fileNameList[itemCount] = "↑";
			++itemCount;
		} else {
			fileNameList = new String[mFileList.length];
		}
		
		// ファイル一覧を表示する準備(ファイルとディレクトリの見た目を変えよう)
		for (File file : mFileList) {
			if (file.isDirectory()) {
				fileNameList[itemCount] = file.getName() + "/";
			} else {
				fileNameList[itemCount] = file.getName();
			}
			++itemCount;
		}
		
		// そして最後にファイル一覧をダイアログで表示
		new AlertDialog.Builder(mParent).setTitle(dir).setItems(fileNameList, this);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		mSelectedItemIndex = which;
		if (mFileList == null) { return; }
		int selectedItemIndex = mSelectedItemIndex;
		
		if (0 < mDirs.size()) { --selectedItemIndex; } // "↑"を表示した分インデックスがずれる
		
		if (selectedItemIndex < 0) { // "↑"をタップしたとき
			openDir(mDirs.pop());
		} else { // "↑"以外をタップしたとき
			mLastSelectedItem = mFileList[selectedItemIndex];
			if (mLastSelectedItem.isDirectory()) {
				mDirs.push(mCurDir);
				openDir(mLastSelectedItem.getAbsolutePath());
			} else {
				mListener.onFileSelected(mLastSelectedItem);
			}
		}
		
	}
}
