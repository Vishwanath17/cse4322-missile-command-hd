package cse4322.mchd;

import android.graphics.Canvas;

public interface Prize {
	
	public boolean hasCollided(Explosion e);
	
	public int getPointsWorth();
	
	public void applyBonus(City city);
	
	public void update();
	
	public void draw(Canvas canvas);

}
