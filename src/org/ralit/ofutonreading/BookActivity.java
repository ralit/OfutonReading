package org.ralit.ofutonreading;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * ScrollViewの子にはminimumHeightを設定しないと大きさが0になるっぽい
 * http://somethinglikemusic.seesaa.net/article/303331255.html
 */

public class BookActivity extends Activity implements AnimatorListener{

	private Context mContext;
	private BookManager mBook;
	// レイアウトとビュー
	private LinearLayout mLinearLayout;
	private FrameLayout mTickerFrame;
	private ScrollView mScrollView;
	private ImageView mTicker1;
	private ImageView mTicker2;
	private FrameLayout mPageFrame;
	private ImageView mPageView;
	private ImageView mMarkerView;
	// その他
	private AnimatorSet mAnimation;
	private float mTextZoom;
	private int mTickerWidth;
	private int mTickerHeight;
	private ImageView mAnimatingTicker; 
	private Bitmap mPageBitmap;
	private Bitmap mScaledPageBitmap;
	private int mDuration;
	private int mLineW;
	private int mLineH;
	private float mRW;
	private float mRH;
	private boolean mPending = false;

	public BookActivity(Context context, BookManager manager) {
		super(context);
		mContext = context;
		mBook = manager;
		initialize();
//		setImage(mBook.getBitmap(mBook.getCurPage()));
		
	}

	//	public BookView(Context context, AttributeSet attrs) {
	//		super(context, attrs);
	//		mContext = context;
	//		initialize();
	//	}

	private void initialize() {
		Fun.log("BookView()");
		// 2画面の基本となる一番下の枠を作る
		mLinearLayout = new LinearLayout(mContext);
		//		mLinearLayout.setBackgroundColor(Color.BLACK);
		mTickerFrame = new FrameLayout(mContext);
		mScrollView = new ScrollView(mContext);
		mLinearLayout.addView(mTickerFrame);
		mLinearLayout.addView(mScrollView);
		//		mTickerFrame.setBackgroundColor(Color.BLUE);
		//		mScrollView.setBackgroundColor(Color.CYAN);
		// 上画面の電光掲示板を作る
		mTicker1 = new ImageView(mContext);
		mTicker2 = new ImageView(mContext);
		mTickerFrame.addView(mTicker1);
		mTickerFrame.addView(mTicker2);
				mTicker1.setBackgroundColor(Color.DKGRAY);
		//		mTicker2.setBackgroundColor(Color.GRAY);
		// 下画面にFrameLayoutを入れる(ページとマーカーを重ねるため)
		mPageFrame = new FrameLayout(mContext);
		mScrollView.addView(mPageFrame);
		mScrollView.setSmoothScrollingEnabled(true);
		//		mPageFrame.setBackgroundColor(Color.GREEN);
		// 下画面を作る
		mPageView = new ImageView(mContext);
		mMarkerView = new ImageView(mContext);
		mPageFrame.addView(mPageView);
		mPageFrame.addView(mMarkerView);
				mPageView.setBackgroundColor(Color.LTGRAY);
		//		mMarkerView.setBackgroundColor(Color.MAGENTA);
		//		// スプラッシュ表示のため
		//		mLinearLayout.setAlpha(0f);
		//		// ここで初めて描画
		addView(mLinearLayout);
		setBackgroundColor(Color.WHITE);
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Fun.log("onSizeChanged");
		super.onSizeChanged(w, h, oldw, oldh);
		final int count  = getChildCount();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			LayoutParams params = view.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
			view.setLayoutParams(params);
		}
		Fun.log(String.valueOf(w));
		Fun.log(String.valueOf(h));
		{
			LayoutParams params = mTickerFrame.getLayoutParams();
			params.width = w;
			params.height = h / 2;
			mTickerFrame.setLayoutParams(params);
		}
		{
			LayoutParams params = mScrollView.getLayoutParams();
			params.width = w;
			params.height = h / 2;
			mScrollView.setLayoutParams(params);
			mPageFrame.setMinimumHeight(h / 2);
		}
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
	}


	//	@Override
	//	public void addView(View view) {
	//		super.addView(view);
	//		int currentScreen = -1;
	//		final int index = indexOfChild(view);
	//		if (index > currentScreen) {
	//			if (currentScreen > 0) {
	//				view.setVisibility(View.GONE);//★非表示
	//			}
	//			currentScreen = index;
	//			view.setVisibility(View.VISIBLE);//★表示
	//		}
	//	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		final int count = getChildCount();
		final int left = getLeft();
		final int top = getTop();
		final int right = getRight();
		final int bottom = getBottom();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (view.getVisibility() != View.GONE) {
				view.layout(left, top, right, bottom);
			}
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}


	public void setImage(Bitmap _bmp) {
		Fun.log("setImage()");
		mPageBitmap = _bmp;
		if(!mBook.isRecognized()) {
			Fun.log("mBook.isRecognized() == false");
			// レイアウト認識がまだだったらレイアウト認識を行う。
			// レイアウト認識中は全画面でページを表示してあげる。
			final float pageW = (float) mPageBitmap.getWidth();
			Fun.log("pageW:"+pageW);
			final float pageH = (float) mPageBitmap.getHeight();
			Fun.log("pageH:"+pageH);
			final float ratio = mRH / pageH;
			Fun.log("ratio:"+ratio);
			final float small_w = pageW * ratio;
			Fun.log("small_w:"+small_w);
			final float scale_ratio = mRW / small_w;
			Fun.log("scale_ratio:"+scale_ratio);
			mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), false);
			Fun.log("(mRW * (pageH/pageW)):"+(mRW * (pageH/pageW)));
			mPageView.setImageBitmap(mScaledPageBitmap);
			// マーカーの処理
			//			markedPage = Bitmap.createScaledBitmap(markerBitmap, (int)dW, (int)(dW * (h/w)), false);
			//			markerview.setImageBitmap(markedPage);
			mPageFrame.setScaleX(scale_ratio);
			mPageFrame.setScaleY(scale_ratio);

			// 認識終了を待つ
			CountDownTimer keyEventTimer = new CountDownTimer(20000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {
					Fun.log(String.valueOf(millisUntilFinished));
					if(mBook.isRecognized()) {
						float linemid = (mBook.getPageLayout().get(mBook.getCurLine()).getBottom() + mBook.getPageLayout().get(mBook.getCurLine()).getTop()) / 2;
						Fun.log("linemid:"+linemid);
						float distance = pageH / 2 - linemid;
						Fun.log("distance:"+distance);
						float i = distance * (mRW / pageW);
						Fun.log("i:"+i);
						mPageFrame.setY(i);
						AnimatorSet set = new AnimatorSet();
						ObjectAnimator anim1 = ObjectAnimator.ofFloat(mTickerFrame, "height", mRH / 2);
						ObjectAnimator anim2 = ObjectAnimator.ofFloat(mScrollView, "height", mRH / 2);
						ObjectAnimator anim3 = ObjectAnimator.ofFloat(mPageFrame, "y", i);
						set.playTogether(anim1, anim2, anim3);
						set.setDuration(500);
						set.start();
					}
				}
				@Override
				public void onFinish() {
					Fun.log("20秒待ったけど終わらなかった");
				}
			}.start();
		}
		if(mAnimatingTicker == mTicker1) { mAnimatingTicker = mTicker2; } 
		else { mAnimatingTicker = mTicker1; }
		mLineW = mBook.getPageLayout().get(mBook.getCurLine()).getRight() - mBook.getPageLayout().get(mBook.getCurLine()).getLeft();
		Fun.log("mLineW:"+mLineW);
		mLineH = mBook.getPageLayout().get(mBook.getCurLine()).getBottom() - mBook.getPageLayout().get(mBook.getCurLine()).getTop();
		Fun.log("mLineH:"+mLineH);
		mAnimatingTicker.setImageBitmap(Bitmap.createBitmap(mPageBitmap, mBook.getPageLayout().get(mBook.getCurLine()).getLeft(), mBook.getPageLayout().get(mBook.getCurLine()).getTop(), mLineW, mLineH));
		mTextZoom = ((float)mRH / 2f) / ((float)mLineH * ((float)mRW / (float)mLineW));
		Fun.log("mTextZoom:"+mTextZoom);
		mAnimatingTicker.setScaleX(mTextZoom);
		mAnimatingTicker.setScaleY(mTextZoom);
		mTickerWidth = (int) (mRW * ((float)mLineW/(float)mLineH)); // 修正
		mTickerHeight = (int) (mRH / 2); // 修正
		Fun.log("mTickerWidth: "+mTickerWidth);
		Fun.log("mTickerHeight: "+mTickerHeight);
		mAnimatingTicker.setX(mTickerWidth);
		mAnimatingTicker.setY(0);
		// アニメーション開始
//		animation(0);
	}
}

