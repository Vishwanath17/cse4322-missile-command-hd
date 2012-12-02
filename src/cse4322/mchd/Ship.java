package cse4322.mchd;

import android.content.Context;
import android.graphics.Canvas;


public interface Ship {
	
	public boolean hasCollided (Explosion e);
	
	public Ordnance getOrdnance(Context context, int targetX, int targetY);
	
	public boolean readyToFire(int difficultyLevel);
	
	public int getPointsWorth();
	
	public double getX();
	
	public double getY();
	
	public void update(int width);
	
	public void draw(Canvas canvas);

}
