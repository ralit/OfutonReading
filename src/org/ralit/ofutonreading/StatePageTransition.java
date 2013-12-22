package org.ralit.ofutonreading;

public class StatePageTransition implements State{

	private static StatePageTransition singleton = new StatePageTransition();
	
	private StatePageTransition() {}
	
	public static State getInstance() { return singleton; }

	@Override
	public void onChangePage(int page) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangeLine(int line) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangeSpeed(boolean up) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMark() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRotate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOpenMarkerList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSleep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHome() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

}
