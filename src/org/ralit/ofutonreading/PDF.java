package org.ralit.ofutonreading;

/**
 * 呼び出すときは以下のページを参考にしましょう。
 * 
 * Y.A.M の 雑記帳: Android　大きい画像を効果的に読み込む
 * http://y-anz-m.blogspot.jp/2012/08/android.html
 * 
 * Y.A.M の 雑記帳: Android　バックグラウンドで Bitmap を処理する
 * http://y-anz-m.blogspot.jp/2012/08/android-bitmap.html
 * 
 * Y.A.M の 雑記帳: Android　Bitmap をキャッシュする
 * http://y-anz-m.blogspot.jp/2012/08/androidbitmap.html
 * 
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.artifex.mupdfdemo.MuPDFCore;

public class PDF {

	private MuPDFCore mCore;
	private int pageMax;
	
	public PDF(Context context, String filePath) {
		try {
			mCore = new MuPDFCore(context, filePath);
			pageMax = mCore.countPages();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getPageCount() {
		return pageMax;
	}
	
	public PointF getSize(int page) {
		return mCore.getPageSize(page);
	}
	
	public Bitmap getBitmap(int page, PointF size) {
		Bitmap bitmap = Bitmap.createBitmap((int)size.x, (int)size.y, Bitmap.Config.ARGB_8888);
		mCore.drawPage(bitmap, page, (int)size.x, (int)size.y, 0, 0, (int)size.x, (int)size.y);
		return bitmap;
	}
	
	public Bitmap getBitmap(int page) { // サイズを指定しなければ等倍で読み込む
		PointF size = getSize(page);
		Bitmap bitmap = Bitmap.createBitmap((int)size.x, (int)size.y, Bitmap.Config.ARGB_8888);
		mCore.drawPage(bitmap, page, (int)size.x, (int)size.y, 0, 0, (int)size.x, (int)size.y);
		return bitmap;
	}
}