//package org.ralit.ofutonreading;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.os.Handler;
//import android.view.View;
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
//interface LayoutFinishedListener {
//	void onPageViewLayoutFinished();
//}
//
//public class PageView_20131217_1805 extends FrameLayout{
//
//	private ImageView mPageView;
//	private ImageView mMarkerView;
//	private Bitmap mPageBitmap;
//	private Bitmap mScaledPageBitmap;
//	private float mRH;
//	private float mRW;
//	private LayoutFinishedListener layoutFinishedListener;
//	private boolean isFirstLayout = true;
//	private BookManager mBook;
//	private float pageW;
//	private float pageH;
//	private Timer waitForRecognizeTimer;
//	private Handler handler = new Handler();
//
//	public PageView_20131217_1805(Context context, BookManager bookManager, LayoutFinishedListener _layoutFinishedListener) {
//		super(context);
//		layoutFinishedListener = _layoutFinishedListener;
//		mBook = bookManager;
//
//		mPageView = new ImageView(context);
//		mMarkerView = new ImageView(context);
//		addView(mPageView);
//		addView(mMarkerView);
//		setBackgroundColor(Color.GREEN);
//		mPageView.setBackgroundColor(Color.BLACK);
//		//		mPageView.setImageResource(R.drawable.usagi);
//
//	}
//
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		super.onSizeChanged(w, h, oldw, oldh);
//		mRH = h;
//		mRW = w;
//		{
//			android.view.ViewGroup.LayoutParams params = getLayoutParams();
//			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//			setLayoutParams(params);
//		}
//		if(0 < pageH && 0 < pageW) {
//			Fun.log("PageViewに画像をセットした後のonSizeChenged");
//			android.view.ViewGroup.LayoutParams params = getLayoutParams();
//			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//			params.height = (int)(mRW * (pageH/pageW));
//			setLayoutParams(params);
//		}
//		final int count  = getChildCount();
//		for (int i = 0; i < count; i++) {
//			View view = getChildAt(i);
//			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
//			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//			params.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//			view.setLayoutParams(params);
//		}
//		if(isFirstLayout) {
//			layoutFinishedListener.onPageViewLayoutFinished();
//			isFirstLayout = false;
//		}
//	}
//
//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		super.onLayout(changed, l, t, r, b);
//		// TODO Auto-generated method stub
//		//		final int count = getChildCount();
//		//		final int left = getLeft();
//		//		final int top = getTop();
//		//		final int right = getRight();
//		//		final int bottom = getBottom();
//		//		for (int i = 0; i < count; i++) {
//		//			View view = getChildAt(i);
//		//			if (view.getVisibility() != View.GONE) {
//		//				view.layout(left, top, right, bottom);
//		//			}
//		//		}
//		//		invalidate();
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		//		final int count = getChildCount();
//		//		for(int i = 0; i < count; i++) {
//		//			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
//		//		}
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	}
//
//	public void setImage(Bitmap _bmp) {
//		mPageBitmap = _bmp;
//		pageW = (float) mPageBitmap.getWidth();
//		Fun.log("pageW:"+pageW);
//		pageH = (float) mPageBitmap.getHeight();
//		Fun.log("pageH:"+pageH);
//		final float ratio = mRH / pageH;
//		Fun.log("ratio:"+ratio);
//		final float small_w = pageW * ratio;
//		Fun.log("small_w:"+small_w);
//		final float scale_ratio = mRW / small_w;
//		Fun.log("scale_ratio:"+scale_ratio);
//		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), false);
//		Fun.log("(mRW * (pageH/pageW)):"+(mRW * (pageH/pageW)));
//		mPageView.setImageBitmap(mScaledPageBitmap);
//		// マーカーの処理
//		//			markedPage = Bitmap.createScaledBitmap(markerBitmap, (int)dW, (int)(dW * (h/w)), false);
//		//			markerview.setImageBitmap(markedPage);
//		mPageView.setScaleX(scale_ratio);
//		mPageView.setScaleY(scale_ratio);
//		setScaleX(scale_ratio);
//		setScaleY(scale_ratio);
//		{
//			android.view.ViewGroup.LayoutParams params = getLayoutParams();
//			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
//			params.height = (int)(mRW * (pageH/pageW));
//			setLayoutParams(params);
//		}
//
//
//		if(!mBook.isRecognized()) {
//			Fun.log("mBook.isRecognized() == false");
//			waitForRecognizeTimer = new Timer();
//			waitForRecognizeTimer.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					if(mBook.isRecognized()) {
//						handler.post(new Runnable() {
//							@Override
//							public void run() {
//								waitForRecognizeTimer.cancel();
//								onFinishRecognize();
//							}
//						});
//					}
//				}
//			}, 0, 1000);
//		} else {
//			onFinishRecognize();
//		}
//	}
//
//	public void onFinishRecognize() {
//		float linemid = (mBook.getPageLayout().get(mBook.getCurLine()).getBottom() + mBook.getPageLayout().get(mBook.getCurLine()).getTop()) / 2;
//		Fun.log("linemid:"+linemid);
//		float distance = pageH / 2 - linemid;
//		Fun.log("distance:"+distance);
//		float i = distance * (mRW / pageW);
//		Fun.log("i:"+i);
//		setY(i);
//		AnimatorSet set = new AnimatorSet();
//		ObjectAnimator anim3 = ObjectAnimator.ofFloat(this, "y", i);
//		set.playTogether(anim3);
//		set.setDuration(500);
//		set.start();
//	}
//
//	public Bitmap getImage() {
//		return mPageBitmap;
//	}
//}
