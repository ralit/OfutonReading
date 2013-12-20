package org.ralit.ofutonreading;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;


interface LayoutFinishedListener {
	void onPageViewLayoutFinished();
}

public class PageView extends ScrollView{

	private FrameLayout mFrameLayout;
	private ImageView mPageView;
	private ImageView mMarkerView;
	private Bitmap mPageBitmap;
	private Bitmap mScaledPageBitmap;
	private float mRH;
	private float mRW;
	private LayoutFinishedListener layoutFinishedListener;
	private boolean isFirstLayout = true;
	private BookManager mBook;
	private float pageW;
	private float pageH;
	private Timer waitForRecognizeTimer;
	private Handler handler = new Handler();
	private Context context;

	public PageView(Context context, BookManager bookManager, LayoutFinishedListener _layoutFinishedListener, float w, float h, Bitmap _bmp) {
		super(context);
		this.context = context;
		mBook = bookManager;
		layoutFinishedListener = _layoutFinishedListener;
		mRW = w;
		mRH = h;
		mPageBitmap = _bmp;

		mFrameLayout = new FrameLayout(context);
		mPageView = new ImageView(context);
		mMarkerView = new ImageView(context);

		pageW = (float) mPageBitmap.getWidth();
		pageH = (float) mPageBitmap.getHeight();
		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), true);
		mPageView.setImageBitmap(mScaledPageBitmap);
		mFrameLayout.addView(mPageView);
		mFrameLayout.addView(mMarkerView);
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

		if(!mBook.isRecognized()) {
			Fun.log("mBook.isRecognized() == false");
			waitForRecognizeTimer = new Timer();
			waitForRecognizeTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(mBook.isRecognized()) {
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

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(isFirstLayout) {
			layoutFinishedListener.onPageViewLayoutFinished();
			isFirstLayout = false;
		}
	}

	public void scrollToCurrentLine() {
//		float linemid = (mBook.getPageLayout().get(mBook.getCurLine()).getBottom() + mBook.getPageLayout().get(mBook.getCurLine()).getTop()) / 2;
//		Fun.log("linemid:"+linemid);
//		float distance = pageH / 2 - linemid;
//		Fun.log("distance:"+distance);
//		final float i = distance * (mRW / pageW);
//		Fun.log("i:"+i);
		Word word = mBook.getPageLayout().get(mBook.getCurLine());
//		final int scroll = (word.getTop() + word.getBottom()) / 2 + (int)mRH / 4;
		final int scroll = ((word.getTop() + word.getBottom()) / 2) * (int)(mRW * (pageH/pageW));
		Fun.log("Scroll位置");
		Fun.log(scroll);
		/*
		 * frameLayoutの位置を直接動かしてはいけない！
		 * ScrollViewをScrollToさせるんだ！
		 */
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						smoothScrollTo(0, scroll);
					}
				});
			}
		});
		thread.start();
		
//		AnimatorSet set = new AnimatorSet();
//		ObjectAnimator anim3 = ObjectAnimator.ofFloat(mFrameLayout, "y", i);
//		set.playTogether(anim3);
//		set.setDuration(500);
//		set.start();
	}

	public Bitmap getImage() {
		return mPageBitmap;
	}
}
