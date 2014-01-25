package org.ralit.ofutonreading;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;

public class Line extends Thread{

	private int[] pixels;
	private int w;
	private int h;
	private Foreground isLine;
	private int[] numLine;
	ArrayList<Word> rectList = new ArrayList<Word>();
	private Bitmap bmp;
	private boolean isEnded = false;
	
	public Line(Bitmap _bmp, Foreground _isLine) {
		bmp = _bmp;
		isLine = _isLine;
		w = bmp.getWidth();
		h = bmp.getHeight();
		pixels = new int[w*h];
		bmp.getPixels(pixels, 0, w, 0, 0, w, h);
	}

	
	
	@Override
	public void run() {
		super.run();
		countBlackInLine();
		
//		rgbHistogram();
		
//		lineHistogram();
		
		createRect1();
		
		cutWhiteInLine();
		
		isEnded = true;
	}

	public ArrayList<Word> getRectList() {
		if (isEnded) {
			return rectList;
		} else {
			return null;
		}
	}

	private void countBlackInLine() {
		numLine = new int[h];
		for (int y = 0; y < h; y++) {
			numLine[y] = 0;
			for (int x = 0; x < w; x++) {
				if (isLine.evaluate(pixels[x + y*w])) {
					numLine[y]++;
				}
			}
		}
	}
	
	private void createRect1() {
		int top;
		int bottom;
		rectList = new ArrayList<Word>();
//		boolean isInside = false; 
		for (int y = 0; y < h; y++) {
			if (numLine[y] > 0) {
				top = y;
				int i;
				for (i = y; i < h; i++) {
					if (numLine[i] == 0) { break; }
				}
				bottom = i;
				y = i;
				Word rect = new Word();
				rect.setPoint(0, top, w-1, bottom);
				rectList.add(rect);
			}
		}
		for(Word rect : rectList) {
			Fun.log(rect.getTop() + "〜" + rect.getBottom());
		}
		
//		Fun.paintPosition(bmp, rectList, "test", 0);
	}
	
	private void cutWhiteInLine() {
		for(Word rect : rectList) {
			int left = w-1;
			for (int y = rect.getTop(); y < rect.getBottom(); y++) {
				for (int x = 0; x < w; x++) {
					if (isLine.evaluate(pixels[x + y*w])) {
						if (x < left) {
							left = x;
						}
						break;
					}
				}
			}
			rect.setLeft(left);
		}
		
		for(Word rect : rectList) {
			int right = 0;
			for (int y = rect.getTop(); y < rect.getBottom(); y++) {
				for (int x = w-1; 0 < x; x--) {
					if (isLine.evaluate(pixels[x + y*w])) {
						if (right < x) {
							right = x;
						}
						break;
					}
				}
			}
			rect.setRight(right);
		}
		
		Fun.paintPosition(bmp, rectList, "test", 0);
		
	}
	
	private void rgbHistogram() {
		int[] rgbHistogram = new int[256];
		for (int i = 0; i < 256; i++) {
				rgbHistogram[i] = Color.WHITE;
		}
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int pixel = pixels[x + y*w];
				rgbHistogram[(Color.red(pixel) + Color.green(pixel) + Color.blue(pixel))/3]++;
			}
		}
		for (int i = 0; i < 256; i++) {
			Fun.log(i+ ": " + rgbHistogram[i]);
		}
	}
	
	private void lineHistogram() {
		int ww = w + 100;
		int[] pixels2 = new int[ww*h];
		for (int y = 0; y < h; y++) {
			int percent = (int)(((float)numLine[y] / (float)w) * 100);
//			Fun.log(numLine[y]);
			for (int x = 0; x < w; x++) {
				pixels2[x + y*ww] = pixels[x +y*w];
			}
			for (int x = w; x < w+percent; x++) {
				pixels2[x + y*ww] = Color.BLACK;
			}
			for (int x = w+percent; x < ww; x++) {
				pixels2[x + y*ww] = Color.WHITE;
			}
		}
		
		Bitmap histogram = Bitmap.createBitmap(pixels2, ww, h, Bitmap.Config.ARGB_8888);
		File file = new File(Fun.DIR + "test" + "/histogram.png");
		try {
			FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
			histogram.compress(CompressFormat.PNG, 99, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
