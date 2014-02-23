package org.ralit.ofutonreading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
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

public class PageView extends ScrollView implements TimerCallbackListener{

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
	private Handler handler = new Handler();
	private Context context;
	private Bitmap markerBitmap;
	private Canvas markerCanvas;

	
	public PageView(Context context, BookManager bookManager, float w, float h, Bitmap _bmp) {
		super(context);
		this.context = context;
		mBook = bookManager;
		mRW = w;
		mRH = h;
		mPageBitmap = _bmp;

		layout();
		layout2();

		mFrameLayout = new FrameLayout(context);		
		mFrameLayout.addView(mPageViewList.getFirst());
		mFrameLayout.addView(mMarkerViewList.getFirst());
		addView(mFrameLayout);
		setMark();
		waitForRecognize();
	}

	public void setImage(Bitmap bmp) {
		mPageBitmap = bmp;
		layout();
		mFrameLayout.addView(mPageViewList.getLast());
		mFrameLayout.addView(mMarkerViewList.getLast());
		mFrameLayout.removeView(mPageViewList.pollFirst());
		mFrameLayout.removeView(mMarkerViewList.pollFirst());
		layout2();
		setMark();
		waitForRecognize();
	}

	private void layout() {
		ImageView pageView = new ImageView(context);
		mPageViewList.add(pageView);
		ImageView markerView = new ImageView(context);
		mMarkerViewList.add(markerView);

		pageW = (float) mPageBitmap.getWidth();
		pageH = (float) mPageBitmap.getHeight();
		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), true);
		mPageViewList.getLast().setImageBitmap(mScaledPageBitmap);

		markerBitmap = Bitmap.createBitmap((int)mRW, (int)(mRW * (pageH/pageW)), Bitmap.Config.ARGB_8888);
		markerCanvas = new Canvas(markerBitmap);
	}

	private void layout2() {
		final int count = getChildCount();
		Fun.log("PageView.getChildCount: " + count);
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = (int)mRW;
			params.height = (int)(mRW * (pageH/pageW));
			view.setLayoutParams(params);
		}
	}

	private void waitForRecognize() { 
		if(!mBook.isRecognized()) {
			new SimpleTimer(context).start(1000, "scrollToCurrentLine");
		} else {
			scrollToCurrentLine();
		}
	}

	public void scrollToCurrentLine() {
		Word word = mBook.getPageLayout().get(mBook.getCurLine());
		final int scroll = (int)(((word.getTop() + word.getBottom()) / 2) * (mRW/pageW) - (mRH / 4));
		handler.post(new Runnable() {
			@Override
			public void run() {
				smoothScrollTo(0, scroll);
			}
		});
	}

	public Bitmap getImage() {
		return mPageBitmap;
	}

	public void mark(MotionEvent ev1, MotionEvent ev2, Point screen) {
		
		float realx1 = ev1.getX() * (pageW / mRW);
		float realy1 = (pageW / mRW) * (ev1.getY() - (mRH/2) - (screen.y - mRH) + getScrollY());
		float realx2 = ev2.getX() * (pageW / mRW);
		float realy2 = (pageW / mRW) * (ev2.getY() - (mRH/2) - (screen.y - mRH) + getScrollY());
		Fun.log("mark(disp): (" + ev1.getX() + ", " + ev1.getY() + ") → (" + ev2.getX() + ", " + ev2.getY() + ")");
		Fun.log("mark(real): (" + realx1 + ", " + realy1 + ") → (" + realx2 + ", " + realy2 + ")");
		Paint marker = new Paint();
		marker.setStyle(Style.FILL_AND_STROKE);
		marker.setColor(Color.YELLOW);
		marker.setStrokeWidth(1);
		marker.setAlpha(64);

		ArrayList<Word> layout = mBook.getPageLayout();
		Collections.sort(layout, new PointComparator());
		Rect rect = null;
		for (int i = 1; i < layout.size() - 1; i++) {
			Fun.log("mark: " + layout.get(i).getTop());
			Word prev = layout.get(i-1);
			Word now = layout.get(i);
			Word next = layout.get(i+1);
			int prevMid = (prev.getBottom() + prev.getTop()) / 2;
			int nowMid = (now.getBottom() + now.getTop()) / 2;
			int nextMid = (next.getBottom() + next.getTop()) / 2;
			int mid1 = (nowMid + prevMid) / 2;
			int mid2 = (nextMid + nowMid) / 2;
			if(prevMid <= realy1 && realy1 <= nextMid) {
				if(realy1 <= mid1) {
					rect = new Rect((int)realx1, prev.getTop(), (int)realx2, prev.getBottom());
					Fun.log("mark1");
					break;
				} else if(realy1 <= mid2) {
					rect = new Rect((int)realx1, now.getTop(), (int)realx2, now.getBottom());
					Fun.log("mark2");
					break;
				} else {
					rect = new Rect((int)realx1, next.getTop(), (int)realx2, next.getBottom());
					Fun.log("mark3");
					break;
				}
			}
		}

		if ( rect != null ) {
			Rect scaledRect = new Rect((int)(rect.left * (mRW/pageW)), (int)(rect.top * (mRW/pageW)), (int)(rect.right * (mRW/pageW)), (int)(rect.bottom * (mRW/pageW)));
//			rect.set((int)(rect.left * (mRW/pageW)), (int)(rect.top * (mRW/pageW)), (int)(rect.right * (mRW/pageW)), (int)(rect.bottom * (mRW/pageW)));
			markerCanvas.drawRect(scaledRect, marker);
			mMarkerViewList.getFirst().setImageBitmap(markerBitmap);
			mBook.saveMarkedImage(rect);
		}
	}
	
	private void setMark() {
		ArrayList<ArrayList<Integer>> pos = mBook.readMarkedPosition();
		Paint marker = new Paint();
		marker.setStyle(Style.FILL_AND_STROKE);
		marker.setColor(Color.YELLOW);
		marker.setStrokeWidth(1);
		marker.setAlpha(64);
		for (int index = 0; index < pos.size(); index++) {
			if(pos.get(index).get(0) == mBook.getCurPage()) {
				Fun.log("setmark: " + pos.get(index).get(1) + ", " + pos.get(index).get(2) + ", " + pos.get(index).get(3) + ", " + pos.get(index).get(4));
				Rect scaledRect = new Rect((int)(pos.get(index).get(1) * (mRW/pageW)), (int)(pos.get(index).get(2) * (mRW/pageW)), (int)(pos.get(index).get(3) * (mRW/pageW)), (int)(pos.get(index).get(4) * (mRW/pageW)));
				Fun.log("setmark: " + scaledRect.toString());
				markerCanvas.drawRect(scaledRect, marker);
			}
		}
		mMarkerViewList.getFirst().setImageBitmap(markerBitmap);
	}

	@Override
	public void timerCallback(String message, SimpleTimer timer) {
		if(message.equals("scrollToCurrentLine")) {
			if(mBook.isRecognized()) {
				timer.cancel();
				scrollToCurrentLine();
			}
		}
	}

}

