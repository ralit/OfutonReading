package org.ralit.ofutonreading;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

/*
 *  横長の画像のばあい、上下に大きな余白ができてしまう
 */

interface LayoutFinishedListener {
	void onPageViewLayoutFinished();
}

public class PageView extends ScrollView{

	private FrameLayout mFrameLayout;
	private LinkedList<ImageView> mPageViewList = new LinkedList<ImageView>();
	private LinkedList<ImageView> mMarkerViewList = new LinkedList<ImageView>();
	private Bitmap mPageBitmap;
	private Bitmap mScaledPageBitmap;
	private float mRH;
	private float mRW;
	private BookManager mBook;
	private float pageW;
	private float pageH;
	private Timer waitForRecognizeTimer;
	private Handler handler = new Handler();
	private Context context;
	private Bitmap markerBitmap;
	private Canvas markerCanvas;

	public void layout() {
		
	}
	
	public PageView(Context context, BookManager bookManager, float w, float h, Bitmap _bmp) {
		super(context);
		this.context = context;
		mBook = bookManager;
		mRW = w;
		mRH = h;
		mPageBitmap = _bmp;

		mFrameLayout = new FrameLayout(context);
		ImageView pageView = new ImageView(context);
		mPageViewList.add(pageView);
		ImageView markerView = new ImageView(context);
		mMarkerViewList.add(markerView);

		pageW = (float) mPageBitmap.getWidth();
		pageH = (float) mPageBitmap.getHeight();
		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), true);
		mPageViewList.getFirst().setImageBitmap(mScaledPageBitmap);
		mFrameLayout.addView(mPageViewList.getFirst());
		mFrameLayout.addView(mMarkerViewList.getFirst());
		
		markerBitmap = Bitmap.createBitmap((int)mRW, (int)(mRW * (pageH/pageW)), Bitmap.Config.ARGB_8888);
		markerCanvas = new Canvas(markerBitmap);
		
		addView(mFrameLayout);

		final int count = getChildCount();
		Fun.log("PageView.getChildCount: " + count);
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = (int)mRW;
			params.height = (int)(mRW * (pageH/pageW));
			view.setLayoutParams(params);
		}

//		gestureDetector = new GestureDetector(context, new YScrollDetector());
		
		if(!mBook.isRecognized()) {
			Fun.log("mBook.isRecognized() == false");
			waitForRecognizeTimer = new Timer();
			waitForRecognizeTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(mBook.isRecognized()) { // 1ページ目はこれでいいかもしれないけど、…いや、ページが変わるときにmRecognizedをfalseにさせればいいのか…
						waitForRecognizeTimer.cancel();
						handler.post(new Runnable() {
							@Override
							public void run() {
								scrollToCurrentLine();
							}
						});
					}
				}
			}, 0, 1000);
		} else {
			scrollToCurrentLine();
		}
	}

	public void setImage(Bitmap bmp) {
		ImageView pageView = new ImageView(context);
		mPageViewList.add(pageView);
		ImageView markerView = new ImageView(context);
		mMarkerViewList.add(markerView);
		
		mPageBitmap = bmp;
		
		pageW = (float) mPageBitmap.getWidth();
		pageH = (float) mPageBitmap.getHeight();
		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), true);
		mPageViewList.getLast().setImageBitmap(mScaledPageBitmap);
		
		markerBitmap = Bitmap.createBitmap((int)mRW, (int)(mRW * (pageH/pageW)), Bitmap.Config.ARGB_8888);
		markerCanvas = new Canvas(markerBitmap);

		mFrameLayout.addView(mPageViewList.getLast());
		mFrameLayout.addView(mMarkerViewList.getLast());
		mFrameLayout.removeView(mPageViewList.pollFirst());
		mFrameLayout.removeView(mMarkerViewList.pollFirst());

		final int count = getChildCount();
		Fun.log("PageView.getChildCount: " + count);
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = (int)mRW;
			params.height = (int)(mRW * (pageH/pageW));
			view.setLayoutParams(params);
		}
		
		if(!mBook.isRecognized()) {
			Fun.log("mBook.isRecognized() == false");
			waitForRecognizeTimer = new Timer();
			waitForRecognizeTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(mBook.isRecognized()) { // 1ページ目はこれでいいかもしれないけど、…いや、ページが変わるときにmRecognizedをfalseにさせればいいのか…
						waitForRecognizeTimer.cancel();
						handler.post(new Runnable() {
							@Override
							public void run() {
								scrollToCurrentLine();
							}
						});
					}
				}
			}, 0, 1000);
		} else {
			scrollToCurrentLine();
		}
	}

	public void scrollToCurrentLine() {
		Word word = mBook.getPageLayout().get(mBook.getCurLine());
		final int scroll = (int)(((word.getTop() + word.getBottom()) / 2) * (mRW/pageW) - (mRH / 4));
		smoothScrollTo(0, scroll);
	}

	public Bitmap getImage() {
		return mPageBitmap;
	}
	
	public void mark(MotionEvent ev1, MotionEvent ev2, Point screen) {
		
//		Word word = mBook.getPageLayout().get(mBook.getCurLine());
//		float linemid = (word.getBottom() + word.getTop()) / 2;
		float realx1 = ev1.getX() * (pageW / mRW);
//		float realy1 = linemid + (pageW / mRW) * (ev1.getY() - (1f/2f) * mRH  - (screen.y - mRH));
		float realy1 = (pageW / mRW) * (ev1.getY() - (mRH/2) - (screen.y - mRH) + getScrollY());
		float realx2 = ev2.getX() * (pageW / mRW);
//		float realy2 = linemid + (pageW / mRW) * (ev2.getY() - (1f/2f) * mRH  - (screen.y - mRH));
		float realy2 = (pageW / mRW) * (ev2.getY() - (mRH/2) - (screen.y - mRH) + getScrollY());
		Fun.log("(" + realx1 + ", " + realy1 + ") → (" + realx2 + ", " + realy2 + ")");
		Paint marker = new Paint();
		marker.setStyle(Style.FILL_AND_STROKE);
		marker.setColor(Color.YELLOW);
		marker.setStrokeWidth(1);
		marker.setAlpha(64);

		ArrayList<Word> layout = mBook.getPageLayout();
		Rect rect = null;
		for (int i = 1; i < layout.size() - 1; i++) {
			Word prev = layout.get(i-1);
			Word now = layout.get(i);
			Word next = layout.get(i+1);
			int prevMid = (prev.getBottom() + prev.getTop()) / 2;
			int nowMid = (now.getBottom() + now.getTop()) / 2;
			int nextMid = (next.getBottom() + next.getTop()) / 2;
			int mid1 = (nowMid + prevMid) / 2;
			int mid2 = (nextMid + nowMid) / 2;
			if(realy1 <= mid1) {
				rect = new Rect((int)realx1, prev.getTop(), (int)realx2, prev.getBottom());
				break;
			} else if(realy1 <= mid2) {
				rect = new Rect((int)realx1, now.getTop(), (int)realx2, now.getBottom());
				break;
			} else {
				rect = new Rect((int)realx1, next.getTop(), (int)realx2, next.getBottom());
			}
		}
		
		if ( rect != null ) {
			rect.set((int)(rect.left * (mRW/pageW)), (int)(rect.top * (mRW/pageW)), (int)(rect.right * (mRW/pageW)), (int)(rect.bottom * (mRW/pageW)));
			
			
			markerCanvas.drawRect(rect, marker);
			mMarkerViewList.getFirst().setImageBitmap(markerBitmap);
//			Bitmap markerBitmap = Bitmap.createBitmap(mPageBitmap.getWidth(), mPageBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//			Canvas markerCanvas = new Canvas(markerBitmap);
//			markerCanvas.drawRect(rect, marker);
//			Bitmap markedPage = Bitmap.createScaledBitmap(markerBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), false);
//			mMarkerViewList.getFirst().setImageBitmap(markedPage);
//			saveMarkedImage(Bitmap.createBitmap(bmp, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top), "test", rect);
		}
	}

}

