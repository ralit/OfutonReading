package org.ralit.ofutonreading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.PointF;
import android.os.Environment;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BookManager {

	private String mBookName;
	private String mFilePath;
	private Context mContext;
	private int mCurLine;
	private int mCurPage;
	private enum FileType { pdf, zip, png, jpg };
	private FileType mType; 
	private PDF mPDF;
	ArrayList<ArrayList<Integer>> mPosList;
	boolean mRecognized = false;
	
	public boolean isRecognized() {
		return mRecognized;
	}
	
	public BookManager(String bookName, String filePath, Context context) {
		mBookName = bookName;
		mFilePath = filePath;
		mContext = context;
		check();
	}
	
	private void check() {
		// ファイルの種類に依存しない共通の処理
		if (isReading()) {
			if (new File(mFilePath).length() != readFileSize()) {
				// ファイルが変更されたか、同じファイル名の別のファイルを開こうとしている！
				Toast.makeText(mContext, "ファイル サイズ が ちがう", Toast.LENGTH_LONG).show();
			}
			if ((mCurPage = readCurPage()) == -1) {
				// エラー！
				Toast.makeText(mContext, "currentPage が だめだ", Toast.LENGTH_LONG).show();
			}
			if ((mCurLine = readCurLine()) == -1) {
				// エラー！
				Toast.makeText(mContext, "currentLine が だめだ", Toast.LENGTH_LONG).show();
			}
			if ((mPosList = readPageLayout(mCurPage)).isEmpty()) {
				// エラー！
				Toast.makeText(mContext, "layout が だめだ", Toast.LENGTH_LONG).show();
			}
			if (mPosList == null) {
				mRecognized = false;
			} else {
				mRecognized = true;
			}
		}
		mType = getFileType();
		if (mType == FileType.pdf) {
			mPDF = new PDF(mContext, mFilePath);
			if (mPDF.getPageCount() < readCurPage()) {
				// たぶんファイルが違うよ。またはページを一部削除したかな。
			}
		}
	}
	
	private boolean isReading() {
		if (readCurPage() == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	private FileType getFileType() {
		if (match(mFilePath, "\\.pdf$", true)) { return FileType.pdf; }
		if (match(mFilePath, "\\.zip$", true)) { return FileType.zip; }
		if (match(mFilePath, "\\.png$", true)) { return FileType.png; }
		if (match(mFilePath, "\\.jpe?g$", true)) { return FileType.jpg; }
		return null;
	}
	
	private boolean match(String str, String regExp, boolean caseSensitive) {
		Pattern pattern;
		if (caseSensitive) { 
			pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(regExp);
		}
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	private boolean savePageLayout() {
		PointF size = null;
		if (mType == FileType.pdf) { size = mPDF.getSize(mCurPage); }
		if (size == null) {
			Toast.makeText(mContext, "レイアウトが ほぞん できない 画像の サイズが わからないからだ", Toast.LENGTH_LONG).show();
			return false;
		}
		String fileName = mCurPage + "_" + (int)size.x + "_" + (int)size.y;
		Gson gson = new Gson();
		save(gson.toJson(mPosList, mPosList.getClass()), Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/layout/" + fileName);
		return true;
	}
	
	private ArrayList<ArrayList<Integer>> readPageLayout(int page) {
		PointF size = null;
		if (mType == FileType.pdf) { size = mPDF.getSize(page); }
		String fileName = page + "_" + (int)size.x + "_" + (int)size.y;
		// 処理を分ける！
		if (!new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/layout/" + fileName).exists()) {
			File[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/layout/").listFiles();
			ArrayList<String> names = new ArrayList<String>();
			for (File file : files) { names.add(file.getName()); }
			String conflictName = null;
			for (String name : names) { 
				if(match(name, page + "_[0-9]+?_[0-9]+?", false)) { conflictName = name; }
			}
			if (conflictName != null) {
				// ページの大きさが合ってない
				Toast.makeText(mContext, "レイアウトを 読もうとしたら サイズが違う らしい", Toast.LENGTH_LONG).show();
				ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
				return list;
			} else {
				// まだ開いたことがないページ
				return null;
			}
		}
		Gson gson = new Gson();
		String json = read(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/layout/" + fileName);
		Type type = new TypeToken<ArrayList<ArrayList<Integer>>>(){}.getType();
		return gson.fromJson(json, type);
	}

	public void setCurLine(int curLine) {
		mCurLine = curLine;
	}
	
	public int getCurLine() {
		return mCurLine;
	}
	
	private void saveCurLine() {
		save(String.valueOf(mCurLine), Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/currentLine.txt");
	}
	
	private int readCurLine() {
		try { return Integer.parseInt(read(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/currentLine.txt")); } 
		catch (Exception e) { return -1; }
	}
	
	private void saveFilePath() {
		save(mFilePath, Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/filePath.txt");
	}
	
	private String readFilePath() {
		return read(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/filePath.txt");
	}
	
	private void saveFileSize() {
		save(String.valueOf(new File(mFilePath).length()), Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/fileSize.txt");
	}
	
	private long readFileSize() {
		try { return Long.parseLong(read(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/fileSize.txt")); } 
		catch (Exception e) { return -1; }
	}
	
	public void setCurPage(int curPage) {
		mCurPage = curPage;
	}
	
	public int getCurPage() {
		return mCurPage;
	}
	
	private void saveCurPage(int curPage) {
		save(String.valueOf(curPage), Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/currentPage.txt");
	}
	
	private int readCurPage() {
		try { return Integer.parseInt(read(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/currentPage.txt")); } 
		catch (Exception e) { return -1; }
	}
	
	private void save(String data, String filePath) {
		File rootDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/");
		File bookDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		File layoutDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/layout/");
		File markerDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/marker/");
		try {
			if (!rootDir.exists()) { rootDir.mkdir(); }
			if (!bookDir.exists()) { bookDir.mkdir(); }
			if (!layoutDir.exists()) { layoutDir.mkdir(); }
			if (!markerDir.exists()) { markerDir.mkdir(); }
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
			writer.write(data);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String read(String filePath) {		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
			String data = reader.readLine();
			reader.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "ファイルが よみこめない", Toast.LENGTH_SHORT).show();
			return null;
		}
	}
}
