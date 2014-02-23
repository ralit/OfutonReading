package org.ralit.ofutonreading;

import android.view.MotionEvent;

public class StateRecognize implements State{

	private static StateRecognize singleton = new StateRecognize();
	
	private StateRecognize() {}
	
	public static State getInstance() { return singleton; }

	@Override
	public void onChangePage(Con con, int page) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangeLine(Con con, int line) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangeSpeed(Con con, boolean up) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMark(Con con, MotionEvent ev1, MotionEvent ev2) {
		
	}

	@Override
	public void onRotate(Con con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOpenMarkerList(Con con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBack(Con con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSleep(Con con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHome(Con con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause(Con con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume(Con con) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "StateRecognize";
	}

}
