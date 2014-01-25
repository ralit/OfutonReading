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
	private CountDownTimer keyEventTimer; // BackボタンPress時の有効タイマー

	private JsonNode jsonNode;
	private String jobID;

	public DocomoLine(Bitmap _bmp, String _bookName) {
		bmp = _bmp;
		bookName = _bookName;
	}

	private HttpResponse requestJobID() {
		try {
			// HTTPClientはどっちかを使う
			// 1. org.apache.http.impl.client.DefaultHttpClient
			// 2. android.net.http.AndroidHttpClient
			DefaultHttpClient client = new DefaultHttpClient(); // (1) こっちも動きました
			//			AndroidHttpClient client = AndroidHttpClient.newInstance("Android UserAgent"); // (2)
			HttpPost post = new HttpPost("https://api.apigw.smt.docomo.ne.jp/characterRecognition/v1/scene?APIKEY=" + ApiKey.getApiKey());
			// これを知らなかった。MultipartのPOSTをするときはこのクラスを使おう。
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			//			FileBody fileBody = new FileBody(new File(filePath), "image/jpeg");
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.JPEG, 80, outStream);
			ByteArrayBody byteArrayBody = new ByteArrayBody(outStream.toByteArray(), "OfutonReading.jpg");
			//			multipartEntity.addPart("image", fileBody);
			multipartEntity.addPart("image", byteArrayBody);
			post.setEntity(multipartEntity);
			// 通信開始
			HttpResponse response = client.execute(post);
			return response;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private HttpResponse requestJobID2() {
		try {
			// HTTPClientはどっちかを使う
			// 1. org.apache.http.impl.client.DefaultHttpClient
			// 2. android.net.http.AndroidHttpClient
			DefaultHttpClient client = new DefaultHttpClient(); // (1) こっちも動きました
			//			AndroidHttpClient client = AndroidHttpClient.newInstance("Android UserAgent"); // (2)
			HttpPost post = new HttpPost("https://api.apigw.smt.docomo.ne.jp/characterRecognition/v1/scene?APIKEY=" + ApiKey.getApiKey());
			// これを知らなかった。MultipartのPOSTをするときはこのクラスを使おう。
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			Fun.cacheImageForDocomo(bmp, 80, bookName);
			FileBody fileBody = new FileBody(new File(Fun.DIR + bookName + "/tmpImageForDocomo.jpg"), "image/jpeg");
			//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			//			bmp.compress(CompressFormat.JPEG, 80, outStream);
			//			ByteArrayBody byteArrayBody = new ByteArrayBody(outStream.toByteArray(), "OfutonReading");
			multipartEntity.addPart("image", fileBody);
			//			multipartEntity.addPart("OfutonReading", byteArrayBody);
			post.setEntity(multipartEntity);
			// 通信開始
			HttpResponse response = client.execute(post);
			return response;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String parseJobID(HttpResponse response) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			JsonNode jsonNode = new ObjectMapper().readTree(builder.toString());
			String jobID = jsonNode.path("job").path("@id").asText();
			return jobID;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private JsonNode requestResult(String jobID) {
		try { 
			//			AndroidHttpClient client = AndroidHttpClient.newInstance("Android UserAgent");
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet("https://api.apigw.smt.docomo.ne.jp/characterRecognition/v1/scene/" + jobID + "?APIKEY=" + ApiKey.getApiKey());
			get.setHeader("Content-Type", "application/json");
			HttpResponse response = client.execute(get);
			// HttpResponseのEntityデータをStringへ変換
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			JsonNode jsonNode = new ObjectMapper().readTree(builder.toString());
			return jsonNode;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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
			// 画像を送ってjobIDをもらう
			HttpResponse response = requestJobID2();
			// JSONパース(jobIDの取得)
			String jobID = parseJobID(response);
			// 認識結果取得待ち
			int waitingTime = 0;
			JsonNode jsonNode = null;
			while(true) {
				Thread.sleep(1000);
				// 認識結果をリクエスト
				jsonNode = requestResult(jobID);
				String status = jsonNode.path("job").path("@status").asText();
				if (!status.equals("process") && !status.equals("queue")) { break; }
				Fun.log("status: " + status + " (" + ++waitingTime + " sec)");
			}
			// 認識結果が返ってきた
			// JSONパース(文字情報)
			ArrayList<Word> wordList = parseMoji(jsonNode);
			Collections.sort(wordList, new PointComparator());
			mWordList = wordList;
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}
