package org.ralit.ofutonreading;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipEntry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

public class ZipAnt {

	private ZipFile zipFile;
	private String filePath;
	private Bitmap bmp;

	public ZipAnt(String _filePath) {
		filePath = _filePath;
		try {
			zipFile = new ZipFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getCount() {
//		Fun.log("ZIP.getcount->zipFile.size(): " + zipFile.size());
		int size = 0;
		long time1 = System.currentTimeMillis();
		for(Enumeration<ZipEntry> e = zipFile.getEntries(); e.hasMoreElements(); size++) {
			e.nextElement();
		}
		long time2 = System.currentTimeMillis();
		Fun.log("ZipCountTime: " + (time2 - time1));
		Fun.log("ZIP.getcount->zipFile.size(): " + size);
		return size;
	}
	
	public PointF getSize() {
		PointF point = new PointF(bmp.getWidth(), bmp.getHeight());
		return point;
	}

	public Bitmap openZip(int page, String bookName) {
		try {
			
			ZipFile zipFile = new ZipFile(new File(filePath));
			Enumeration<ZipEntry> zipEntries = zipFile.getEntries();
			
			for (int i = 0; i < page; i++) {
				zipEntries.nextElement();
			}
			
			ZipEntry zipEntry = zipEntries.nextElement();
			
			File dir = new File(Fun.DIR + bookName + "/tmp_zip");
			if (!dir.exists()) { dir.mkdir(); }
			
			InputStream inputStream = zipFile.getInputStream(zipEntry);
			
			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath() + "/" + zipEntry.getName()));
			byte[] buffer = new byte[1024 * 256];
			int len;
			while ((len = inputStream.read(buffer)) != -1) { outStream.write(buffer, 0, len); }
			inputStream.close();
			outStream.close();
			inputStream = null;
			outStream = null;
			buffer = null;
			
			File[] fileList = dir.listFiles();
			String tmpFilePath = fileList[0].getAbsolutePath();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;
			File tmpFile = new File(tmpFilePath);
			FileInputStream fileInputStream = new FileInputStream(tmpFile);
			bmp = BitmapFactory.decodeStream(fileInputStream);
			
			for(File file : fileList) {
				file.delete();
			}
			dir.delete();
			
			return bmp;
			
//			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath));
//			for (int i = 0; i < page; i++) {
//				zipInputStream.getNextEntry();
//			}
//
//			ZipEntry zipEntry = zipInputStream.getNextEntry();
////			ZipEntry zipEntry = zipInputStream.();
//
//			File dir = new File(Fun.DIR + bookName + "/tmp_zip");
//			if (!dir.exists()) { dir.mkdir(); }

//			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath() + "/" + zipEntry.getName()));
//			byte[] buffer = new byte[1024 * 256];
//			int len;
//			while ((len = zipInputStream.read(buffer)) != -1) { outStream.write(buffer, 0, len); }
//			zipInputStream.closeEntry();
//			outStream.close();
//			outStream = null;
//			zipInputStream.closeEntry();
//			zipInputStream.close();

//			File[] fileList = dir.listFiles();
//			String tmpFilePath = fileList[0].getAbsolutePath();
//
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inScaled = false;
//			File tmpFile = new File(tmpFilePath);
//			FileInputStream fileInputStream = new FileInputStream(tmpFile);
//			bmp = BitmapFactory.decodeStream(fileInputStream);
//			
//			for(File file : fileList) {
//				file.delete();
//			}
//			dir.delete();
//			
//			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
