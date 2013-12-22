package org.ralit.ofutonreading;

public class StateNormal implements State{

	private static StateNormal singleton = new StateNormal();
	
	private StateNormal() {}
	
	public static State getInstance() { return singleton; }

	@Override
	public void onChangePage(Con con, int page) {
		con.changePage();
		
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
	public void onMark(Con con) {
		// TODO Auto-generated method stub
		
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
