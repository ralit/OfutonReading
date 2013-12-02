package org.ralit.ofutonreading;

/**
 * 
 * やっぱりこのクラスは使いません。
 * 
 * BookManagerに全部突っ込みます。
 * 
 */



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.PointF;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class PageLayout {

	private String mBookName;
	private int mPage;
	private ArrayList<ArrayList<Integer>> mPosList;
	private PointF mSize;
	private int mCurLine;
	
	public PageLayout(String bookName, int page, ArrayList<ArrayList<Integer>> posList, PointF size) {
		mBookName = bookName;
		mPage = page;
		mPosList = posList;
		mSize = size;
	}
	
	public void savePageLayout() {
		
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
	
	public int getCurLine() {
		return mCurLine;
	}

}
