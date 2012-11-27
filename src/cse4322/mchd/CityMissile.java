package cse4322.mchd;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CityMissile {
	
	private double x, y, destX, destY;
	private double speed;
	private Bitmap missileBitmap, targetBitmap;
	private double angle;
	private boolean detonated = false;
	
	public boolean hasDetonated()
	{
		return detonated;
	}
	public void update()
	{
		x = x + (speed * Math.cos(angle));
		y = y - (speed * Math.sin(angle));
		
		if((Math.sqrt(Math.pow(destX - x, 2) + Math.pow(destY - y, 2))) < 20)
				detonated = true;
	}
	
	public void draw(Canvas canvas)
	{
		canvas.drawBitmap(missileBitmap, (int)x, (int)y, null);
		canvas.drawBitmap(targetBitmap, (int)destX, (int)destY, null);
	}
	
	public CityMissile(Bitmap missileBitmap, Bitmap targetBitmap, int x, int y, int destX, int destY)
	{
		this.missileBitmap = missileBitmap;
		this.targetBitmap = targetBitmap;
		this.x = x;
		this.y = y;
		this.destX = destX;
		this.destY = destY;
		this.speed = 5;
		angle = (double) Math.atan2(y - destY, destX - x);
	}

}
