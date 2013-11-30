package org.ralit.ofutonreading;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.PointF;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class Layout {

	private String mBookName;
	private int mPage;
	private ArrayList<ArrayList<Integer>> mPosList;
	private PointF mSize;
	
	public Layout(String bookName, int page, ArrayList<ArrayList<Integer>> posList, PointF size) {
		mBookName = bookName;
		mPage = page;
		mPosList = posList;
		mSize = size;
	}
	
	public void saveLayout() {
		Gson gson = new Gson();
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/OfutonReading/" + mBookName);
		String fileName = mPage + "_" + mSize.x + "_" + mSize.y;
		try {
			if (!dir.exists()) { dir.mkdir(); }
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		try {
			JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(dir.getAbsolutePath() + "/" + fileName + ".txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
