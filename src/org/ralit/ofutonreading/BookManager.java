package org.ralit.ofutonreading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.PointF;
import android.os.Environment;
import android.os.PatternMatcher;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class BookManager {

	private String mBookName;
	private String mFilePath;
	private int mPage;
	private ArrayList<ArrayList<Integer>> mPosList;
	private PointF mSize;
	private Context mContext;
	
	public BookManager(String bookName, String filePath, Context context) {
		mBookName = bookName;
		mFilePath = filePath;
		mContext = context;
	}
	
	public boolean isReading() {
		if (getCurPage() == -1) {
			return false;
		} else {
			return true;
		}
	}
	
	public String getFileType() {
		if (match(mFilePath, "\\.pdf$", true)) { return "pdf"; }
		if (match(mFilePath, "\\.zip$", true)) { return "zip"; }
		if (match(mFilePath, "\\.png$", true)) { return "png"; }
		if (match(mFilePath, "\\.jpe?g$", true)) { return "jpg"; }
		return null;
	}
	
	public boolean match(String str, String regExp, boolean caseSensitive) {
		Pattern pattern;
		if (caseSensitive) { 
			pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(regExp);
		}
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	public void savePageLayout(int page, ArrayList<ArrayList<Integer>> posList, PointF size) {
		mPage = page;
		mPosList = posList;
		mSize = size;
		
		File rootDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/");
		File bookDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName + "/layout/");
		String fileName = mPage + "_" + (int)mSize.x + "_" + (int)mSize.y;
		try {
			if (!rootDir.exists()) { rootDir.mkdir(); }
			if (!bookDir.exists()) { bookDir.mkdir(); }
			if (!dir.exists()) { dir.mkdir(); }
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		try {
			JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(dir.getAbsolutePath() + "/" + fileName + ".json")));
			gson.toJson(mPosList, mPosList.getClass(), writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveFilePath() {
		File rootDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/");
		File bookDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		String fileName = "filePath.txt";
		try {
			if (!rootDir.exists()) { rootDir.mkdir(); }
			if (!bookDir.exists()) { bookDir.mkdir(); }
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(bookDir.getAbsolutePath() + "/" + fileName)));
			writer.write(mFilePath);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFilePath() {
		File bookDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		String fileName = "filePath.txt";
		if (!bookDir.exists()) {
			return null;
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(bookDir.getAbsolutePath() + "/" + fileName)));
			String filePath = reader.readLine();
			reader.close();
			return filePath;
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "本のファイルパスが読み込めなかったよ", Toast.LENGTH_SHORT).show();
			return null;
		}
	}
	
	public void saveFileSize() {
		File rootDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/");
		File bookDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		String fileName = "fileSize.txt";
		try {
			if (!rootDir.exists()) { rootDir.mkdir(); }
			if (!bookDir.exists()) { bookDir.mkdir(); }
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(bookDir.getAbsolutePath() + "/" + fileName)));
			File file = new File(mFilePath);
			writer.write(String.valueOf(file.length()));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public long getFileSize() {
		File bookDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		String fileName = "fileSize.txt";
		if (!bookDir.exists()) {
			return -1;
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(bookDir.getAbsolutePath() + "/" + fileName)));
			String filePath = reader.readLine();
			reader.close();
			return Long.parseLong(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "本のファイルサイズが読み込めなかったよ", Toast.LENGTH_SHORT).show();
			return -1;
		}
	}
	
	public void saveCurPage(int curPage) {
		File rootDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/");
		File bookDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		String fileName = "currentPage.txt";
		try {
			if (!rootDir.exists()) { rootDir.mkdir(); }
			if (!bookDir.exists()) { bookDir.mkdir(); }
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(bookDir.getAbsolutePath() + "/" + fileName)));
			writer.write(curPage);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getCurPage() {
		File bookDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		String fileName = "currentPage.txt";
		if (!bookDir.exists()) {
			return -1;
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(bookDir.getAbsolutePath() + "/" + fileName)));
			String curPage = reader.readLine();
			reader.close();
			return Integer.parseInt(curPage);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mContext, "とりあえず 最初の ページを 読み込むよ", Toast.LENGTH_SHORT).show();
			return 0;
		}
	}
}
