package org.ralit.ofutonreading;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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

public class BookView extends ViewGroup implements AnimatorListener{

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

	public BookView(Context context) {
		super(context);
		mContext = context;
		initialize();
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
//		mTicker1.setBackgroundColor(Color.DKGRAY);
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
//		mPageView.setBackgroundColor(Color.LTGRAY);
//		mMarkerView.setBackgroundColor(Color.MAGENTA);
//		// スプラッシュ表示のため
//		mLinearLayout.setAlpha(0f);
//		// ここで初めて描画
		addView(mLinearLayout);
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
}

