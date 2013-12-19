package org.ralit.ofutonreading;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

public class LayeredImageScrollView extends ScrollView {

	public LayeredImageScrollView(Context context, int color) {
		super(context);
		FrameLayout frameLayout = new FrameLayout(context);
		ImageView imageView1 = new ImageView(context);
		ImageView imageView2 = new ImageView(context);
		addView(frameLayout);
		frameLayout.addView(imageView1);
		frameLayout.addView(imageView2);
		setBackgroundColor(color);
	}


}