/*package org.ralit.ofutonreading;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import jp.ne.nttdocomo.spm.api.common.exception.SdkException;
import jp.ne.nttdocomo.spm.api.common.exception.ServerException;
import jp.ne.nttdocomo.spm.api.common.http.AuthApiKey;
import jp.ne.nttdocomo.spm.api.recognition.CharacterRecognize;
import jp.ne.nttdocomo.spm.api.recognition.CharacterRecognizeResult;
import jp.ne.nttdocomo.spm.api.recognition.constants.ImageContentType;
import jp.ne.nttdocomo.spm.api.recognition.constants.Lang;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizePointData;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizeResultData;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizeShapeData;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizeStatusData;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizeWordData;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizeWordsData;
import jp.ne.nttdocomo.spm.api.recognition.param.CharacterRecognizeJobInfoRequestParam;
import jp.ne.nttdocomo.spm.api.recognition.param.CharacterRecognizeRequestParam;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class Docomo {

	CharacterRecognizeRequestParam param;
	RecognizeResultData ret;
	
	public Docomo(String filePath, Lang lang, ImageContentType type) {
		param = new CharacterRecognizeRequestParam();
		param.setLang(lang);
		param.setImageContentType(type);
		param.setFilePath(filePath);
	}
	
	public Docomo(Bitmap bmp, Lang lang, ImageContentType type, int quality) {
		param = new CharacterRecognizeRequestParam();
		param.setLang(lang);
		param.setImageContentType(type);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		CompressFormat format = CompressFormat.JPEG;
		if (type == ImageContentType.IMAGE_JPEG) { format = CompressFormat.JPEG; }
		else if (type == ImageContentType.IMAGE_PNG) { format = CompressFormat.PNG; }
		bmp.compress(format, quality, bos);
		param.setImageData(bos.toByteArray());
	}
	
	public Docomo(Bitmap bmp) {
		param = new CharacterRecognizeRequestParam();
		param.setLang(Lang.CHARACTERS_JP);
		param.setImageContentType(ImageContentType.IMAGE_JPEG);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 80, bos);
		param.setImageData(bos.toByteArray());
	}
	
	public void recognize() {
		AuthApiKey.initializeAuth(ApiKey.getApiKey());
		CharacterRecognize recognize = new CharacterRecognize();
		RecognizeStatusData response = new RecognizeStatusData();
		try {
			response = recognize.request(param);
		} catch (SdkException e) {
			e.printStackTrace();
		} catch (ServerException e) {
			e.printStackTrace();
		}
		
		CharacterRecognizeJobInfoRequestParam jobParam = new CharacterRecognizeJobInfoRequestParam();
		jobParam.setJobId(response.getJob().getId());
		CharacterRecognizeResult recognizer = new CharacterRecognizeResult();
		ret = new RecognizeResultData();
		try {
			ret = recognizer.request(jobParam);
		} catch (SdkException e) {
			e.printStackTrace();
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ArrayList<RecognizePointData>> getShape() {
		RecognizeWordsData words = ret.getWords();
		ArrayList<RecognizeWordData> wordList = words.getWord();
		ArrayList<ArrayList<RecognizePointData>> pointList = new ArrayList<ArrayList<RecognizePointData>>();
		if (words != null) {
			for (RecognizeWordData word : wordList) {
				RecognizeShapeData shape = word.getShape();
				pointList.add(shape.getPoint());
			}
		}
		return pointList;
	}
	
	public ArrayList<RecognizeWordData> getWord() {
		RecognizeWordsData words = ret.getWords();
		ArrayList<RecognizeWordData> wordList = words.getWord();
		return wordList;
	}
}
*/