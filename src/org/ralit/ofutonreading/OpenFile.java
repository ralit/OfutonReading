package org.ralit.ofutonreading;

/**
 * 以下のコードをほぼ丸ごと使いました。
 * 
 * サンプルプログラム工場
 * [Android]ファイル/フォルダ選択ダイアログのサンプル#tryFileOpenDialog00
 * http://junkcode.aakaka.com/archives/675
 * 
 */

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.util.Log;

interface OpenFileListener {
	void onFileSelected(final File file);
}


class OpenFile implements OnClickListener{

	private Context mParent;
	private int mSelectedItemIndex = -1;
	private File[] mFileList;
	private String mCurDir;
	private Stack<String> mDirs = new Stack<String>();
	private OpenFileListener mListener;
	private File mLastSelectedItem;
	
	public OpenFile(final Context parent, final OpenFileListener listener) {
		super();
		Fun.log("OpenFile()");
		mParent = parent;
		mListener = listener;
	}

	public void openDir(String dir_, boolean isFirst) {
		Fun.log("OpenDir()");
		
		if(isFirst) {
			String dir = null;
			if (dir_ != null) {
				String.copyValueOf(dir_.toCharArray());
			}
			// >> 前回開いたディレクトリを開くようにした
			if (dir == null) {
				String lastDir = Fun.read(Fun.DIR + "lastDir.txt");
				if (lastDir == null) {
					lastDir = Environment.getExternalStorageDirectory().getAbsolutePath();
				}
				dir = lastDir;
			}
			
			mCurDir = Environment.getExternalStorageDirectory().getAbsolutePath();
			ArrayList<ArrayList<String>> tree = new ArrayList<ArrayList<String>>();
			String matchStr = Environment.getExternalStorageDirectory().getAbsolutePath();
			while ((tree = Fun.matchGroup(dir, "(" + matchStr + "\\/.+?)(\\/|$)", false)) != null) {
				Fun.log(String.valueOf(tree));
				matchStr = tree.get(0).get(0);
				Fun.log(dir);
				Fun.log(matchStr);
				String tmp = String.copyValueOf(matchStr.toCharArray());
				mDirs.push(mCurDir);
				openDir(tmp, false);
			}
			// << 前回開いたディレクトリを開くようにした
		}
		
		
		mFileList = new File(mCurDir).listFiles();
		mCurDir = dir_;
		
		String[] fileNameList = null;
		int itemCount = 0;
		
		// "↑"をファイル一覧の先頭に表示する(ルートディレクトリ以外で)
		if (0 < mDirs.size()) {
			fileNameList = new String[mFileList.length + 1];
			fileNameList[itemCount] = "↑";
			++itemCount;
		} else {
			fileNameList = new String[mFileList.length];
		}
		
		// ファイル一覧を表示する準備(ファイルとディレクトリの見た目を変えよう)
		for (File file : mFileList) {
			if (file.isDirectory()) {
				fileNameList[itemCount] = file.getName() + "/";
			} else {
				fileNameList[itemCount] = file.getName();
			}
			++itemCount;
		}
		
		// そして最後にファイル一覧をダイアログで表示
		new AlertDialog.Builder(mParent).setTitle(dir_).setItems(fileNameList, this).show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		mSelectedItemIndex = which;
		if (mFileList == null) { return; }
		int selectedItemIndex = mSelectedItemIndex;
		
		if (0 < mDirs.size()) { --selectedItemIndex; } // "↑"を表示した分インデックスがずれる
		
		if (selectedItemIndex < 0) { // "↑"をタップしたとき
			openDir(mDirs.pop(), true);
		} else { // "↑"以外をタップしたとき
			mLastSelectedItem = mFileList[selectedItemIndex];
			if (mLastSelectedItem.isDirectory()) {
				mDirs.push(mCurDir);
				openDir(mLastSelectedItem.getAbsolutePath(), true);
			} else {
				mListener.onFileSelected(mLastSelectedItem);
			}
		}
		
	}
}


/**
 * 選択されたときに呼び出されるリスナー
 *
 */
interface FileOpenDialogListener {
    void onFileSelected(final File file);
}
 
/**
 * ファイルが選択されたダイアログ
 *
 */
class FileOpenDialog implements DialogInterface.OnClickListener {
 
    private Context mParent = null; // 親のコンテキスト
    private int mSelectedItemIndex = -1;    // 選択中のアイテムインデックス
    private File[] mFileList;   // 表示中のファイルのリスト
     
    private String mCurrDirectory = null;   // 今居るディレクトリ
    private Stack<String> mDirectorys = new Stack<String>();    // ディレクトリ
     
    private FileOpenDialogListener mListener;   // リスナー
     
    private boolean mOpenDirectory; // ディレクトリを開く
    private File mLastSelectedItem; // 最後に選択されたモノ
 
    /**
     * コンストラクタ
     * @param parent 親のコンテキスト
     * @param listener 選択が決まったときに呼び出される
     * @param openDirectory true:ディレクトリを開く
     */
    public FileOpenDialog(final Context parent, final FileOpenDialogListener listener, boolean openDirectory) {
        super();
         
        // コンテキスト
        this.mParent = parent;
         
        // リスナー
        this.mListener = listener;
         
        // ディレクトリだけを開くか
        this.mOpenDirectory = openDirectory;
    }
 
    /**
     * ダイアログが選択されたときに呼び出される
     */
    public void onClick(DialogInterface dialog, int which) {
         
        // 今の選択されているモノ
        this.mSelectedItemIndex = which;
 
        // ファイルリストが空じゃない
        if (this.mFileList != null) {
             
            int selectedItemIndex = this.mSelectedItemIndex;    // 選択されている項目
             
            // 上の階層がある場合
            if (0 < this.mDirectorys.size()) {
                // 上の階層ボタン分減らす
                selectedItemIndex--;
            }
 
            // 上の階層へが選択されてた
            if (selectedItemIndex < 0) {
                 
                // 一つ上の階層へ移動する
                this.openDirectory(this.mDirectorys.pop());
             
            } else {
                 
                // ファイルを取り出す
                this.mLastSelectedItem = this.mFileList[selectedItemIndex];
                // ディレクトリの場合はそのディレクトリのモノを表示する
                if (this.mLastSelectedItem.isDirectory()) {
                     
                    // 次の階層に移動する前に、今の階層に戻れる様にスタックに積んでおく
                    this.mDirectorys.push(this.mCurrDirectory);
                     
                    // 次の階層で新しくダイアログを開く
                    this.openDirectory(this.mLastSelectedItem.getAbsolutePath());
                 
                // ファイルだった場合は、そのファイルを選択されたファイルとして登録する
                } else {
                    // ファイルが選択されたことを通知する
                    this.mListener.onFileSelected(this.mLastSelectedItem);
                }
            }
        }
    }
     
    /**
     * 指定のディレクトリを開く
     * @param dir 開きたいディレクトリ(このディレクトリがルートディレクトリになる)
     */
    public void openDirectory(String dir) {
        try {
            // ディレクトリだけ取り出したい
            if (this.mOpenDirectory == true) {
                 
                // ディレクトリだけ取り出す(フィルタ使う)
                this.mFileList = new File(dir).listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        // ディレクトリだけ許可
                        if (pathname.isDirectory())
                            return true;
                        return false;
                    }
                });
                 
            } else {
                 
                // 指定のディレクトリのファイルを全部取り出す
                this.mFileList = new File(dir).listFiles();
            }
             
            // 今の階層を取っておく
            this.mCurrDirectory = dir;
             
            // 何もとれなかった(開けない階層、多分アクセス権限がない)
            if (this.mFileList == null) {
                // 一つ上の階層へ移動する
                this.openDirectory(this.mDirectorys.pop());
                return ;
            }
 
            // 何も残ってない(ディレクトリが確定)
            if (this.mFileList.length <= 0) {
                this.mListener.onFileSelected(this.mLastSelectedItem);
                return ;
            }
             
            // Alertダイアログのために配列を用意する
            String[] fileNameList = null;
            int itemCount = 0;
             
            // ルートディレクトリ以外
            if (0 < this.mDirectorys.size()) {
                // 上の階層へ行くための項目を追加する
                fileNameList = new String[this.mFileList.length + 1];
                fileNameList[itemCount] = "<上の階層>";
                itemCount++;
                 
            // ルートディレクトリ
            } else {
                // ファイルの数だけ
                fileNameList = new String[this.mFileList.length];
            }
             
            // 見つかったファイルの分だけ追加する
            for (File currFile : this.mFileList) {
                 
                // ディレクトリだった
                if (currFile.isDirectory()) {
                    // 最後に/を加えてディレクトリの表示を
                    fileNameList[itemCount] = currFile.getName() + "/";
                     
                // ファイルだった
                } else {
                    fileNameList[itemCount] = currFile.getName();
                }
                itemCount++;
            }
             
            // ダイアログを表示する
            new AlertDialog.Builder(this.mParent)
                .setTitle(dir)
                .setItems(fileNameList, this)
                .show();
             
        } catch (SecurityException se) {
            Log.e("SecurityException", se.getMessage());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }
 
}