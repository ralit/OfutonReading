package org.ralit.ofutonreading;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;

/*
 * http://utage.headwaters.co.jp/blog/?p=840
 */

interface AnimatorCallbackListener {
	void animatorCallBack(String message, SimpleAnimator animator);
}

public class SimpleAnimator {

	private Activity activity;
	private ObjectAnimator animator;
	
	public SimpleAnimator(Context context) {
		activity = (Activity)context;
	}
	
	public void start(final String message, long duration, Object target, String propertyName, float... values) {
		animator = ObjectAnimator.ofFloat(target, propertyName, values);
		animator.setDuration(duration);
		animator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {}
			
			@Override
			public void onAnimationRepeat(Animator animation) {}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				((AnimatorCallbackListener)activity).animatorCallBack(message, SimpleAnimator.this);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {}
		});
		animator.start();
	}
	
	public void cancel() {
		animator.cancel();
	}
	
	public void end() {
		animator.end();
	}

}
