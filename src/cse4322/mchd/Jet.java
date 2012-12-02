package cse4322.mchd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Jet implements Ship 
{
	private static final double SPEED = 3;
	private static final int POINTS_WORTH = 5;
	private double x, y, dx;
	private Bitmap bitmap;

	@Override
	public boolean hasCollided(Explosion e) 
	{	
		//check if any corners of the jet's collision box have entered the explosion
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
	public Ordnance getOrdnance(Context context, int targetX, int targetY) 
	{
		return new Missile(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy_missile), (int)getX(), (int)getY(), targetX, targetY);
	}

	@Override
	public boolean readyToFire(int difficultyLevel) 
	{
		int randomFire = (int)(Math.random() * 100);
		
		if(randomFire < (25 + difficultyLevel * 5))
			return true;
		else
			return false;
	}

	@Override
	public void update(int width) 
	{
		x += dx;
		if(dx > 0 && x > width)
			x = 0 - bitmap.getWidth();
		else if(dx < 0 && (x + bitmap.getWidth() < 0))
			x = width;
	}

	@Override
	public void draw(Canvas canvas) 
	{
		canvas.drawBitmap(bitmap, (int)x, (int)y, null);
	}
	
	public Jet(Bitmap bitmap, int x, int y)
	{
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		
		if(this.x <= 0)
			dx = SPEED;
		else
			dx = -SPEED;
	}

	@Override
	public int getPointsWorth() 
	{
		return POINTS_WORTH;
	}

	@Override
	public double getX() 
	{
		return x + bitmap.getWidth()/2;
	}

	@Override
	public double getY() 
	{
		return y + bitmap.getHeight()/2;
	}

}
