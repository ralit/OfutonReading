package org.ralit.ofutonreading;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class CardListView extends ViewGroup{

	Context mContext;
	ListView mListView;
	View view;

	public CardListView(Context context) {
		super(context);
		Fun.log("CardListView()");
		mContext = context;
		initialize();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int slide = 100;
//		LayoutParams params = view.getLayoutParams();
//		params.width = w - slide;
//		params.height = h - slide;
//		view.setLayoutParams(params);
////		view.setX(slide);
////		view.setY(slide);
//		LayoutParams params = mListView.getLayoutParams();
//		params.width = w - slide;
//		params.height = h - slide;
//		mListView.setLayoutParams(params);
		LayoutParams params = getLayoutParams();
		params.width = w;
		params.height = h/2;
		setLayoutParams(params);
	}

	private void initialize() {
		Fun.log("CardListView.initialize()");
//		FrameLayout frame = new FrameLayout(mContext);
//		addView(frame);
//		view = new View(mContext);
//		view.setBackgroundColor(Color.rgb(200, 200, 200));
		mListView = new ListView(mContext);
		mListView.setBackgroundColor(Color.WHITE);
		addView(mListView);
//		frame.addView(view);
//		frame.addView(mListView);
//		String[] test = {"111", "fhwuei", "iowceow", "jifoewjf", "111", "fhwuei", "iowceow", "jifoewjf", "111", "fhwuei", "iowceow", "jifoewjf" };
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, test);
//		mListView.setAdapter(adapter);
	}
	
	public void setAdapter(ArrayAdapter<?> adapter) {
		mListView.setAdapter(adapter);
	}

	//	@Override
	//	public void addView(View view) {
	//		super.addView(view);
	//		int currentScreen = -1;
	//		final int index = indexOfChild(view);
	//		if (index > currentScreen) {
	//			if (currentScreen > 0) {
	//				view.setVisibility(View.GONE);//★非表示
	//			}
	//			currentScreen = index;
	//			view.setVisibility(View.VISIBLE);//★表示
	//		}
	//	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		final int count = getChildCount();
		final int left = getLeft();
		final int top = getTop();
		final int right = getRight();
		final int bottom = getBottom();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (view.getVisibility() != View.GONE) {
				view.layout(left, top, right, bottom);
			}
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
