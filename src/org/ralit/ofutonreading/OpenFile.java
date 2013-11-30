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
		super();
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
		new AlertDialog.Builder(mParent).setTitle(dir).setItems(fileNameList, this).show();
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



interface FileOpenDialogListener {
	void onFileSelected(final File file);
}


class FileOpenDialog implements DialogInterface.OnClickListener {

	private Context parent = null;
	private int selectedItemIndex = -1;
	private File[] fileList;
	private String cd = null;
	private Stack<String> directories = new Stack<String>();
	private FileOpenDialogListener listener;
	private File lastSelectedItem;
	
	public FileOpenDialog(final Context parent, final FileOpenDialogListener listener) {
		super();
		this.parent = parent;
		this.listener = listener;
	}
	
	public void openDirectory(String dir) {
		this.fileList = new File(dir).listFiles();
		this.cd = dir;
		
		String[] fileNameList = null;
		int itemCount = 0;
		
		// ルートディレクトリ以外では上の階層に移動できるようにする
		if (0 < this.directories.size()) {
			fileNameList = new String[this.fileList.length + 1];
			fileNameList[itemCount] = "↑";
			itemCount++;
		} else {
			fileNameList = new String[this.fileList.length];
		}
		
		// ファイル名を表示
		for (File file : this.fileList) {
			if (file.isDirectory()) { fileNameList[itemCount] = file.getName() + "/"; }
			else { fileNameList[itemCount] = file.getName(); }
			itemCount++;
		}
		
		// ダイアログ表示
		new AlertDialog.Builder(this.parent).setTitle(dir).setItems(fileNameList, this).show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		this.selectedItemIndex = which;
		if (this.fileList == null) { return; }
		
		int selectedItemIndex = this.selectedItemIndex;
		if (0 < this.directories.size()) { selectedItemIndex--; } // "↑"項目ぶんずれる。
		
		// ファイルをタップ
		if (selectedItemIndex < 0) { // "↑"がタップされた
			this.openDirectory(this.directories.pop());
		} else {
			this.lastSelectedItem = fileList[selectedItemIndex];
			if (this.lastSelectedItem.isDirectory()) {
				this.directories.push(cd);
				this.openDirectory(this.lastSelectedItem.getAbsolutePath());
			} else {
				this.listener.onFileSelected(this.lastSelectedItem);
			}
		}
	}
	
}