package org.ralit.ofutonreading;

import android.view.MotionEvent;

public class StateNormal implements State{

	private static StateNormal singleton = new StateNormal();
	
	private StateNormal() {}
	
	public static State getInstance() { return singleton; }

	@Override
	public void onChangePage(Con con, int page) {
		con.changePage(page);
		
	}

	@Override
	public void onChangeLine(Con con, int line) {
		con.changeLine(line);
		
	}

	@Override
	public void onChangeSpeed(Con con, boolean up) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMark(Con con, MotionEvent ev1, MotionEvent ev2) {
		con.mark(ev1, ev2);
		
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

}
