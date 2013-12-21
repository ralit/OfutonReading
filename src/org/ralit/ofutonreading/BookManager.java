package org.ralit.ofutonreading;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.CountDownTimer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

interface RecognizeFinishedListener {
	void onRecognizeFinished();
}

public class BookManager {

	private String mBookName;
	private String mFilePath;
	private Context mContext;
	private int mCurLine = 0;
	private int mCurPage = 0;
	private String mReadFilePath = "";
	private long mFileSize;

	private enum FileType { pdf, zip, png, jpg };
	private FileType mType; 

	private PDF mPDF;
	private ArrayList<Word> mPosList;
	boolean mRecognized = false;

	//	private Docomo docomo;
	private DocomoOld docomo;
	private ArrayList<Word> wordList;
	
	boolean done = false;
	private ZIP zip;
	private Timer timer;

	private RecognizeFinishedListener mRecognizeFinishedListener;
	
	private void initializeBook() {
		if (mType == FileType.pdf) {
			mPDF = new PDF(mContext, mFilePath);
		} else if (mType == FileType.zip) {
			zip = new ZIP(mFilePath);
		}
	}

	private void checkWithFileType() {
		if (mType == FileType.pdf) {
			if (mPDF.getPageCount() < mCurPage) {
				Fun.log("PDFファイルのページ数よりmCurPageの方が大きい)");
			}
		} else if (mType == FileType.zip) {
			if (zip.getCount() < mCurPage) {
				Fun.log("PDFファイルのファイル数よりmCurPageの方が大きい)");
			}
		}
	}

	private PointF getSize(int page) {
		if (mType == FileType.pdf) { 
			return mPDF.getSize(page);
		} else if (mType == FileType.zip) {
			zip.openZip(page);
			//			zip.openZipMoreFaster(page);
			return zip.getSize();
		}
		return null;
	}

	public Bitmap getBitmap(int page) {
		if (mType == FileType.pdf) {
			return mPDF.getBitmap(page);
		} else if (mType == FileType.zip) {
			return zip.openZip(page);
			//			return zip.openZipMoreFaster(page);
		}
		return null;
	}

	public BookManager(String bookName, String filePath, Context context, RecognizeFinishedListener recognizeFinishedListener) {
		Fun.log("BookManager()");
		mBookName = bookName;
		mFilePath = filePath;
		mContext = context;
		mRecognizeFinishedListener = recognizeFinishedListener;
		mCurLine = readCurLine();
		mCurPage = readCurPage();
		mReadFilePath = readFilePath();
		mFileSize = readFileSize();
		mType = getFileType();

		if(mCurPage == -1) { mCurPage = 0; }
		if(mCurLine == -1) { mCurLine = 0; }

		initializeBook();

		mPosList = readPageLayout(mCurPage);

		Fun.log(String.valueOf(mCurLine));
		Fun.log(String.valueOf(mCurPage));
		Fun.log(mReadFilePath);
		Fun.log(String.valueOf(mFileSize));
		Fun.log(String.valueOf(mType));
		if (mPosList != null) {
			Fun.log(mPosList.toString());
		}
		check();
		saveCurLine();
		saveCurPage();
		saveFilePath();
		saveFileSize();
		if (!mRecognized) { 
			recognize();
		} else {
			mRecognizeFinishedListener.onRecognizeFinished();
		}
	}

	public void setCurLine(int curLine) {
		mCurLine = curLine;
	}

	public int getCurLine() {
		return mCurLine;
	}

	private void saveCurLine() {
		Fun.save(String.valueOf(mCurLine), Fun.DIR + mBookName + "/currentLine.txt", mBookName);
	}

	private int readCurLine() {
		Fun.log("readCurLine");
		try { return Integer.parseInt(Fun.read(Fun.DIR + mBookName + "/currentLine.txt")); } 
		catch (Exception e) { return -1; }
	}

	private void saveFilePath() {
		Fun.save(mFilePath, Fun.DIR + mBookName + "/filePath.txt", mBookName);
	}

	private String readFilePath() {
		return Fun.read(Fun.DIR + mBookName + "/filePath.txt");
	}

	private void saveFileSize() {
		Fun.save(String.valueOf(new File(mFilePath).length()), Fun.DIR + mBookName + "/fileSize.txt", mBookName);
	}

	private long readFileSize() {
		try { return Long.parseLong(Fun.read(Fun.DIR + mBookName + "/fileSize.txt")); } 
		catch (Exception e) { return -1; }
	}

	public void setCurPage(int curPage) {
		mCurPage = curPage;
		mPosList = readPageLayout(mCurPage);
		saveCurPage();
		if(mPosList != null) {
			if (mPosList.get(0).getLeft() == -1 && mPosList.get(0).getRight() == -1) {
				Fun.log("サイズが違うレイアウトデータが存在する場合");
			}
		}
		if (mPosList == null) { 
			mRecognized = false;
			recognize();
		} else { 
			mRecognized = true; 
		}
	}

	public int getCurPage() {
		return mCurPage;
	}

	private void saveCurPage() {
		Fun.save(String.valueOf(mCurPage), Fun.DIR + mBookName + "/currentPage.txt", mBookName);
	}

	private int readCurPage() {
		Fun.log("readCurPage");
		try { return Integer.parseInt(Fun.read(Fun.DIR + mBookName + "/currentPage.txt")); } 
		catch (Exception e) { Fun.log("catch in readCurPage()"); return -1; }
	}

	private void check() {
		Fun.log("check()");
		// ファイルの種類に依存しない共通の処理
		if (isReading()) {
			Fun.log("isReading == true");
			if (!mFilePath.equals(mReadFilePath)) {
				Fun.log("同じ名前の別のディレクトリにあるファイルを開こうとした");
			}
			if (new File(mFilePath).length() != mFileSize) {
				Fun.log("ファイルが変更された");
			}
			if(mPosList != null) {
				if (mPosList.get(0).getLeft() == -1 && mPosList.get(0).getRight() == -1) {
					Fun.log("サイズが違うレイアウトデータが存在する場合");
				}
			}
			if (mPosList == null) { mRecognized = false; }
			else { mRecognized = true; }
		}
		// ファイル種類別
		checkWithFileType();
	}

	private boolean isReading() {
		if (mCurPage == -1) { return false; } 
		else { return true; }
	}

	private FileType getFileType() {
		Fun.log("getFileType()");
		if (Fun.match(mFilePath, "\\.pdf$", true)) { return FileType.pdf; }
		if (Fun.match(mFilePath, "\\.zip$", true)) { return FileType.zip; }
		if (Fun.match(mFilePath, "\\.png$", true)) { return FileType.png; }
		if (Fun.match(mFilePath, "\\.jpe?g$", true)) { return FileType.jpg; }
		return null;
	}

	public ArrayList<Word> getPageLayout() {
		Fun.log("getPageLayout()");
		return mPosList;
	}

	private boolean savePageLayout() {
		PointF size = getSize(mCurPage);
		if (size == null) {
			Fun.log("画像のサイズが取得できなかった");
			return false;
		}
		String fileName = mCurPage + "_" + (int)size.x + "_" + (int)size.y + ".txt";
		Gson gson = new Gson();
		Fun.save(gson.toJson(mPosList, mPosList.getClass()), Fun.DIR + mBookName + "/layout/" + fileName, mBookName);
		return true;
	}

	private ArrayList<Word> readPageLayout(int page) {
		try {
			Fun.log("readPageLayout");
			PointF size = getSize(page);
			String fileName = page + "_" + (int)size.x + "_" + (int)size.y + ".txt";

			// しばらくエラー処理
			if (!new File(Fun.DIR + mBookName + "/layout/" + fileName).exists()) {
				Fun.log("少なくとも全く同じファイル名のLayoutデータは存在しない");
				File[] files = new File(Fun.DIR + mBookName + "/layout/").listFiles();
				ArrayList<String> names = new ArrayList<String>();
				for (File file : files) { names.add(file.getName()); }
				String conflictName = null;
				for (String name : names) { 
					if(Fun.match(name, page + "_[0-9]+?_[0-9]+?\\.txt", false)) { conflictName = name; }
				}
				if (conflictName != null) {
					Fun.log("サイズが違うLayoutデータは存在する");
					ArrayList<Word> list = new ArrayList<Word>();
					Word word = new Word();
					word.setPoint(-1, -1, -1, -1);
					list.add(word);
					return list;
				} else {
					Fun.log("まだ開いたことがないページ");
					return null;
				}
			}

			Fun.log("同じファイル名のLayoutデータが存在する");
			Gson gson = new Gson();
			String json = Fun.read(Fun.DIR + mBookName + "/layout/" + fileName);
			Type type = new TypeToken<ArrayList<Word>>(){}.getType();
			return gson.fromJson(json, type);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public boolean isRecognized() {
		return mRecognized;
	}

	public void recognize() {
		Fun.log("recognize()");
		Bitmap bmp = getBitmap(mCurPage);
		//			PointF size = mPDF.getSize(mCurPage);
		//			size.x = size.x * 2;
		//			size.y = size.y * 2;
		//			Fun.log(String.valueOf(size.x));
		//			Fun.log(String.valueOf(size.y));
		//			Bitmap bmp = mPDF.getBitmap(mCurPage, size);
		//			docomo = new Docomo(bmp, mBookName);
		docomo = new DocomoOld(bmp);
		docomo.start();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				wordList = docomo.getWordList();
				if(wordList == null) {
					Fun.log("wordListはnull");
				} else {
					timer.cancel();
					Fun.log("wordList取得!");
					mPosList = wordList;
					mRecognized = true;
					savePageLayout();
					mRecognizeFinishedListener.onRecognizeFinished();
					Fun.paintPosition(getBitmap(mCurPage), mPosList, mBookName, mCurPage);
				}
			}
		}, 0, 1000);
	}
}
