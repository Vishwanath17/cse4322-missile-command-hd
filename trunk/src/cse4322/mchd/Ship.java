package cse4322.mchd;

import android.graphics.Canvas;


public interface Ship {
	
	public boolean hasCollided (Explosion e);
	
	public Ordnance getOrdnance();
	
	public boolean readyToFire();
	
	public int getPointsWorth();
	
	public float getX();
	
	public float getY();
	
	public void update(int width);
	
	public void draw(Canvas canvas);

}
