//package org.ralit.ofutonreading;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//
//import android.os.Environment;
//import android.util.Log;
//
//public class ZIP {
//
//	public ZIP() {
//		// TODO Auto-generated constructor stub
//	}
//
//    private void openZip() {
//    	Log.i(tag, "openZip()");
//    	try {
//			ZipInputStream zis = new ZipInputStream(new FileInputStream(filepath));
//			ZipEntry ze;
//			BufferedOutputStream bos;
//			int len;
//			
//			while ((ze = zis.getNextEntry()) != null) {
//				File file = new File(ze.getName());
//				File tmpdir = new File(Environment.getExternalStorageDirectory().getPath() + "/imagemove/tmp_zip");
//				try {
//					if (!tmpdir.exists()) { tmpdir.mkdir(); }
//				} catch (SecurityException e) {
//					e.printStackTrace();
//				}
//				bos = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove/tmp_zip/" + file.getName()));
//				byte[] buffer = new byte[1024];
//				while ((len = zis.read(buffer)) != -1) { bos.write(buffer, 0, len); }
//				zis.closeEntry();
//				bos.close();
//				bos = null;
//				
//				File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/imagemove/tmp_zip");
//				File[] fileList = dir.listFiles();
//				filepath = fileList[0].getAbsolutePath();
//				openImage();
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    }
//}
