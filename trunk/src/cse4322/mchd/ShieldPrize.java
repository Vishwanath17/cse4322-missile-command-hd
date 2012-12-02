package cse4322.mchd;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ShieldPrize implements Prize {
	private static final int SHIELD_POWER = 15;
	private static final int POINTS_WORTH = 5;
	
	private Bitmap bitmap;
	private double x, y;

	@Override
	public boolean hasCollided(Explosion e) 
	{
		//check if any corners of the prize's collision box have entered the explosion
		if(Math.sqrt(Math.pow(x - e.getX(), 2) + Math.pow(y - e.getY(), 2)) <= e.getSize())
			return true;
		else if(Math.sqrt(Math.pow(x + bitmap.getWidth() - e.getX(), 2) + Math.pow(y - e.getY(), 2)) <= e.getSize())
			return true;
		else if(Math.sqrt(Math.pow(x - e.getX(), 2) + Math.pow(y + bitmap.getHeight() - e.getY(), 2)) <= e.getSize())
			return true;
		else if(Math.sqrt(Math.pow(x + bitmap.getWidth() - e.getX(), 2) + Math.pow(y + bitmap.getHeight()- e.getY(), 2)) <= e.getSize())
			return true;
		else
			return false;
	}

	@Override
	public int getPointsWorth() 
	{
		return POINTS_WORTH;
	}

	@Override
	public void applyBonus(City city) 
	{
		city.powerShield(SHIELD_POWER);
	}

	@Override
	public void update() 
	{

	}

	@Override
	public void draw(Canvas canvas) 
	{
		canvas.drawBitmap(bitmap, (int)x, (int)y, null);
	}
	
	public ShieldPrize(Bitmap bitmap, int x, int y)
	{
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
	}
}
