package org.ralit.ofutonreading;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;

public class ScanOrPhoto {

	private int[] pixels;
	private int w;
	private int h;
	
	public ScanOrPhoto(Bitmap bmp) {
		w = bmp.getWidth();
		h = bmp.getHeight();
		pixels = new int[w*h];
		bmp.getPixels(pixels, 0, w, 0, 0, w, h);
	}
	
	public double getWhiteRate() {
		int whiteCount = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (Color.WHITE == pixels[y*w + x]) {
					whiteCount++;
				}
			}
		}
		return (double)whiteCount / (w*h);
	}
}
