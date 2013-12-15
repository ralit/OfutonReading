package org.ralit.ofutonreading;

/**
 * ファイル名、クラス名からアンダーバーを取ってApiKeyにしてください。
 * 古い文字認識APIのAPIKEYはAPI_KEY_OLDに入れてください。
 * 新しい文字認識APIのAPIKEYはAPI_KEYに入れてください。
 * @author ralit
 */

class _ApiKey {

	private final static String API_KEY_OLD = "";
	private final static String API_KEY = "";

	public static String getApiKey() {
		return API_KEY;
	}
	
	public static String getApiKeyOld() {
		return API_KEY_OLD;
	}
}
