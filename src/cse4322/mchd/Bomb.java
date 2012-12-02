package cse4322.mchd;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bomb implements Ordnance {
	private static final int POINTS_WORTH = 3;
	private static final int DAMAGE = 20;
	private static final double SPEED = 2;
	
	private Bitmap bitmap;
	private double x, y, angle, dx, dy;

	@Override
	public boolean hasCollided(Explosion e) 
	{
		if(Math.sqrt(Math.pow(this.x - e.getX(), 2) + Math.pow(this.y - e.getY(), 2)) <= e.getSize())
			return true;
		else
			return false;
	}

	@Override
	public int getDamage() 
	{
		return DAMAGE;
	}

	@Override
	public int getPointsWorth() 
	{
		return POINTS_WORTH;
	}

	@Override
	public double getX() 
	{
		return x;
	}

	@Override
	public double getY() 
	{
		return y;
	}

	@Override
	public void update() 
	{
		x += dx;
		y += dy;
	}

	@Override
	public void draw(Canvas canvas) 
	{
		//the angle we need to rotate the canvas by so that the bomb looks like it's actually flying towards its target
		float rotationAngle = (90 - (float)Math.toDegrees(angle));
		//angle the canvas
		canvas.rotate(rotationAngle, (float)x, (float)y);
		//draw the missile
		canvas.drawBitmap(bitmap, (int)x - (bitmap.getWidth()/2), (int)y, null);
		//set canvas angle back to normal
		canvas.rotate(-rotationAngle, (float)x, (float)y);
	}

	public Bomb(Bitmap bitmap, int x, int y, int destX, int destY)
	{
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		angle = (double) Math.atan2(y - destY, destX - x);
		
		this.dx = (SPEED * Math.cos(angle));
		this.dy =  0 - (SPEED * Math.sin(angle));
	}
}
