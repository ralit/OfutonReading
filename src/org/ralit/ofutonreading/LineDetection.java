package org.ralit.ofutonreading;

import java.io.IOException;
import java.util.ArrayList;

import org.ralit.ofutonreading.Rect;

import android.graphics.Point;

class LineDetection {

	private int w;
	private int h;
	private int[][] gray;
	private int[][] edged; // 2値(0xffと0x00)を想定する

	public LineDetection(int w, int h, int[][] gray, int[][] edged) {
		this.w = w;
		this.h = h;
		this.gray = gray;
		this.edged = edged;
	}

	public ArrayList<Word> getWordList() {
		try {
			final int DIV = 40;
			final int minLineLength = 4;
//			return deleteShortPlot(deleteOutlier(plot(deleteShortLine(getLine(deleteLongCats(getColBlock(DIV))), 4))), 4);
			ArrayList<ArrayList<Rect>> list =  deleteShortLine(getLine(deleteLongCats(getColBlock(DIV))), 4);
			return getWordList(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<Word> getWordList(ArrayList<ArrayList<Rect>> list) {
		ArrayList<Word> wordList = new ArrayList<Word>();
		for (ArrayList<Rect> rects : list) {
			int left = 0;
			int top = h;
			int bottom = 0;
			int right = 0;
			for (int i = 0; i < rects.size(); i++) {
				Rect rect = rects.get(i);
				if (i == 0) {
					left = rect.x;
				}
				if (i == rects.size() - 1) {
					right = rect.x + rect.w;
				}
				if (rect.y < top) {
					top = rect.y;
				}
				if (rect.y + rect.h > bottom) {
					bottom = rect.y + rect.h;
				}
			}
			Word word = new Word();
			word.setPoint(left, top, right, bottom);
			wordList.add(word);
		}
		return wordList;
	}
	
	private ArrayList<ArrayList<Rect>> getColBlock(int DIV) throws IOException {
		int block = w / DIV;

		ArrayList<ArrayList<Rect>> cols = new ArrayList<ArrayList<Rect>>();
		for(int i = 0; i < DIV; i++) {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			Rect rect = null;
			for(int y = 0; y < h; y++){
				boolean exist = false;
				for(int x = i*block; x < (i+1)*block; x++){
					if (edged[y][x] > 0) {
						exist = true;
						break;
					}
				}

				if(exist) {
					if(rect == null) {
						rect = new Rect(i*block, y, block, 0);
					} else {
						rect.h = rect.h + 1;
					}
				} else {
					if(rect != null) {
						rects.add(rect);
						rect = null;
					}
				}
			}
			cols.add(rects);
		}
//		writeRects(bmp, cols, testDir + "getColBlocks.jpg");
		return cols;
	}

	// 縦に長いものを取り除く	
	private ArrayList<ArrayList<Rect>> deleteLongCats(ArrayList<ArrayList<Rect>> cols) throws IOException {
		int max = 0;
		for (ArrayList<Rect> rects : cols) {
			//			System.out.println(rects.size() + "rects");
			for (Rect rect : rects) {
				if(rect.h > max) {
					max = rect.h;
				}
			}
		}

		boolean[] lengthDistribution = new boolean[max + 1];
		for (ArrayList<Rect> rects : cols) {
			for (Rect rect : rects) {
				lengthDistribution[rect.h] = true; 
			}
		}

		int maxForUse = -1;
		for (int i = lengthDistribution.length - 1; i >= 0; i--) {
			if(lengthDistribution[i]) {
				if(maxForUse == -1) {
					maxForUse = i;
				} else {
					break;
				}
			} else {
				maxForUse = -1;
			}
		}

		for(int i = 0; i < cols.size(); i++) {
			for(int j = 0; j < cols.get(i).size(); j++) {
				if(cols.get(i).get(j).h > maxForUse || cols.get(i).get(j).h <= 1) {
					cols.get(i).remove(j); // 時間のかかる処理
					j--;
				}
			}
		}
//		writeRects(bmp, cols, testDir + "deleteLongcats.jpg");
		return cols;
	}

	private ArrayList<ArrayList<Rect>> getLine(ArrayList<ArrayList<Rect>> cols) throws IOException {
		ArrayList<ArrayList<Rect>> connection = new ArrayList<ArrayList<Rect>>();
		for(int i = 0; i < cols.size() - 1; i++) {
			System.out.println(cols.get(i).size());
			for(int j = 0; j < cols.get(i).size(); j++) {
				ArrayList<Rect> last = new ArrayList<Rect>();
				last.add(cols.get(i).get(j));
				connection.add(last);
				//				System.out.println(connection.size());
				searchConnection(cols.get(i).get(j), cols, i+1, connection);
			}
		}
//		writeRects(bmp, connection, testDir + "getLine.jpg");
		return connection;
	}

	// 再帰的に右につなげる
	private void searchConnection(Rect rect, ArrayList<ArrayList<Rect>> cols, int colNum, ArrayList<ArrayList<Rect>> connection) {
		ArrayList<Rect> tmp = new ArrayList<Rect>();
		for(int k = 0; k < cols.get(colNum).size(); k++) {
			if(!(cols.get(colNum).get(k).y + cols.get(colNum).get(k).h < rect.y) && !(rect.y + rect.h < cols.get(colNum).get(k).y)) {
				tmp.add(cols.get(colNum).get(k));
				cols.get(colNum).remove(k);
				k--;
			}
		}
		if(!tmp.isEmpty()) {
			int last = tmp.size() - 1;
			Rect newRect = new Rect(tmp.get(0).x, tmp.get(0).y, tmp.get(0).w, tmp.get(last).y + tmp.get(last).h - tmp.get(last).y);
			connection.get(connection.size() - 1).add(newRect);
			if (colNum < cols.size() - 1) {
				searchConnection(newRect, cols, colNum + 1, connection);
			}
		}
	}

	// connectionの選別
	private ArrayList<ArrayList<Rect>> deleteShortLine(ArrayList<ArrayList<Rect>> connection, final int minLineLength) throws IOException {
		int maximum = 0;
		for (int i = 0; i < connection.size(); i++) {
			if(connection.get(i).size() > maximum) { maximum = connection.get(i).size(); }
		}
		int[] connectionLength = new int[maximum + 1];
		for (int i = 0; i < connection.size(); i++) {
			connectionLength[connection.get(i).size()]++;
		}
		int lineCount = 0;
		int minLength;
		for (minLength = connectionLength.length - 1; minLength >= 0; minLength--) {
			lineCount += connectionLength[minLength];
			if (connectionLength[minLength] == 0) {
//				if (lineCount > minLineLength && minLength > connectionLength.length/2) { // connectionLength.length => maximum の方がいい(ほとんど変わらないけど)
				if (lineCount > minLineLength && minLength < connectionLength.length/2) { // 本の場合は、短い行もあるので、逆のほうが適する。(これだと0まで行くのでは？)
					break;
				}
			} 
		}
		for (int i = 0; i < connection.size(); i++) {
			if(connection.get(i).size() < minLength) {
				connection.remove(i);
				i--;
			}
		}
//		writeRects(bmp, connection, testDir + "deleteShortLine.jpg");
		return connection;
	}

}


class Rect {
	public int x;
	public int y;
	public int w;
	public int h;
	public Rect(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	public String toString() {
		//		return "(" + x + " - " + (x+w) + ", " + y + " - " + (y+h) + ")";
		return "point (" + x + ", " + y + "); length (" + w + ", " + h + ")";
	}
}
