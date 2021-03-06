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
import org.apache.tools.zip.ZipOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.PointF;

public class ZIP {

	private ZipFile zipFile;
	private String filePath;
	private float width;
	private float height;

	public ZIP(String _filePath) {
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
		return size - 1;
	}

	public PointF getSize() {
		PointF point = new PointF(width, height);
		return point;
	}

	public Bitmap openZip(int page, String bookName) {
		try {

			ZipFile zipFile_ = new ZipFile(new File(filePath));
			String encoding = zipFile_.getEncoding();
			zipFile_.close();
			zipFile_ = null;

			Fun.log("encoding: " + encoding);
			ZipFile zipFile = new ZipFile(new File(filePath), encoding);
			Enumeration<ZipEntry> zipEntries = zipFile.getEntries();

			for (int i = 0; i < page; i++) {
				zipEntries.nextElement();
			}

			ZipEntry zipEntry = zipEntries.nextElement();

			File dir = new File(Fun.DIR + bookName + "/tmp_zip");
			if (!dir.exists()) { dir.mkdir(); }

			InputStream inputStream = zipFile.getInputStream(zipEntry);

			BufferedOutputStream outStream = null;
			File file = null;

			file = new File(dir.getAbsolutePath() + "/" + zipEntry.getName());
			file.getParentFile().mkdirs();
			outStream = new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath() + "/" + zipEntry.getName()));

			byte[] buffer = new byte[1024 * 256];
			int len;
			while ((len = inputStream.read(buffer)) != -1) { outStream.write(buffer, 0, len); }
			inputStream.close();
			outStream.close();
			zipFile.close();
			inputStream = null;
			outStream = null;
			buffer = null;
			zipFile = null;
			

			File[] fileList = dir.listFiles();
			//			String tmpFilePath = fileList[0].getAbsolutePath();

			Bitmap bmp;
			
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				bmp = BitmapFactory.decodeStream(fileInputStream);
				fileInputStream.close();
				fileInputStream = null;
			} catch (Exception e) {
				Fun.log("画像が大きすぎたよ");
				Options option = new Options();
				option.inSampleSize = 2; 
				bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), option);
			}
			

			for(File f : fileList) {
				if(f.isDirectory()) {
					File[] d = f.listFiles();
					for(File f2 : d) {
						f2.delete();
					}
					f.delete();
				} else {
					f.delete();
				}
			}
			dir.delete();

			width = bmp.getWidth();
			height = bmp.getHeight();

			return bmp;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}



	//	public static void addZip(String zipFilePath, String addFilePath) throws IOException {
	//		ZipFile zipFile = new ZipFile(new File(zipFilePath), "SHIFT-JIS");
	//		
	//		ZipOutputStream zos = new ZipOutputStream(new File(zipFilePath));
	//		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(addFilePath)));
	//		String entryName = new File(addFilePath).getName();
	//		zos.putNextEntry(new ZipEntry(entryName));
	//		byte[] buffer = new byte[1024 * 256];
	//		int len;
	//		while ((len = bis.read(buffer)) != -1) { zos.write(buffer, 0, len); }
	//		bis.close();
	//		bis = null;
	//		buffer = null;
	//		zos.closeEntry();
	//		zos.close();
	//	}
}
