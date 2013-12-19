//package org.ralit.ofutonreading;
//
//import java.util.LinkedList;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.animation.Animator;
//import android.animation.Animator.AnimatorListener;
//import android.animation.ObjectAnimator;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Handler;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.LinearInterpolator;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//
//
//
///**
// * ViewGroupの作成は以下のページが参考になりました。
// * http://ga29.blog.fc2.com/blog-entry-7.html
// */
//
//interface LineEndListener {
//	void onLineEnd();
//}
//
//public class TickerView_20131217_2030 extends FrameLayout implements AnimatorListener{
//
////	private ImageView mTicker1;
////	private ImageView mTicker2;
//	private LinkedList<ImageView> mTickerList = new LinkedList<ImageView>();
//	private LinkedList<ObjectAnimator> mAnimatorList = new LinkedList<ObjectAnimator>();
//	private Context context;
//	private BookManager mBook;
//	private float mRH;
//	private float mRW;
//	private int mTickerWidth;
//	private int mTickerHeight;
//	private long mDuration;
//	private long animationDelay = 0;
//	private long loss = 0;
//	private Timer animationTimer;
//	private Handler handler = new Handler();
//	private ObjectAnimator move;
//	private Bitmap bmp;
//
//	public TickerView_20131217_2030(Context context, BookManager bookManager, LineEndListener _lineEndListener) {
//		super(context);
//		this.context = context;
//		mBook = bookManager;
////		mTicker1 = new ImageView(context);
////		mTicker2 = new ImageView(context);
////		addView(mTicker1);
////		addView(mTicker2);
////		mTicker1.setBackgroundColor(Color.DKGRAY);
//	}
//
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
//
//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		super.onLayout(changed, l, t, r, b);
//		// TODO Auto-generated method stub
//		final int count = getChildCount();
//		final int left = getLeft();
//		final int top = getTop();
//		final int right = getRight();
//		final int bottom = getBottom();
//		for (int i = 0; i < count; i++) {
//			View view = getChildAt(i);
//			if (view.getVisibility() != View.GONE) {
//				view.layout(left, top, right, bottom);
//			}
//		}
//		invalidate();
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		final int count = getChildCount();
//		for(int i = 0; i < count; i++) {
//			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
//		}
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	}
//	
//	public void setImage(Bitmap _bmp) {
//		if (_bmp != null) {
//			bmp = _bmp;
//		}
//		
//		ImageView ticker = new ImageView(context);
//		mTickerList.add(ticker);
//
//		int mLineW = mBook.getPageLayout().get(mBook.getCurLine()).getRight() - mBook.getPageLayout().get(mBook.getCurLine()).getLeft();
//		Fun.log("mLineW:"+mLineW);
//		int mLineH = mBook.getPageLayout().get(mBook.getCurLine()).getBottom() - mBook.getPageLayout().get(mBook.getCurLine()).getTop();
//		Fun.log("mLineH:"+mLineH);
//		ticker.setImageBitmap(Bitmap.createBitmap(bmp, mBook.getPageLayout().get(mBook.getCurLine()).getLeft(), mBook.getPageLayout().get(mBook.getCurLine()).getTop(), mLineW, mLineH));
//		float mTextZoom = ((float)mRH / 2f) / ((float)mLineH * ((float)mRW / (float)mLineW));
//		Fun.log("mTextZoom:"+mTextZoom);
//		ticker.setScaleX(mTextZoom);
//		ticker.setScaleY(mTextZoom);
//		mTickerHeight = (int) (mRH / 2); // 修正
//		mTickerWidth = (int) (mLineW * ((float)mTickerHeight/(float)mLineH)); // 修正
////		mTickerWidth = (int) (mRW * ((float)mLineW/(float)mLineH)); // 修正
//		Fun.log("mTickerWidth: "+mTickerWidth);
//		Fun.log("mTickerHeight: "+mTickerHeight);
//		ticker.setX(mTickerWidth/2);
//		ticker.setY(0);
//		// アニメーション開始
////		animation();
//		ViewGroup parent = (ViewGroup)mTickerList.getFirst().getParent(); 
//		if ( parent != null ) {
//		    parent.removeView(mTickerList.getFirst());
//		}
//		addView(mTickerList.getFirst());
//		animation();
//	}
//	
//	
//	public void animation() {
//		mDuration = 530;
//
//		ObjectAnimator move = ObjectAnimator.ofFloat(mTickerList.pollFirst(), "x", mTickerWidth/2, -mTickerWidth/2);
//		mAnimatorList.add(move);
//		if (mTickerWidth > mTickerHeight) { 
//			mDuration *= ((float)mTickerWidth / (float)mTickerHeight);
//		} else { 
//			mDuration *= ((float)mTickerHeight / (float)mTickerWidth);
//		}
////		if (imageview.getWidth() > imageview.getHeight()) { 
////			mDuration *= ((float)imageview.getWidth() / (float)imageview.getHeight());
////		} else { 
////			mDuration *= ((float)imageview.getHeight() / (float)imageview.getWidth());
////		}
//		Fun.log("mDuration:"+mDuration);
//		
//		move.setDuration(mDuration);
//		move.addListener(this);
//		move.setInterpolator(new LinearInterpolator());
//		mBook.setCurLine(mBook.getCurLine() + 1);
//		mAnimatorList.pollFirst().start();
////		move.start();
////		animationTimer = new Timer();
////		if (loss != 0) {
////			loss = System.currentTimeMillis() - loss;
////		}
////		animationTimer.schedule(new TimerTask() {
////			@Override
////			public void run() {
////				handler.post(new Runnable() {
////					@Override
////					public void run() {
////						mBook.setCurLine(mBook.getCurLine() + 1);
////						mAnimatorList.pollFirst().start();
////
////					}
////				});
////			}
////		}, animationDelay);
//	}
//	
//	
//
//
//	@Override
//	public void onAnimationCancel(Animator animation) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onAnimationEnd(Animator animation) {
//		setImage(null);
//	}
//
//	@Override
//	public void onAnimationRepeat(Animator animation) {
//		// TODO Auto-generated method stub
//
//	}
//
//	//	@Override
//	//	public void onAnimationStart(Animator animation) {
//	//		// TODO Auto-generated method stub
//	////		if (mAnimationFlag == AnimationFlag.loop) {
//	//			if(0 < animation.getStartDelay()) { mPending = true; } 
//	//			Fun.log("startdelay: " + (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));
//	//			animation((long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth)));	
//	////		}
//	//	}
//
//	@Override
//	public void onAnimationStart(Animator animation) {
////		animationDelay = (long)(mDuration * ((float)(mTickerWidth - mRW) / (float)mTickerWidth));
////		loss = System.currentTimeMillis();
////		Fun.log("onAnimationStart > animationDelay: " + animationDelay);
////		setImage(null);
//	}
//}
