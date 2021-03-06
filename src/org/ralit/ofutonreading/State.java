package org.ralit.ofutonreading;

import android.view.MotionEvent;

public interface State {

	public abstract void onChangePage(Con con, int page);
	
	public abstract void onChangeLine(Con con, int line);
	
	public abstract void onChangeSpeed(Con con, boolean up);
	
	public abstract void onMark(Con con, MotionEvent ev1, MotionEvent ev2);
	
	public abstract void onRotate(Con con);
	
	public abstract void onOpenMarkerList(Con con);
	
	public abstract void onBack(Con con);
	
	public abstract void onSleep(Con con);
	
	public abstract void onHome(Con con);
	
	public abstract void onPause(Con con);
	
	public abstract void onResume(Con con);

}
