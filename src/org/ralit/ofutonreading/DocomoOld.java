//package org.ralit.ofutonreading;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//
//import jp.recognize.HttpSceneryLineLayoutAnalysisRequest;
//import jp.recognize.SceneryLineLayoutAnalyzer;
//import jp.recognize.client.HttpSceneryLineLayoutAnalyzer;
//import jp.recognize.common.ImageContentType;
//import jp.recognize.common.RecognitionResult.LineLayout;
//import jp.recognize.common.Shape.Rectangle;
//import jp.recognize.common.client.HttpSceneryRecognitionRequest.InputStreamImageContent;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
//
//class DocomoOld {
//
//	LineLayout[] job;
//	
//	public DocomoOld(Bitmap bmp) {
//		Recognize thread = new Recognize(bmp, job);
//		thread.start();
//		try {
//			thread.join();
//			job = thread.getLineLayout();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public DocomoOld(Bitmap bmp, ImageContentType type, int quality, String precision) {
//		RecognizeVerbose thread = new RecognizeVerbose(bmp, type, quality, precision, job);
//		thread.start();
//		try {
//			thread.join();
//			job = thread.getLineLayout();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public ArrayList<ArrayList<Integer>> getPos() {
//		float marginRatio = 0; // 余白はここではなく、最終的に切り出すときに設定すべきだ(おかしくなるので)
//		ArrayList<ArrayList<Integer>> posList = new ArrayList<ArrayList<Integer>>();
//		for (LineLayout line : job) {
//			Rectangle bounds = line.getShape().getBounds();
//			ArrayList<Integer> internal = new ArrayList<Integer>();
//			float margin = (bounds.getBottom() - bounds.getTop()) * marginRatio;
//			internal.add(bounds.getLeft() - (int)margin);
//			internal.add(bounds.getTop() - (int)margin);
//			internal.add(bounds.getRight() + (int)margin);
//			internal.add(bounds.getBottom() + (int)margin);
//			posList.add(internal);
//		}
//		return posList;
//	}
//}
//
//class Recognize extends Thread {
//	
//	private Bitmap mBmp;
//	private LineLayout[] mJob;
//	
//	public Recognize (Bitmap bmp, LineLayout[] job) {
//		mBmp = bmp;
//		mJob = job;
//	}
//	
//	public void run() {
//		try {
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			mBmp.compress(CompressFormat.JPEG, 80, bos);
//			SceneryLineLayoutAnalyzer analyzer;
//			analyzer = new HttpSceneryLineLayoutAnalyzer(new URL("https://recognize.jp/v1/scenery/api/line-region"));
//			mJob = analyzer.analyze(new HttpSceneryLineLayoutAnalysisRequest(
//					ApiKey.getApiKeyOld(), 
//					"standard", 
//					new InputStreamImageContent(ImageContentType.IMAGE_JPEG, new ByteArrayInputStream(bos.toByteArray())),
//					null /* new HttpSceneryLineLayoutAnalysisHint(aImageTrimRectangle, aImageRotationDegree, aLetterColor) */
//					));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public LineLayout[] getLineLayout() {
//		return mJob;
//	}
//}
//
//class RecognizeVerbose extends Thread {
//	
//	private Bitmap mBmp;
//	private LineLayout[] mJob;
//	ImageContentType mType;
//	int mQuality;
//	String mPrecision;
//	
//	public RecognizeVerbose (Bitmap bmp, ImageContentType type, int quality, String precision, LineLayout[] job) {
//		mBmp = bmp;
//		mJob = job;
//		mType = type;
//		mQuality = quality;
//		mPrecision = precision;
//	}
//	
//	public void run() {
//		try {
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			CompressFormat format = CompressFormat.JPEG;
//			if (mType == ImageContentType.IMAGE_JPEG) { format = CompressFormat.JPEG; }
//			else if (mType == ImageContentType.IMAGE_PNG) {format = CompressFormat.PNG; } 
//			mBmp.compress(format, mQuality, bos);
//			SceneryLineLayoutAnalyzer analyzer;
//			analyzer = new HttpSceneryLineLayoutAnalyzer(new URL("https://recognize.jp/v1/scenery/api/line-region"));
//			mJob = analyzer.analyze(new HttpSceneryLineLayoutAnalysisRequest(
//					ApiKey.getApiKeyOld(), 
//					mPrecision, 
//					new InputStreamImageContent(mType, new ByteArrayInputStream(bos.toByteArray())),
//					null /* new HttpSceneryLineLayoutAnalysisHint(aImageTrimRectangle, aImageRotationDegree, aLetterColor) */
//					));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public LineLayout[] getLineLayout() {
//		return mJob;
//	}
//}
