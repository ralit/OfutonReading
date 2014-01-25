package org.ralit.ofutonreading;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;

public class Line {

	private int[] pixels;
	private int w;
	private int h;
	private Foreground isLine;
	private int[] numLine;
	
	public Line(Bitmap bmp, Foreground _isLine) {
		isLine = _isLine;
		w = bmp.getWidth();
		h = bmp.getHeight();
		pixels = new int[w*h];
		bmp.getPixels(pixels, 0, w, 0, 0, w, h);
		
		Fun.log("getPixels直後");
		Fun.log(w);
		Fun.log(h);
		
		numLine = new int[h];
		for (int y = 0; y < h; y++) {
			numLine[y] = 0;
			for (int x = 0; x < w; x++) {
				if (isLine.evaluate(pixels[x + y*w])) {
					numLine[y]++;
				}
			}
		}
		
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
		
		Fun.log("保存直前");
		Fun.log(w);
		Fun.log(h);
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
