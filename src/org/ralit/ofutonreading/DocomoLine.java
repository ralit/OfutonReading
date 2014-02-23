package org.ralit.ofutonreading;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.CountDownTimer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


class DocomoLine extends Thread {

	private Bitmap bmp;
	private String bookName;
	private ArrayList<Word> mWordList;
	private String attachName;
	private File tmpFile;

	public DocomoLine(Bitmap _bmp, String _bookName, String _attachName) {
		bmp = _bmp;
		bookName = _bookName;
		attachName = _attachName;
	}

	private JsonNode requestJobID() {
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("https://api.apigw.smt.docomo.ne.jp/characterRecognition/v1/line?APIKEY=" + ApiKey.getApiKey());
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			Fun.cacheImageForDocomo(bmp, 80, bookName, attachName);
			tmpFile = new File(Fun.DIR + bookName + "/" + attachName);
			FileBody fileBody = new FileBody(tmpFile, "image/jpeg");
			multipartEntity.addPart("image", fileBody);
			post.setEntity(multipartEntity);
			// 通信開始
			HttpResponse response = client.execute(post);

			// HttpResponseのEntityデータをStringへ変換
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			JsonNode jsonNode = new ObjectMapper().readTree(builder.toString());
			return jsonNode;
//			return response;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

//	private String parseJobID(HttpResponse response) {
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//			StringBuilder builder = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				builder.append(line + "\n");
//			}
//			JsonNode jsonNode = new ObjectMapper().readTree(builder.toString());
//			String jobID = jsonNode.path("job").path("@id").asText();
//			return jobID;
//		} catch(Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	private ArrayList<Word> parseMoji(JsonNode jsonNode) {
		JsonNode wordNode = null;
		ArrayList<Word> wordList = new ArrayList<Word>();
		Fun.log(jsonNode.toString());
		for (int i = 0; (wordNode = jsonNode.path("words").path("word").get(i)) != null; i++) {
			Word word = new Word();
			JsonNode pointNode = wordNode.path("shape").path("point");
			//			Fun.log(pointNode.toString());
			word.setPoint(pointNode.get(0).path("@x").asInt(), pointNode.get(0).path("@y").asInt(), pointNode.get(2).path("@x").asInt(), pointNode.get(2).path("@y").asInt());
			//			Fun.log(String.valueOf(word.getArea()));
			word.setText(wordNode.path("@text").asText());
			word.setScore(wordNode.path("@score").asInt());
			wordList.add(word);
		}
		return wordList;
	}

	public ArrayList<Word> getWordList() {
		return mWordList;
	}

	public void run() {
		try {
			JsonNode jsonNode = null;
			// 画像を送ってjobIDをもらう
			jsonNode = requestJobID();
			// JSONパース(jobIDの取得)
//			String jobID = parseJobID(response);
			// 認識結果取得待ち
			int waitingTime = 0;
			
			while(true) {
				// 認識結果をリクエスト
				//				jsonNode = requestResult(jobID);
				String status = jsonNode.path("job").path("@status").asText();
				if (!status.equals("process") && !status.equals("queue")) { break; }
				Thread.sleep(1000);
				Fun.log("status: " + status + " (" + ++waitingTime + " sec)");
			}
			// 認識結果が返ってきた
			// JSONパース(文字情報)
			ArrayList<Word> wordList = parseMoji(jsonNode);
			Collections.sort(wordList, new PointComparatorHorizontal());
			mWordList = wordList;
			for(Word word : mWordList) {
				Fun.log(word.getText());
			}
			tmpFile.delete();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}
