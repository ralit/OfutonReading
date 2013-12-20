package org.ralit.ofutonreading;

import java.util.LinkedList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup;
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

public class TickerView extends FrameLayout implements AnimatorListener, OnGestureListener{

	private LinkedList<ImageView> mTickerList = new LinkedList<ImageView>();
//	private LinkedList<ObjectAnimator> mAnimatorList = new LinkedList<ObjectAnimator>();
	public LinkedList<ObjectAnimator> mAnimatorList = new LinkedList<ObjectAnimator>();
	private Context context;
	private BookManager mBook;
	private float mRH;
	private float mRW;
	private int mTickerWidth;
	private int mTickerHeight;
	private long mDuration;
	private Handler handler = new Handler();
	private Bitmap bmp;
	private LineEndListener lineEndListener;
	private GestureDetector gesture;

	public TickerView(Context context, BookManager bookManager, LineEndListener _lineEndListener, float w, float h) {
		super(context);
		this.context = context;
		mBook = bookManager;
		lineEndListener = _lineEndListener;
		mRW = w;
		mRH = h;
		setBackgroundColor(Color.DKGRAY);
		gesture = new GestureDetector(context, gestureListener);
//		gesture = new GestureDetector
	}

	//	@Override
	//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	//		super.onSizeChanged(w, h, oldw, oldh);
	//		mRH = h;
	//		mRW = w;
	//		final int count  = getChildCount();
	//		for (int i = 0; i < count; i++) {
	//			View view = getChildAt(i);
	//			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
	//			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
	//			params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
	//			view.setLayoutParams(params);
	//		}
	//	}

	public void setImage(Bitmap _bmp) {
		if (_bmp != null) {
			bmp = _bmp;
		}

		ImageView ticker = new ImageView(context);
		mTickerList.add(ticker);

		int mLineW = mBook.getPageLayout().get(mBook.getCurLine()).getRight() - mBook.getPageLayout().get(mBook.getCurLine()).getLeft();
		Fun.log("mLineW:"+mLineW);
		int mLineH = mBook.getPageLayout().get(mBook.getCurLine()).getBottom() - mBook.getPageLayout().get(mBook.getCurLine()).getTop();
		Fun.log("mLineH:"+mLineH);
		ticker.setImageBitmap(Bitmap.createBitmap(bmp, mBook.getPageLayout().get(mBook.getCurLine()).getLeft(), mBook.getPageLayout().get(mBook.getCurLine()).getTop(), mLineW, mLineH));
		float mTextZoom = ((float)mRH / 2f) / ((float)mLineH * ((float)mRW / (float)mLineW));
		Fun.log("mTextZoom:"+mTextZoom);
		ticker.setScaleX(mTextZoom);
		ticker.setScaleY(mTextZoom);
		mTickerHeight = (int) (mRH / 2); // 修正
		mTickerWidth = (int) (mLineW * ((float)mTickerHeight/(float)mLineH)); // 修正
		Fun.log("mTickerWidth: "+mTickerWidth);
		Fun.log("mTickerHeight: "+mTickerHeight);
		ticker.setX(mTickerWidth/2);
		ticker.setY(0);
		// アニメーション開始
		//		animation();
		ViewGroup parent = (ViewGroup)mTickerList.getFirst().getParent(); 
		if ( parent != null ) {
			parent.removeView(mTickerList.getFirst());
		}
		addView(mTickerList.getFirst());
		animation();
	}


	public void animation() {
		mDuration = 530;

		ObjectAnimator move = ObjectAnimator.ofFloat(mTickerList.pollFirst(), "x", mTickerWidth/2, -mTickerWidth/2);
		mAnimatorList.add(move);
		if (mTickerWidth > mTickerHeight) { 
			mDuration *= ((float)mTickerWidth / (float)mTickerHeight);
		} else { 
			mDuration *= ((float)mTickerHeight / (float)mTickerWidth);
		}
		Fun.log("mDuration:"+mDuration);

		move.setDuration(mDuration);
		move.addListener(this);
		move.setInterpolator(new LinearInterpolator());
		mBook.setCurLine(mBook.getCurLine());
		mAnimatorList.getFirst().start();
	}




	@Override
	public void onAnimationCancel(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animator animation) {
		mAnimatorList.pollFirst();
		mBook.setCurLine(mBook.getCurLine() + 1);
		setImage(null);
		lineEndListener.onLineEnd();

	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub

	}

	//	@Override
	//	public void onAnimationStart(Animator animation) {
	//		// TODO Auto-generated method stub
	////		if (mAnimationFlag == AnimationFlag.loop) {
	//			if(0 < animation.getStartDelay()) { mPending = true; } 
	//			Fun.log("startdelay: " + (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));
	//			animation((long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));	
	////		}
	//	}

	@Override
	public void onAnimationStart(Animator animation) {
		//		animationDelay = (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth));
		//		loss = System.currentTimeMillis();
		//		Fun.log("onAnimationStart > animationDelay: " + animationDelay);
		//		setImage(null);
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Fun.log("onTouchEvent");
		return gesture.onTouchEvent(ev);
	}

	private final SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		
		@Override
		public boolean onFling(MotionEvent ev1, MotionEvent ev2, float vx, float vy) {
			Fun.log("onFling");
			if (ev2.getX() - ev1.getX() > 120 && Math.abs(vx) > 200) {
				// 1行戻る
				//					if (set.getChildAnimations().get(0).isRunning()) { 
				//						if (index > 0) { --index; }
				//					}
				//					set.cancel();
			} else if (ev1.getX() - ev2.getX() > 120 && Math.abs(vx) > 200) {
				// 1行進む
				Fun.log("1行進む");
				mAnimatorList.getFirst().end();
			}
			return false;
		}
	};

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		Fun.log("ふつうのonFling");
		if (e1.getX() - e2.getX() > 120 && Math.abs(velocityX) > 200) {
			// 1行進む
			Fun.log("1行進む");
			mAnimatorList.getFirst().end();
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}



}
