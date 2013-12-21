//package org.ralit.ofutonreading;
//
//import java.util.LinkedList;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Handler;
//import android.view.GestureDetector;
//import android.view.GestureDetector.SimpleOnGestureListener;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.ScrollView;
//
///*
// *  横長の画像のばあい、上下に大きな余白ができてしまう
// */
//
//interface LayoutFinishedListener {
//	void onPageViewLayoutFinished();
//}
//
//public class PageView_20131221_1642 extends ScrollView{
//
//	private FrameLayout mFrameLayout;
////	private ImageView mPageView;
////	private ImageView mMarkerView;
//	private LinkedList<ImageView> mPageViewList = new LinkedList<ImageView>();
//	private LinkedList<ImageView> mMarkerViewList = new LinkedList<ImageView>();
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
//	private Context context;
//	private GestureDetector gestureDetector;
//	private View.OnTouchListener gestureListener;
//
//	public PageView_20131221_1642(Context context, BookManager bookManager, LayoutFinishedListener _layoutFinishedListener, float w, float h, Bitmap _bmp) {
//		super(context);
//		this.context = context;
//		mBook = bookManager;
//		layoutFinishedListener = _layoutFinishedListener;
//		mRW = w;
//		mRH = h;
//		mPageBitmap = _bmp;
//
//		mFrameLayout = new FrameLayout(context);
//		ImageView pageView = new ImageView(context);
//		mPageViewList.add(pageView);
//		ImageView markerView = new ImageView(context);
//		mMarkerViewList.add(markerView);
////		mPageView = new ImageView(context);
////		mMarkerView = new ImageView(context);
//
//		pageW = (float) mPageBitmap.getWidth();
//		pageH = (float) mPageBitmap.getHeight();
//		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), true);
//		mPageViewList.getFirst().setImageBitmap(mScaledPageBitmap);
//		mFrameLayout.addView(mPageViewList.getFirst());
//		mFrameLayout.addView(mMarkerViewList.getFirst());
////		mPageView.setImageBitmap(mScaledPageBitmap);
////		mFrameLayout.addView(mPageView);
////		mFrameLayout.addView(mMarkerView);
//		addView(mFrameLayout);
//
//		final int count = getChildCount();
//		Fun.log("PageView.getChildCount: " + count);
//		for (int i = 0; i < count; i++) {
//			View view = getChildAt(i);
//			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
//			params.width = (int)mRW;
//			params.height = (int)(mRW * (pageH/pageW));
//			view.setLayoutParams(params);
//		}
//
////		gestureDetector = new GestureDetector(context, new YScrollDetector());
//		
//		if(!mBook.isRecognized()) {
//			Fun.log("mBook.isRecognized() == false");
//			waitForRecognizeTimer = new Timer();
//			waitForRecognizeTimer.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					if(mBook.isRecognized()) { // 1ページ目はこれでいいかもしれないけど、…いや、ページが変わるときにmRecognizedをfalseにさせればいいのか…
//						waitForRecognizeTimer.cancel();
//						handler.post(new Runnable() {
//							@Override
//							public void run() {
//								scrollToCurrentLine();
//							}
//						});
//					}
//				}
//			}, 0, 1000);
//		} else {
//			scrollToCurrentLine();
//		}
//	}
//
//	public void setImage(Bitmap bmp) {
//		ImageView pageView = new ImageView(context);
//		mPageViewList.add(pageView);
//		ImageView markerView = new ImageView(context);
//		mMarkerViewList.add(markerView);
//		
//		mPageBitmap = bmp;
//		
//		pageW = (float) mPageBitmap.getWidth();
//		pageH = (float) mPageBitmap.getHeight();
//		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), true);
//		mPageViewList.getLast().setImageBitmap(mScaledPageBitmap);
//		handler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		mFrameLayout.addView(mPageViewList.getLast());
//		mFrameLayout.addView(mMarkerViewList.getLast());
//		mFrameLayout.removeView(mPageViewList.pollFirst());
//		mFrameLayout.removeView(mMarkerViewList.pollFirst());
//
//		final int count = getChildCount();
//		Fun.log("PageView.getChildCount: " + count);
//		for (int i = 0; i < count; i++) {
//			View view = getChildAt(i);
//			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
//			params.width = (int)mRW;
//			params.height = (int)(mRW * (pageH/pageW));
//			view.setLayoutParams(params);
//		}
//		
//		if(!mBook.isRecognized()) {
//			Fun.log("mBook.isRecognized() == false");
//			waitForRecognizeTimer = new Timer();
//			waitForRecognizeTimer.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					if(mBook.isRecognized()) { // 1ページ目はこれでいいかもしれないけど、…いや、ページが変わるときにmRecognizedをfalseにさせればいいのか…
//						waitForRecognizeTimer.cancel();
//						handler.post(new Runnable() {
//							@Override
//							public void run() {
//								scrollToCurrentLine();
//							}
//						});
//					}
//				}
//			}, 0, 1000);
//		} else {
//			scrollToCurrentLine();
//		}
//	}
//	
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		super.onSizeChanged(w, h, oldw, oldh);
//		if(isFirstLayout) {
//			layoutFinishedListener.onPageViewLayoutFinished();
//			isFirstLayout = false;
//		}
//	}
//
//	public void scrollToCurrentLine() {
//		//		float linemid = (mBook.getPageLayout().get(mBook.getCurLine()).getBottom() + mBook.getPageLayout().get(mBook.getCurLine()).getTop()) / 2;
//		//		Fun.log("linemid:"+linemid);
//		//		float distance = pageH / 2 - linemid;
//		//		Fun.log("distance:"+distance);
//		//		final float i = distance * (mRW / pageW);
//		//		Fun.log("i:"+i);
//		Word word = mBook.getPageLayout().get(mBook.getCurLine());
//		//		final int scroll = (word.getTop() + word.getBottom()) / 2 + (int)mRH / 4;
////		final int scroll = ((word.getTop() + word.getBottom()) / 2) * (int)(mRW * (pageH/pageW));
//		final int scroll = (int)(((word.getTop() + word.getBottom()) / 2) * (mRW/pageW) - (mRH / 4));
//		Fun.log("Scroll位置");
//		Fun.log(scroll);
//		/*
//		 * frameLayoutの位置を直接動かしてはいけない！
//		 * ScrollViewをScrollToさせるんだ！
//		 */
////		Thread thread = new Thread(new Runnable() {
////			@Override
////			public void run() {
////				handler.post(new Runnable() {
////					@Override
////					public void run() {
////						// TODO Auto-generated method stub
////						smoothScrollTo(0, scroll);
////					}
////				});
////			}
////		});
////		thread.start();
//		
////		handler.post(new Runnable() {
////			@Override
////			public void run() {
////				// TODO Auto-generated method stub
////				smoothScrollTo(0, scroll);
////			}
////		});
//		
//		smoothScrollTo(0, scroll);
//
//		//		AnimatorSet set = new AnimatorSet();
//		//		ObjectAnimator anim3 = ObjectAnimator.ofFloat(mFrameLayout, "y", i);
//		//		set.playTogether(anim3);
//		//		set.setDuration(500);
//		//		set.start();
//	}
//
//	public Bitmap getImage() {
//		return mPageBitmap;
//	}
//
//
////	@Override
////	public boolean onTouchEvent(MotionEvent ev) {
////		return super.onTouchEvent(ev);
////	}
////
////	@Override
////	public boolean onInterceptTouchEvent(MotionEvent ev) {
////		Fun.log("onInterceptTouchEvent");
////		boolean result = super.onInterceptTouchEvent(ev);
////		if (gestureDetector.onTouchEvent(ev)) {
////			Fun.log(result);
////			return result;
////		} else {
////			Fun.log("else->false");
////			return false;
////		}
////	}
////	
////	class YScrollDetector extends SimpleOnGestureListener {
////		@Override
////		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
////			Fun.log("onScroll");
////			try {
////				if (Math.abs(distanceX) > Math.abs(distanceY)) {
////					return true;
////				} else {
////					return false;
////				}
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
////			return false;
////		}
////	}
////	
//}
//
