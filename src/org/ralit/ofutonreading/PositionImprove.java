package org.ralit.ofutonreading;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class PositionImprove {

	public static void expand(Bitmap bmp, ArrayList<Word> pos) {
		int ww = bmp.getWidth();
		int hh = bmp.getHeight(); 
		int pixels[] = new int[ww * hh];
		bmp.getPixels(pixels, 0, ww, 0, 0, ww, hh);

		for (int i = 0; i < pos.size(); ++i) {
			Word word = pos.get(i);
			int y = (word.getBottom() + word.getTop()) / 2;
			int newL = -1;
			int newR = -1;
			for (int x = 0; x < word.getLeft(); ++x) {
				if(pixels[x + y * ww] == -16777216) { newL = x; break; }
			}
			for (int x = ww-1; word.getRight() < x; --x) {
				if(pixels[x + y * ww] == -16777216) { newR = x; break; }
			}
			if (newL != -1) {
				word.setLeft(newL);
			}
			if (newR != -1) { 
				word.setRight(newR);
			}
		}
	}

	public static void deleteLongcat(ArrayList<Word> pos) {
		ArrayList<Integer> fatCat = new ArrayList<Integer>();
		ArrayList<Integer> longCat = new ArrayList<Integer>();
		for (int i = 0; i < pos.size(); ++i) {
			if (pos.get(i).getRight() - pos.get(i).getLeft() > pos.get(i).getBottom() - pos.get(i).getTop()) {
				fatCat.add(i);
			} else {
				longCat.add(i);
			}
		}
		if (fatCat.size() >= longCat.size()) {
			for (int i = 0; i < pos.size(); ++i) {
				if (!(pos.get(i).getRight() - pos.get(i).getLeft() > pos.get(i).getBottom() - pos.get(i).getTop())) {
					pos.remove(i);
					--i;
				}
			}
		} else {
			for (int i = 0; i < pos.size(); ++i) {
				if (pos.get(i).getRight() - pos.get(i).getLeft() > pos.get(i).getBottom() - pos.get(i).getTop()) {
					pos.remove(i);
					--i;
				}
			}
		}
	}

	public static void deleteDuplicate(ArrayList<Word> pos) {
		for (int i = 1; i < pos.size();) {
			Word next = pos.get(i);
			Word prev = pos.get(i - 1);
			if (next.getTop() < prev.getBottom() && (next.getLeft() < prev.getRight() && prev.getLeft() < next.getRight())) {  
				prev.setLeft(next.getLeft() < prev.getLeft() ? next.getLeft() : prev.getLeft());
				prev.setRight(next.getRight() > prev.getRight() ? next.getRight() : prev.getRight());
				pos.remove(i);
			} else {
				++i;
			}
		}
	}
}
