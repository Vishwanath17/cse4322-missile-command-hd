package cse4322.mchd;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class City {
	
	private static final int MAX_RELOAD_TIME = 5;
	private static final int MAX_HEALTH = 100;
	private static final int MAX_SHIELD_POWER = 30;
	private static final int INVULNERABLE_TIME = 50;
	
	private Bitmap bitmap;
	private Paint cityPaint, shieldPaint;
	private int x, y;
	private int reloadTimeLeft = 0;
	private int health = MAX_HEALTH;
	private int shieldPower = 0;
	private int afterDamageWait = 0;
	
	public boolean readyToFire()
	{
		if(reloadTimeLeft <= 0)
		{
			reloadTimeLeft = MAX_RELOAD_TIME;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getWidth()
	{
		return bitmap.getWidth();
	}
	
	public int getHeight()
	{
		return bitmap.getHeight();
	}
	
	public int getMidX()
	{
		return x + bitmap.getWidth()/2;
	}
	
	public int getMidY()
	{
		return y + bitmap.getHeight()/2;
	}
	
	public boolean wasHit(Ordnance o)
	{
		boolean hit = false;
		
		if(shieldPower > 0)
		{
			
		}
		else
		{
			if(o.getX() >= x && o.getX() <= x + getWidth() && o.getY() >= y && o.getY() <= y + getHeight())
			{
				damage(o.getDamage());
				hit = true;
			}
			else
				hit = false;
		}
		
		return hit;
	}
	
	public void powerShield(int power)
	{
		if(shieldPower < MAX_SHIELD_POWER)
		{
			shieldPower = Math.min(shieldPower + power, MAX_SHIELD_POWER);
		}
	}
	
	public void damage(int damage)
	{
		if(shieldPower > 0)
		{
			shieldPower = Math.max(shieldPower - damage, 0);
		}
		
		else if(afterDamageWait <= 0)
		{
			health = Math.max(health - damage, 0);
			afterDamageWait = MAX_RELOAD_TIME;
		}	
	}
	
	public void update()
	{
		if(reloadTimeLeft > 0)
			reloadTimeLeft--;
		if(afterDamageWait > 0)
			afterDamageWait--;
		//paint should alternate alpha while city invulnerable
	}
	
	public void draw(Canvas canvas)
	{
		canvas.drawBitmap(bitmap, x, y, cityPaint);
	}
	
	public City(Bitmap bitmap, int screenWidth, int screenHeight)
	{
		this.bitmap = bitmap;
		this.x = (screenWidth/2) - (this.bitmap.getWidth()/2);
		this.y = screenHeight - this.bitmap.getHeight();
		
		cityPaint = new Paint();
		shieldPaint = new Paint();
	}

}
