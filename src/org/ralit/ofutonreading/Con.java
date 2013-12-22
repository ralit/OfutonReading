package org.ralit.ofutonreading;

import android.view.MotionEvent;

public interface Con {

	public abstract void changeState(State state);
	
	public abstract void changePage(int page);
	
	public abstract void changeLine(int line);
	
	public abstract void mark(MotionEvent ev1, MotionEvent ev2);

}
