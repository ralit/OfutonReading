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

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

class OpenFile implements OnClickListener{

	private Context mParent = null;
	private int mSelectedItemIndex = -1;
	private File[] mFileList;
	private String mCurDir = null;
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
		
		
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

}
