package org.ralit.ofutonreading;

public interface State {

	public abstract void onChangePage(int page);
	
	public abstract void onChangeLine(int line);
	
	public abstract void onChangeSpeed(boolean up);
	
	public abstract void onMark();
	
	public abstract void onRotate();
	
	public abstract void onOpenMarkerList();
	
	public abstract void onBack();
	
	public abstract void onSleep();
	
	public abstract void onHome();
	
	public abstract void onPause();
	
	public abstract void onResume();

}
