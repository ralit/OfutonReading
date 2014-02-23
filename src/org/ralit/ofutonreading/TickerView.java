package org.ralit.ofutonreading;

import java.util.LinkedList;

import android.animation.Animator;
import android.animation.TimeInterpolator;
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
import android.widget.Toast;

/**
 * ViewGroupの作成は以下のページが参考になりました。
 * http://ga29.blog.fc2.com/blog-entry-7.html
 */

public class TickerView extends FrameLayout implements AnimatorListener {

	private LinkedList<ImageView> mTickerList = new LinkedList<ImageView>();
	private LinkedList<ObjectAnimator> mAnimatorList = new LinkedList<ObjectAnimator>();
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
//	private static final long durationBase = 530;
	private static final long durationBase = 550;
//	private long durationMultiplied = 530;
	private long durationMultiplied = 550;
//	private float marginRatio = 0.4f;

	public TickerView(Context context, BookManager bookManager, LineEndListener _lineEndListener, float w, float h) {
		super(context);
		this.context = context;
		mBook = bookManager;
		lineEndListener = _lineEndListener;
		mRW = w;
		mRH = h;
		setBackgroundColor(Color.DKGRAY);
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

	public void destroy() {
		if( mAnimatorList.size() > 0 ) {
			for (ObjectAnimator anim : mAnimatorList) {
				anim.removeAllListeners();
				anim.cancel();
			}
		}
		removeAllViews();
		mTickerList = null;
		mAnimatorList = null;
		mTickerList = new LinkedList<ImageView>();
		mAnimatorList = new LinkedList<ObjectAnimator>();
	}

	public void setImage(Bitmap _bmp, float marginRatio) {
		if (_bmp != null) {
			bmp = _bmp;
		}

		ImageView ticker = new ImageView(context);
		mTickerList.add(ticker);

		Word layout = mBook.getPageLayout().get(mBook.getCurLine());
		// 速度調節
		if (layout.getHeightRatio() > 0) {
			durationMultiplied = (long)(durationBase * layout.getHeightRatio());
		} else {
			durationMultiplied = (long)(durationBase * 1.4);
		}
		// 速度調節
		int mLineW = layout.getRight() - layout.getLeft();
		int mLineH = layout.getBottom() - layout.getTop();

		int margin = (int)((layout.getBottom() - layout.getTop()) * marginRatio);
		Bitmap scaledBitmap = Bitmap.createBitmap(
				bmp,
				Math.max(layout.getLeft() - margin, 0),
				Math.max(layout.getTop() - margin, 0),
				Math.min(mLineW + 2*margin, bmp.getWidth() - Math.max(layout.getLeft() - margin, 0) - 1),
				Math.min(mLineH + 2*margin, bmp.getHeight() - Math.max(layout.getTop() - margin, 0) - 1));
		ticker.setImageBitmap(scaledBitmap);

		mLineW = scaledBitmap.getWidth();
		mLineH = scaledBitmap.getHeight();
		float mTextZoom = ((float)mRH / 2f) / ((float)mLineH * ((float)mRW / (float)mLineW));
		ticker.setScaleX(mTextZoom);
		ticker.setScaleY(mTextZoom);
		mTickerHeight = (int) (mRH / 2); // 修正
		mTickerWidth = (int) (mLineW * ((float)mTickerHeight/(float)mLineH)); // 修正
		ticker.setX(mTickerWidth/2 + mRW/2);

		ViewGroup parent = (ViewGroup)mTickerList.getFirst().getParent(); 
		if ( parent != null ) {
			parent.removeView(mTickerList.getFirst());
		}

		handler.post(new Runnable() {
			@Override
			public void run() {
				addView(mTickerList.getFirst());
				animation();
			}
		});
	}


	public void animation() {
		mDuration = durationMultiplied;

		ObjectAnimator move = ObjectAnimator.ofFloat(mTickerList.getFirst(), "x", -mTickerWidth/2 + mRW/2);
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
		Fun.log("onAnimationEnd");
		mAnimatorList.pollFirst();
		ObjectAnimator finish = ObjectAnimator.ofFloat(mTickerList.pollFirst(), "x", -mTickerWidth/2 + mRW/2, -mTickerWidth/2 - mRW/2);
		finish.setDuration((long)(durationMultiplied * ((2 * mRW)/mRH)));
		//		finish.setInterpolator(new LinearInterpolator());
		finish.start();
		if (mBook.getPageLayout().size() - 1 < mBook.getCurLine() + 1) {
			lineEndListener.onPageEnd();
		} else {
			mBook.setCurLine(mBook.getCurLine() + 1);
			setImage(null, mBook.getMarginRatio());
			lineEndListener.onLineEnd();
		}
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animator animation) {

	}

	public void nextLine() {
		mAnimatorList.getFirst().end();
	}

	public void previousLine() {
		if(mBook.getCurLine() == 0) {
			Toast.makeText(context, context.getString(R.string.ofuton_first_line), Toast.LENGTH_SHORT).show();
			return;
		}
		mBook.setCurLine(mBook.getCurLine() - 2);
		mAnimatorList.getFirst().end();
	}

}
