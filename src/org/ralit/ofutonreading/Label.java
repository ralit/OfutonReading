package org.ralit.ofutonreading;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Rect;

abstract class Foreground {
	public abstract boolean evaluate(int pixel);
}

public class Label extends Thread {

	private int[] label; // インスタンス変数なので0で初期化されている
	private int[] pixels;
	private int w;
	private int h;
	private boolean done = false;
	private Foreground isForeground;
//	private ArrayList<Rect> rect = new ArrayList<Rect>();
	private Rect[] rect;

	public Label(Bitmap bmp, Foreground _isForeground) {
		isForeground = _isForeground;
		w = bmp.getWidth();
		h = bmp.getHeight();
		label = new int[w*h];
		pixels = new int[w*h];
		bmp.getPixels(pixels, 0, w, 0, 0, w, h);
	}

	private void createRect(int count) {
		rect = new Rect[count + 1];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int labelNum = label[x + y*w];
				if (rect[labelNum] == null) {
					Rect item = new Rect(x, y, x, y);
					rect[labelNum] = item;
				} else {
					if (x < rect[labelNum].left   ) { rect[labelNum].left   = x; }
					if (y < rect[labelNum].top    ) { rect[labelNum].top    = y; }
					if (rect[labelNum].right  < x ) { rect[labelNum].right  = x; }
					if (rect[labelNum].bottom < y ) { rect[labelNum].bottom = y; }
				}
			}
		}
	}
	
	public boolean isDone() {
		return done;
	}

	@Override
	public void run() {
		super.run();
		Fun.log("Labeling start.");
		Fun.log("scan");
		scan();
		Fun.log("deleteDuplicate");
		deleteDuplicate();
		Fun.log("pack");
		int count = pack();
		Fun.log(count);
		Fun.log("saveLabeledImage");
		saveLabeledImage(count);
		Fun.log("createRect");
		createRect(count);
		Fun.log(rect.toString());
		for (int i = 0; i < count + 1; i++) {
			Fun.log(rect[i]);
		}
		Fun.log("Labeling finished.");
	}

	public void runOnMainThread() {
		scan();
	}

	private int scan() {
		int count = 0; 
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (isForeground.evaluate(pixels[x + y*w]) && label[x + y*w] == 0) { // これ1回しか呼ばないんだから、label配列の中身が0であることを確認する必要ないよね？
					// 背景色じゃないピクセルに対する処理
					int maxLabel = 0;
//					if ((maxLabel = getMax4Neighbors(x, y)) != 0) {
					if ((maxLabel = getMax8Neighbors(x, y)) != 0) {
						// 孤立していなかった
						label[x + y*w] = maxLabel;
					} else {
						// 孤立していた
						count++;
						label[x + y*w] = count;
//						Fun.log(count);
					}
				}
			}
		}
		return count;
	}

	private void saveLabeledImage(int count) {
		int[] colorMap = new int[count + 1];
		Random r = new Random();
		colorMap[0] = Color.argb(255, 255, 255, 255);
		for (int i = 1; i < count + 1; i++) {
			colorMap[i] = Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256));
		}
		int testPixels[] = new int[w*h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				testPixels[x + y*w] = colorMap[label[x + y*w]];
			}
		}
		Bitmap testBmp = Bitmap.createBitmap(testPixels, w, h, Bitmap.Config.ARGB_8888);
		File file = new File(Fun.DIR + "test" + "/label.png");
		try {
			FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
			testBmp.compress(CompressFormat.PNG, 99, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveLabelAsText() {
		StringBuffer tmp = new StringBuffer();
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				tmp.append(label[x + y*w]);
			}
			tmp.append("\n");
		}
//		Fun.log(tmp);
		Fun.save(tmp.toString(), Fun.DIR + "test/label.txt", "test");
	}
	
	private int getMax4Neighbors(int x, int y) {
		int max = 0;
		if (y != 0 && label[x + w*(y-1)] > max) { max = label[x + w*(y-1)]; }	// 上
		if (x != 0 && label[x-1 + w*(y)] > max) { max = label[x-1 + w*(y)]; }	// 左
		if (y != h-1 && label[x + w*(y+1)] > max) { max = label[x + w*(y+1)]; }	// 下
		if (x != w-1 && label[x+1 + w*(y)] > max) { max = label[x+1 + w*(y)]; }	// 右
		return max;
	}
	
	private int getMax8Neighbors(int x, int y) {
		int max = 0;
		if (y != 0 && label[x + w*(y-1)] > max) { max = label[x + w*(y-1)]; }					// 上
		if (y != 0 && x != 0 && label[x-1 + w*(y-1)] > max) { max = label[x-1 + w*(y-1)]; }		// 左上
		if (x != 0 && label[x-1 + w*(y)] > max) { max = label[x-1 + w*(y)]; }					// 左
		if (x != 0 && y != h-1 && label[x-1 + w*(y+1)] > max) { max = label[x-1 + w*(y+1)]; }	// 左下
		if (y != h-1 && label[x + w*(y+1)] > max) { max = label[x + w*(y+1)]; }					// 下
		if (y != h-1 && x != w-1 && label[x+1 + w*(y+1)] > max) { max = label[x+1 + w*(y+1)]; }	// 右下
		if (x != w-1 && label[x+1 + w*(y)] > max) { max = label[x+1 + w*(y)]; }					// 右
		if (x != w-1 && y != 0 && label[x+1 + w*(y-1)] > max) { max = label[x+1 + w*(y-1)]; }	// 右上
		return max;
	}
	
	private void modifyLabel(int labelBefore, int labelAfter) {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (label[x + y*w] == labelBefore) {
					label[x + y*w] = labelAfter;
				}
			}
		}
	}
	
	private void deleteDuplicate() {
//		ArrayList<Integer> doneList = new ArrayList<Integer>();
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (label[x + y*w] != 0/* && !doneList.contains(label[x + y*w])*/) {
					int maxLabel = 0;
					if ((maxLabel = getMax8Neighbors(x, y)) > label[x + y*w]) {
//						doneList.add(label[x + y*w]);
						modifyLabel(maxLabel, label[x + y*w]);
					}
				}
			}
		}
	}
	
	private int pack() {
		int new_count = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (label[x + y*w] > new_count) {
					new_count++;
				}
			}
		}
		return new_count;
	}

}
