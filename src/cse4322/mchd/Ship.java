package cse4322.mchd;

import android.graphics.Canvas;


public interface Ship {
	
	public boolean getCollided (Explosion e);
	
	public Ordinance getOrdinance();
	
	public boolean readyToFire();
	
	public void update();
	
	public void draw(Canvas canvas);

}
