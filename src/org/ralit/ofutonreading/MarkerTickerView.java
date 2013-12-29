package org.ralit.ofutonreading;

import java.util.LinkedList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

interface MarkerEndListener {
	void onMarkerEnd();
}

public class MarkerTickerView extends FrameLayout implements AnimatorListener {

	private LinkedList<ImageView> mTickerList = new LinkedList<ImageView>();
	private LinkedList<ObjectAnimator> mAnimatorList = new LinkedList<ObjectAnimator>();
	private Context context;
	private float mRH;
	private float mRW;
	private int mTickerWidth;
	private int mTickerHeight;
	private long mDuration;
	private Handler handler = new Handler();
	private Bitmap bmp;
	private MarkerEndListener markerEndListener;
	private static final long durationBase = 530;

	public MarkerTickerView(Context context, MarkerEndListener markerEndListener, float w, float h) {
		super(context);
		this.context = context;
		this.markerEndListener = markerEndListener;
		mRW = w;
		mRH = h;
		setBackgroundColor(Color.DKGRAY);
	}

	
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
	
	
	public void setImage(Bitmap bmp) {
		
		ImageView ticker = new ImageView(context);
		mTickerList.add(ticker);

		ticker.setImageBitmap(bmp);
		
		int mLineW = bmp.getWidth();
		int mLineH = bmp.getHeight();
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
		mDuration = durationBase;

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
		mAnimatorList.getFirst().start();

	}

	@Override
	public void onAnimationCancel(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animator animation) {
		mAnimatorList.pollFirst();
		ObjectAnimator finish = ObjectAnimator.ofFloat(mTickerList.pollFirst(), "x", -mTickerWidth/2 + mRW/2, -mTickerWidth/2 - mRW/2);
		finish.setDuration((long)(durationBase * ((2 * mRW)/mRH)));
		finish.setInterpolator(new DecelerateInterpolator());
		finish.start();
		markerEndListener.onMarkerEnd();
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animator animation) {

	}

//	public void nextLine() {
//		mAnimatorList.getFirst().end();
//	}
//
//	public void previousLine() {
//		if(mBook.getCurLine() == 0) {
//			Toast.makeText(context, "最初の行です", Toast.LENGTH_SHORT).show();
//			return;
//		}
//		mBook.setCurLine(mBook.getCurLine() - 2);
//		mAnimatorList.getFirst().end();
//	}
}
