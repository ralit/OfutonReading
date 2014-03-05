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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

public class ZIP_ {

	private ZipFile zipFile;
	private String filePath;
	private Bitmap bmp;

	public ZIP_(String _filePath) {
		filePath = _filePath;
		try {
			zipFile = new ZipFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getCount() {
		Fun.log("ZIP.getcount->zipFile.size(): " + zipFile.size());
		return zipFile.size() - 1;
	}
	
	public PointF getSize() {
		PointF point = new PointF(bmp.getWidth(), bmp.getHeight());
		return point;
	}

	public Bitmap openZip(int page, String bookName) {
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath));
			for (int i = 0; i < page; i++) {
				zipInputStream.getNextEntry();
			}

			ZipEntry zipEntry = zipInputStream.getNextEntry();
//			ZipEntry zipEntry = zipInputStream.();

			File dir = new File(Fun.DIR + bookName + "/tmp_zip");
			if (!dir.exists()) { dir.mkdir(); }

			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath() + "/" + zipEntry.getName()));
			byte[] buffer = new byte[1024 * 256];
			int len;
			while ((len = zipInputStream.read(buffer)) != -1) { outStream.write(buffer, 0, len); }
			zipInputStream.closeEntry();
			outStream.close();
			outStream = null;
			zipInputStream.closeEntry();
			zipInputStream.close();

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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 間違ったコード
	public Bitmap openZipFaster(int page) {
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath));
			for (int i = 0; i < page; i++) {
				zipInputStream.getNextEntry();
			}

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			byte[] buffer = new byte[1024 * 256];
			BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(buffer));
			int len;
			while ((len = zipInputStream.read(buffer)) != -1) { inputStream.read(buffer, 0, len); }
			bmp = BitmapFactory.decodeStream(inputStream);
			zipInputStream.closeEntry();
			inputStream.close();
			inputStream = null;
			zipInputStream.closeEntry();
			zipInputStream.close();

			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 間違ったコード
	public Bitmap openZipMoreFaster(int page) {
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath));
			for (int i = 0; i < page; i++) {
				zipInputStream.getNextEntry();
			}
			bmp = BitmapFactory.decodeStream(zipInputStream);
			zipInputStream.closeEntry();
			zipInputStream.close();
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
