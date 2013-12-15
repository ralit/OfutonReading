package org.ralit.ofutonreading;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.recognize.HttpSceneryLineLayoutAnalysisRequest;
import jp.recognize.SceneryLineLayoutAnalyzer;
import jp.recognize.client.HttpSceneryLineLayoutAnalyzer;
import jp.recognize.common.ImageContentType;
import jp.recognize.common.RecognitionResult.LineLayout;
import jp.recognize.common.Shape.Rectangle;
import jp.recognize.common.client.HttpSceneryRecognitionRequest.InputStreamImageContent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.CountDownTimer;

class DocomoOld extends Thread{

	private LineLayout[] job;
	private Bitmap bmp;
	private ArrayList<Word> wordList;


	public DocomoOld(Bitmap _bmp) {
		bmp = _bmp;
	}

	public ArrayList<Word> getWordList() {
		return wordList;
	}

	@Override
	public void run() {
		// ここは別スレッド。この中で止まってもUIには影響しない。
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.JPEG, 80, bos);
			SceneryLineLayoutAnalyzer analyzer;
			analyzer = new HttpSceneryLineLayoutAnalyzer(new URL("https://recognize.jp/v1/scenery/api/line-region"));
			job = analyzer.analyze(new HttpSceneryLineLayoutAnalysisRequest(
					ApiKey.getApiKeyOld(), 
					"standard", 
					new InputStreamImageContent(ImageContentType.IMAGE_JPEG, new ByteArrayInputStream(bos.toByteArray())),
					null /* new HttpSceneryLineLayoutAnalysisHint(aImageTrimRectangle, aImageRotationDegree, aLetterColor) */
					));

			// jobに結果が返ってくるまで上の行で止まっている。
			ArrayList<Word> posList = new ArrayList<Word>();
			for (LineLayout line : job) {
				Rectangle bounds = line.getShape().getBounds();
				Word word = new Word();
				word.setPoint(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom());
				posList.add(word);
			}
			wordList = posList;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
	}
}
