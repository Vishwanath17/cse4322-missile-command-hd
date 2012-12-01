package cse4322.mchd;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class CityMissile {
	
	private double x, y, destX, destY;
	private double speed;
	private Bitmap missileBitmap, targetBitmap;
	private double angle;
	private boolean detonated = false;
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public boolean hasDetonated()
	{
		return detonated;
	}
	
	public boolean hasCollided(Explosion e)
	{
		if(Math.sqrt(Math.pow(this.x - e.getX(), 2) + Math.pow(this.y - e.getY(), 2)) <= e.getSize())
			return true;
		else
			return false;
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
		//the angle we need to rotate the canvas by so that the missile looks like it's actually flying towards its target
		float rotationAngle = (90 - (float)Math.toDegrees(angle));
		//angle the canvas
		canvas.rotate(rotationAngle, (float)x, (float)y);
		//draw the missile
		canvas.drawBitmap(missileBitmap, (int)x - (missileBitmap.getWidth()/2), (int)y - (missileBitmap.getHeight()/2), null);
		//set canvas angle back to normal
		canvas.rotate(-rotationAngle, (float)x, (float)y);
		//draw the target
		canvas.drawBitmap(targetBitmap, (int)destX - (targetBitmap.getWidth()/2), (int)destY - (missileBitmap.getHeight()/2), null);
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
