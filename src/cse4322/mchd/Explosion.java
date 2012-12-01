package cse4322.mchd;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Explosion 
{
	private final static int MIN_SIZE = 5;
	private final static int MAX_SIZE = 100;
	private final static int SIZE_DELTA = 1;
	private int x,y;
	private int size;
	
	private Paint paint;
	
	public void update()
	{
		if(size < MAX_SIZE)
			size += SIZE_DELTA;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public boolean hasExpired()
	{
		return size >= MAX_SIZE;
	}
	
	public void draw(Canvas canvas)
	{
		canvas.drawCircle(x, y, size, paint);
	}
	
	public Explosion(int x, int y)
	{
		this.x = x;
		this.y = y;
		this.size = MIN_SIZE;
		
		paint = new Paint();
		paint.setARGB(255, 255, 165, 0);
	}

}
