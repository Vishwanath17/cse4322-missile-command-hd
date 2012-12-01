package cse4322.mchd;

import android.graphics.Canvas;

public interface Ordinance {
	
	public boolean hasCollided(Explosion e);
	
	public boolean hasCollided(City c);
	
	public int getDamage();
	
	public int getPointWorth();
	
	public void update();
	
	public void draw(Canvas canvas);

}
