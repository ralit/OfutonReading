package org.ralit.ofutonreading;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;

/*
 * http://utage.headwaters.co.jp/blog/?p=840
 */

interface TimerCallbackListener {
	void timerCallback(String message, SimpleTimer timer);
}

public class SimpleTimer {

	private Activity activity;
	private Timer timer;
	
	public SimpleTimer(Context context) {
		activity = (Activity)context;
	}
	
	public void start(long ms, final String message) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				((TimerCallbackListener)activity).timerCallback(message, SimpleTimer.this);
			}
		}, 0, ms);
	}
	
	public void cancel() {
		timer.cancel();
	}

}
