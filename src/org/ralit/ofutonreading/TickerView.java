package org.ralit.ofutonreading;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;



/**
 * ViewGroupの作成は以下のページが参考になりました。
 * http://ga29.blog.fc2.com/blog-entry-7.html
 */

interface LineEndListener {
	void onLineEnd();
}

public class TickerView extends FrameLayout{

	private ImageView mTicker1;
	private ImageView mTicker2;
	private LinkedList<ImageView> mTickerList = new LinkedList<ImageView>();
	private Context context;
	private BookManager mBook;
	private float mRH;
	private float mRW;
	private int mTickerWidth;
	private int mTickerHeight;
	private long mDuration;

	public TickerView(Context context, BookManager bookManager, LineEndListener _lineEndListener) {
		super(context);
		this.context = context;
		mBook = bookManager;
		mTicker1 = new ImageView(context);
		mTicker2 = new ImageView(context);
		addView(mTicker1);
		addView(mTicker2);
		mTicker1.setBackgroundColor(Color.DKGRAY);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mRH = h;
		mRW = w;
		final int count  = getChildCount();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			view.setLayoutParams(params);
		}
	}

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
	
	public void setimage(Bitmap bmp) {
		ImageView ticker = new ImageView(context);
		mTickerList.add(ticker);
		ticker.
		int mLineW = mBook.getPageLayout().get(mBook.getCurLine()).getRight() - mBook.getPageLayout().get(mBook.getCurLine()).getLeft();
		Fun.log("mLineW:"+mLineW);
		int mLineH = mBook.getPageLayout().get(mBook.getCurLine()).getBottom() - mBook.getPageLayout().get(mBook.getCurLine()).getTop();
		Fun.log("mLineH:"+mLineH);
		ticker.setImageBitmap(Bitmap.createBitmap(bmp, mBook.getPageLayout().get(mBook.getCurLine()).getLeft(), mBook.getPageLayout().get(mBook.getCurLine()).getTop(), mLineW, mLineH));
		float mTextZoom = ((float)mRH / 2f) / ((float)mLineH * ((float)mRW / (float)mLineW));
		Fun.log("mTextZoom:"+mTextZoom);
		ticker.setScaleX(mTextZoom);
		ticker.setScaleY(mTextZoom);
		int mTickerWidth = (int) (mRW * ((float)mLineW/(float)mLineH)); // 修正
		int mTickerHeight = (int) (mRH / 2); // 修正
		Fun.log("mTickerWidth: "+mTickerWidth);
		Fun.log("mTickerHeight: "+mTickerHeight);
//		ticker.setX(mTickerWidth);
//		ticker.setY(0);
		// アニメーション開始
//		animation();
		addView(mTickerList.getFirst());
	}
	
	
	public void animation() {
		mDuration = 530;

		ObjectAnimator move = ObjectAnimator.ofFloat(mTickerList.pollFirst(), "x", mTickerWidth/2, -mTickerWidth/2);
		if (mTickerWidth > mTickerHeight) { 
			mDuration *= ((float)mTickerWidth / (float)mTickerHeight);
		} else { 
			mDuration *= ((float)mTickerHeight / (float)mTickerWidth);
		}
		Fun.log("mDuration:"+mDuration);
		
		move.setDuration(mDuration);
//		move.addListener(this);
		move.setInterpolator(new LinearInterpolator());
		move.start();
//		animationTimer = new Timer();
//		animationTimer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						move.start();
//						mBook.setCurLine(mBook.getCurLine() + 1);
//					}
//				});
//			}
//		}, animationDelay);
	}
}
