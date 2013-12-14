package org.ralit.ofutonreading;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Environment;
import android.widget.Toast;


public class BookManagerOld {

	private String mBookName;
	private String mFilePath;
	private Context mContext;
	private int mCurLine = 0;
	private int mCurPage = 0;
	private String mReadFilePath = "";
	private long mFileSize;
	
	private enum FileType { pdf, zip, png, jpg };
	private FileType mType; 

	private PDF mPDF;
	private ArrayList<ArrayList<Integer>> mPosList;
	boolean mRecognized = false;
	
	private Docomo docomo;
	private ArrayList<Word> wordList;
	

	public BookManagerOld(String bookName, String filePath, Context context) {
		Fun.log("BookManager()");
		mBookName = bookName;
		mFilePath = filePath;
		mContext = context;
		mCurLine = readCurLine();
		mCurPage = readCurPage();
		mReadFilePath = readFilePath();
		mFileSize = readFileSize();
		mType = getFileType();
		
		if (mType == FileType.pdf) {
			mPDF = new PDF(mContext, mFilePath);
		}
		mPosList = readPageLayout(mCurPage);
		
		Fun.log(String.valueOf(mCurLine));
		Fun.log(String.valueOf(mCurPage));
		Fun.log(mReadFilePath);
		Fun.log(String.valueOf(mFileSize));
		Fun.log(String.valueOf(mType));
		if (mPosList != null) {
			Fun.log(mPosList.toString());
		}

		check();
		saveCurLine();
		saveCurPage();
		saveFilePath();
		saveFileSize();
//		savePageLayout();
	}

	public void setCurLine(int curLine) {
		mCurLine = curLine;
	}

	public int getCurLine() {
		return mCurLine;
	}

	private void saveCurLine() {
		Fun.save(String.valueOf(mCurLine), Fun.DIR + mBookName + "/currentLine.txt", mBookName);
	}

	private int readCurLine() {
		Fun.log("readCurLine");
		try { return Integer.parseInt(Fun.read(Fun.DIR + mBookName + "/currentLine.txt")); } 
		catch (Exception e) { return -1; }
	}

	private void saveFilePath() {
		Fun.save(mFilePath, Fun.DIR + mBookName + "/filePath.txt", mBookName);
	}

	private String readFilePath() {
		return Fun.read(Fun.DIR + mBookName + "/filePath.txt");
	}

	private void saveFileSize() {
		Fun.save(String.valueOf(new File(mFilePath).length()), Fun.DIR + mBookName + "/fileSize.txt", mBookName);
	}

	private long readFileSize() {
		try { return Long.parseLong(Fun.read(Fun.DIR + mBookName + "/fileSize.txt")); } 
		catch (Exception e) { return -1; }
	}

	public void setCurPage(int curPage) {
		mCurPage = curPage;
	}

	public int getCurPage() {
		return mCurPage;
	}

	private void saveCurPage() {
		Fun.save(String.valueOf(mCurPage), Fun.DIR + mBookName + "/currentPage.txt", mBookName);
	}

	private int readCurPage() {
		Fun.log("readCurPage");
		try { return Integer.parseInt(Fun.read(Fun.DIR + mBookName + "/currentPage.txt")); } 
		catch (Exception e) { Fun.log("catch in readCurPage()"); return -1; }
	}

	private void check() {
		Fun.log("check()");
		// ファイルの種類に依存しない共通の処理
		if (isReading()) {
			Fun.log("isReading == true");
			if (!mFilePath.equals(mReadFilePath)) {
				Fun.log("同じ名前の別のディレクトリにあるファイルを開こうとした");
			}
			if (new File(mFilePath).length() != mFileSize) {
				Fun.log("ファイルが変更された");
			}
			if (mCurPage == -1) {
				Fun.log("currentPage.txtが存在しなかった");
				mCurPage = 0;
				saveCurPage();
			}
			if (mCurLine == -1) {
				Fun.log("currentLine.txtが存在しなかった");
				mCurLine = 0;
				saveCurLine();
			}
			if (mPosList.get(0).get(0) == -1) {
				Fun.log("サイズが違うレイアウトデータが存在する場合");
			}
			if (mPosList == null) { mRecognized = false; }
			else { mRecognized = true; }
		}
		// ファイル種類別
		if (mType == FileType.pdf) {
			if (mPDF.getPageCount() < mCurPage) {
				Fun.log("PDFファイルのページ数よりmCurPageの方が大きい)");
			}
		}
	}

	private boolean isReading() {
		if (mCurPage == -1) { return false; } 
		else { return true; }
	}

	private FileType getFileType() {
		Fun.log("getFileType()");
		if (Fun.match(mFilePath, "\\.pdf$", true)) { return FileType.pdf; }
		if (Fun.match(mFilePath, "\\.zip$", true)) { return FileType.zip; }
		if (Fun.match(mFilePath, "\\.png$", true)) { return FileType.png; }
		if (Fun.match(mFilePath, "\\.jpe?g$", true)) { return FileType.jpg; }
		return null;
	}

	public ArrayList<ArrayList<Integer>> getPageLayout() {
		Fun.log("getPageLayout()");
		return mPosList;
	}

	private boolean savePageLayout() {
		PointF size = null;
		if (mType == FileType.pdf) { size = mPDF.getSize(mCurPage); }
		if (size == null) {
			Fun.log("画像のサイズが取得できなかった");
			return false;
		}
		String fileName = mCurPage + "_" + (int)size.x + "_" + (int)size.y;
		Gson gson = new Gson();
		Fun.save(gson.toJson(mPosList, mPosList.getClass()), Fun.DIR + mBookName + "/layout/" + fileName, mBookName);
		return true;
	}

	private ArrayList<ArrayList<Integer>> readPageLayout(int page) {
		Fun.log("readPageLayout");
		PointF size = null;
		if (mType == FileType.pdf) { size = mPDF.getSize(page); }
		String fileName = page + "_" + (int)size.x + "_" + (int)size.y;

		// しばらくエラー処理
		if (!new File(Fun.DIR + mBookName + "/layout/" + fileName).exists()) {
			Fun.log("少なくとも全く同じファイル名のLayoutデータは存在しない");
			File[] files = new File(Fun.DIR + mBookName + "/layout/").listFiles();
			ArrayList<String> names = new ArrayList<String>();
			for (File file : files) { names.add(file.getName()); }
			String conflictName = null;
			for (String name : names) { 
				if(Fun.match(name, page + "_[0-9]+?_[0-9]+?", false)) { conflictName = name; }
			}
			if (conflictName != null) {
				Fun.log("サイズが違うLayoutデータは存在する");
				ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
				ArrayList<Integer> inner = new ArrayList<Integer>();
				inner.add(-1);
				list.add(inner);
				return list;
			} else {
				Fun.log("まだ開いたことがないページ");
				return null;
			}
		}
		
		Fun.log("同じファイル名のLayoutデータが存在する");
		Gson gson = new Gson();
		String json = Fun.read(Fun.DIR + mBookName + "/layout/" + fileName);
		Type type = new TypeToken<ArrayList<ArrayList<Integer>>>(){}.getType();
		return gson.fromJson(json, type);
	}
	
	public boolean isRecognized() {
		return mRecognized;
	}
	
	public void recognize() {
		Fun.log("recognize()");
		if (mType == FileType.pdf) {
			Fun.log("recognize(): FileType == pdf");
			Bitmap bmp = mPDF.getBitmap(mCurPage);
//			DocomoOld docomo = new DocomoOld(bmp);
			docomo = new Docomo(bmp, mBookName);
			docomo.start();
		}
	}
	
	public void setPos() {
		wordList = docomo.getWordList();
		savePageLayout();
	}
	
	public Bitmap getBitmap() {
		Fun.log("getBitmap()");
		if (mType == FileType.pdf) {
			Fun.log("getBitmap(): FileType == pdf");
			return mPDF.getBitmap(mCurPage);
		}
		Fun.log("getBitmap(): return null");
		return null;
	}
}
