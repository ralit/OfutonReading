package org.ralit.ofutonreading;

import static org.ralit.ofutonreading.ImageUtility.*;

import java.io.IOException;

class Edge {

	private int w;
	private int h;
	private int[][] gray;
	private int[][] edged;

	public Edge(int w, int h, int[][] gray) throws IOException {
		this.w = w;
		this.h = h;
		this.gray = gray;
	}
	
	public int[][] getEdgedBinaryBitmap() {
		try {
			edgeSobel();
			final int threshold = ohtsu(getHistogram());
			System.out.println(threshold + "threshold");
			return threshold(new Threshold() {
				@Override
				public boolean evaluate(int Y) {
					if (threshold < Y) { return true; }
					else { return false; }
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Sobelオペレータによるエッジ検出
	private void edgeSobel() throws IOException {
		int[][] sobelx = {
				{1, 0, -1},
				{2, 0, -2},
				{1, 0, -1}
		};
		int[][] sobely = {
				{1,  2,  1},
				{0,  0,  0},
				{-1, -2, -1}
		};
		edged = new int[h][w];
		for(int y = 1; y < h-1; y++){
			for(int x = 1; x < w-1; x++){
				int dx = 0;
				int dy = 0;
				for(int yy = 0; yy < 3; yy++){
					for(int xx = 0; xx < 3; xx++){
						dx += gray[y][x + xx - 1] * sobelx[yy][xx];
						dy += gray[y + yy - 1][x] * sobely[yy][xx]; // 怪しい。どこかからコピペしたのか自分で書いたのかどっちだ。
//						dy += gray[y][x + yy - 1] * sobely[yy][xx]; // 怪しい。どこかからコピペしたのか自分で書いたのかどっちだ。
//						dy += gray[(x + y*w) + yy - 1] * sobely[xx + yy*3];
					}
				}
				int filtered = (int)(Math.sqrt(dx*dx + dy*dy) / 8.0);
//				int newRGB = rgb(filtered, filtered, filtered);
//				result[y][x] = newRGB;
				edged[y][x] = filtered;
			}
		}
	}

	// 輝度成分のみの配列からヒストグラムを返す
	private int[] getHistogram() {
		int[] histogram = new int[256];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				histogram[edged[y][x]]++;
			}
		}
		return histogram;
	}

	// 大津の閾値判別法(閾値を返す)
	private int ohtsu(int[] histogram) {
		double max = -1000; // -1000ってなんだろうね
		int threshold = 0;

		for (int i = 1; i < 255; i++) {

			int densityA = 0;
			int pixelsA = 0;
			for (int j = 0; j < i; j++) {
				densityA += histogram[j] * j;
				pixelsA += histogram[j];
			}

			int densityB = 0;
			int pixelsB = 0;
			for (int j = i; j < 256; j++) {
				densityB += histogram[j] * j;
				pixelsB += histogram[j];
			}

			double meanDensityA = 0;
			double meanDensityB = 0;
			if (pixelsA != 0) { meanDensityA = (double)densityA / (double)pixelsA; }
			if (pixelsB != 0) { meanDensityB = (double)densityB / (double)pixelsB; }
			double meanDensity = (double)(densityA + densityB) / (double)(pixelsA + pixelsB);
			double ratioPixelsA = (double)pixelsA / (double)(pixelsA + pixelsB);
			double ratioPixelsB = (double)pixelsB / (double)(pixelsA + pixelsB);
			double dispersion = ratioPixelsA * (meanDensityA - meanDensity) * (meanDensityA - meanDensity) + ratioPixelsB * (meanDensityB - meanDensity) * (meanDensityB - meanDensity);

//			System.out.println("dispersion: " + dispersion);
			if (max < dispersion) {
				max = dispersion;
				threshold = i;
			}
		}
		return threshold;
	}
	
	// 任意の評価関数を渡して2値化する
	private int[][] threshold(Threshold threshold) throws IOException {
		int[][] binary = new int[h][w];
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
//				int newRGB
				int newGray;
				if(threshold.evaluate(edged[y][x])) {
//					newRGB = rgb(0xff, 0xff, 0xff);
					newGray = 0xff;
				} else {
//					newRGB = rgb(0x00, 0x00, 0x00);
					newGray = 0x00;
				}
				binary[y][x] = newGray;
			}
		}
		return binary;
	}
}


abstract class Threshold {

	public abstract boolean evaluate(int Y);

}
