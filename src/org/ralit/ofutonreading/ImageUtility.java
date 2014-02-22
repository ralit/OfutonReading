package org.ralit.ofutonreading;

import static org.ralit.ofutonreading.ImageUtility.rgb2ycbcr;
import static org.ralit.ofutonreading.ImageUtility.y;

public class ImageUtility{
	
	public static int a(int c){
		return c>>>24;
	}
	
	public static int r(int c) {
		return c>>16&0xff;
	}
	
	public static int g(int c) {
		return c>>8&0xff;
	}
	
	public static int b(int c) {
		return c&0xff;
	}
	
	public static int rgb(int r,int g,int b) {
		return 0xff000000 | r <<16 | g <<8 | b;
	}
	
	public static int argb(int a,int r,int g,int b) {
		return a<<24 | r <<16 | g <<8 | b;
	}
	
	public static int rgb2ycbcr(int rgb) {
		int r = r(rgb);
		int g = g(rgb);
		int b = b(rgb);
		int y = (int)(0.2989 * r + 0.5866 * g + 0.1145 * b);
		int cb = (int)(-0.1687 * r - 0.3312 * g + 0.5000 * b) + 128;
		int cr = (int)(0.5000 * r - 0.4183 * g - 0.0816 * b) + 128;
		return 0xff000000 | y <<16 | cb <<8 | cr;
	}
	
	public static int y(int ycrcb) {
		return ycrcb>>16 & 0xff;
	}
	
	public static int cb(int ycrcb) {
		return ycrcb>>8 & 0xff;
	}
	
	public static int cr(int ycrcb) {
		return ycrcb & 0xff;
	}
	
	public static int ycbcr(int y, int cb, int cr) {
		return 0xff000000 | y <<16 | cb <<8 | cr;
	}
	
	public static int ycbcr2rgb(int ycbcr) {
		int y = y(ycbcr);
		int cb = cb(ycbcr) - 128;
		int cr = cr(ycbcr) - 128;
		int r = (int)(y               + 1.4022 * cr);
		int g = (int)(y - 0.3456 * cb - 0.7145 * cr);
		int b = (int)(y + 1.7710 * cb              );
		return rgb(r, g, b);
	}
	
	public static void one2two(int[] pixels, int[][] bitmap, int h, int w) {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				bitmap[y][x] = y(rgb2ycbcr(pixels[y*w + x]));
			}
		}
	}
	

}