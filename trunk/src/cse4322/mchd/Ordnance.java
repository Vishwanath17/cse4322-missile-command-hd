package cse4322.mchd;

import android.graphics.Canvas;

public interface Ordnance {
	
	public boolean hasCollided(Explosion e);
	
	public int getDamage();
	
	public int getPointsWorth();
	
	public double getX();
	
	public double getY();
	
	public void update();
	
	public void draw(Canvas canvas);

}
