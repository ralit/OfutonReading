package org.ralit.ofutonreading;

public class StatePause implements State{

	private static StatePause singleton = new StatePause();
	
	private StatePause() {}
	
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
