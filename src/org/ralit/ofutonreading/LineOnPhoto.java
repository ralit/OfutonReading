package org.ralit.ofutonreading;

import java.io.IOException;
import java.util.ArrayList;

import static org.ralit.ofutonreading.ImageUtility.*;
import android.graphics.Bitmap;

public class LineOnPhoto extends Thread {

	private int[][] bitmap;
	private int w;
	private int h;
	ArrayList<Word> rectList = new ArrayList<Word>();
	private boolean isEnded = false;
	
	public LineOnPhoto(Bitmap bmp) {
		w = bmp.getWidth();
		h = bmp.getHeight();
		int[] pixels = new int[w*h];
		bmp.getPixels(pixels, 0, w, 0, 0, w, h);
		bitmap = new int[h][w];
		one2two(pixels, bitmap, h, w);
	}
	
	public ArrayList<Word> getRectList() {
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Edge edge = new Edge(w, h, bitmap);
			int[][] binary = edge.getEdgedBinaryBitmap();
			edge = null;
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}


}
