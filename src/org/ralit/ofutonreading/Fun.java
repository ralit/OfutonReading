package org.ralit.ofutonreading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;

public class Fun {

	public static final String DIR = getExternalStoragePath() + "/OfutonReading/";
	public static final String LAYOUT = "/layout/";
	public static final String MARKER = "/marker/";

	public static void log(String log) {
		if (log != null) {
			Log.i("ralit", log);
		} else {
			Log.i("ralit", "☆null☆");
		}
		
	}

	public static void save(String data, String filePath, String bookName) {
		log("save()");
		mkdir(bookName);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
			writer.write(data);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveRoot(String data, String fileName) {
		log("saveRoot()");
		mkdirRoot();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(DIR + fileName)));
			writer.write(data);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String read(String filePath) {	
		log("read()");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
			String data = reader.readLine();
			reader.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			log("read()中のIOException");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log("read()中のエラー");
			return null;
		}
	}
	
	public static ArrayList<String> readLines(String filePath) {	
		log("read()");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
			String data = null;
			ArrayList<String> list = new ArrayList<String>();
			while((data = reader.readLine()) != null) {
				list.add(data);
			}
			reader.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log("readLines()エラー");
			return null;
		}
	}

	private static void mkdir(String bookName) {
		log("mkdir()");
		File rootDir = new File(DIR);
		File bookDir = new File(DIR + bookName);
		File layoutDir = new File(DIR + bookName + LAYOUT);
		File markerDir = new File(DIR + bookName + MARKER);
		try {
			if (!rootDir.exists()) { rootDir.mkdir(); }
			if (!bookDir.exists()) { bookDir.mkdir(); }
			if (!layoutDir.exists()) { layoutDir.mkdir(); }
			if (!markerDir.exists()) { markerDir.mkdir(); }
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private static void mkdirRoot() {
		log("mkdirRoot()");
		File rootDir = new File(DIR);
		try {
			if (!rootDir.exists()) { rootDir.mkdir(); }
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public static boolean match(String str, String regExp, boolean caseInsensitive) {
		log("match()");
		Pattern pattern;
		if (caseInsensitive) { 
			pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(regExp);
		}
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	public static ArrayList<ArrayList<String>> matchGroup(String str, String regExp, boolean caseInsensitive) {
		log("match()");
		Pattern pattern;
		if (caseInsensitive) { 
			pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(regExp);
		}
		Matcher matcher = pattern.matcher(str);
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		Fun.log(String.valueOf(matcher.groupCount()));
		while(matcher.find()) {
			Fun.log("find");
			ArrayList<String> innerList = new ArrayList<String>();
			for (int i = 1; i <= matcher.groupCount(); i++) {
				innerList.add(matcher.group(i));
			}
			list.add(innerList);
		}
		//		Fun.log(list.toString());
		if(list.isEmpty()) { return null; }
		return list;
	}

	public static String getExternalStoragePath() {
		String path = null;
		log("MOTOROLA");
		path = System.getenv("EXTERNAL_ALT_STORAGE");
		if (path != null) { return path; }
		log("Samsung");
		path = System.getenv("EXTERNAL_STORAGE2");
		if (path != null) { return path; }
		log("Standard");
		path = System.getenv("EXTERNAL_STORAGE");
		if (path == null) { path = Environment.getExternalStorageDirectory().getAbsolutePath(); }
		log("HTC");
		File file = new File(path + "/ext_sd");
		if (file.exists()) { return file.getAbsolutePath(); }
		log("Standard");
		return path;
	}

	// たぶん使うことはないけど面白かったので
	public static Point getDisplaySize(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		try {
			// test for new method to trigger exception
			Class pointClass = Class.forName("android.graphics.Point");
			Method newGetSize = Display.class.getMethod("getSize", new Class[]{ pointClass });
			// no exception, so new method is available, just use it
			newGetSize.invoke(size);
			return size;
		} catch(Exception ex) {
			// new method is not available, use the old ones
			size.x = display.getWidth();
			size.y = display.getHeight();
			return size;
		}
	}
	
	public static void cacheImageForDocomo(Bitmap bmp, int compress, String bookName) {
		File file = new File(DIR + bookName + "/tmpImageForDocomo.jpg");
		try {
			FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
			bmp.compress(CompressFormat.JPEG, compress, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void paintPosition(Bitmap bmp, ArrayList<Word> word, String bookName, int curPage) {
//		Paint frame = new Paint();
//		frame.setStyle(Style.STROKE);
//		frame.setColor(Color.RED);
//		frame.setStrokeWidth(4);
		Paint number = new Paint();
		number.setStyle(Style.FILL_AND_STROKE);
		number.setColor(Color.RED);
		number.setStrokeWidth(1);
		number.setTextSize(20);
		number.setAntiAlias(true);
		Paint marker = new Paint();
		marker.setStyle(Style.FILL_AND_STROKE);
		marker.setColor(Color.YELLOW);
		marker.setStrokeWidth(1);
		marker.setAlpha(64);
		Bitmap mutableBitmap = bmp.copy(bmp.getConfig(), true);
		Canvas canvas = new Canvas(mutableBitmap);
		for (int i = 0; i < word.size(); ++i) {
			Rect rect = new Rect(word.get(i).getLeft(), word.get(i).getTop(), word.get(i).getRight(), word.get(i).getBottom());
			canvas.drawRect(rect, marker);
			canvas.drawText(Integer.toString(i), word.get(i).getLeft(), word.get(i).getTop(), number);
		}
		
		File file = new File(DIR + bookName + "/layout/" + curPage + ".jpg");
		try {
			FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
			mutableBitmap.compress(CompressFormat.JPEG, 90, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
