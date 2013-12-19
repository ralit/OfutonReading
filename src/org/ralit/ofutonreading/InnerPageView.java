package org.ralit.ofutonreading;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;


public class InnerPageView extends FrameLayout{

	private ImageView mPageView;
	private ImageView mMarkerView;
//	private Bitmap mScaledPageBitmap;
	private Bitmap mPageBitmap;
	private float mRH;
	private float mRW;
	private Context mContext;
	private float pageW;
	private float pageH;
	
	public InnerPageView(Context context, Bitmap bmp, float w, float h) {
		super(context);
		mContext = context;
		mPageBitmap = bmp;
		mRW = w;
		mRH = h;
		
		mPageView = new ImageView(context);
		mMarkerView = new ImageView(context);
//		mPageView.setImageBitmap(bmp);
//		addView(mPageView);
//		addView(mMarkerView);
		
		pageW = (float) mPageBitmap.getWidth();
		pageH = (float) mPageBitmap.getHeight();
//		mScaledPageBitmap = Bitmap.createScaledBitmap(mPageBitmap, (int)mRW, (int)(mRW * (pageH/pageW)), true);
		mPageView.setImageBitmap(mPageBitmap);
		

		
		addView(mPageView);
		addView(mMarkerView);
//		final int count = getChildCount();
//		Fun.log("InnerPageView.getChildCount: " + count);
//		for (int i = 0; i < count; i++) {
//			View view = getChildAt(i);
//			android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
//			params.width = LayoutParams.MATCH_PARENT;
//			params.height = LayoutParams.MATCH_PARENT;
//			view.setLayoutParams(params);
//		}
//		mMarkerView.setBackgroundColor(Color.RED);
		setBackgroundColor(Color.GREEN);

		
	}
}
