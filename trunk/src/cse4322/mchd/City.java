package cse4322.mchd;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class City {
	
	private static final int MAX_RELOAD_TIME = 100;
	private static final int MAX_HEALTH = 100;
	private static final int MAX_SHIELD_POWER = 30;
	
	private Bitmap bitmap;
	private Paint cityPaint, shieldPaint;
	private int x, y;
	private int reloadTimeLeft = 0;
	private int health = MAX_HEALTH;
	private int shieldPower = 0;
	private int shieldSize;
	private int afterDamageWait = 0;
	private int reloadBonusLevel = 0;
	private int missileSpeedBonusLevel = 0;
	
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
	
	public int getSpeedBonus()
	{
		return missileSpeedBonusLevel;
	}
	
	public int getReloadTimeLeft()
	{
		return reloadTimeLeft;
	}
	
	public int getMaxReloadTime()
	{
		return MAX_RELOAD_TIME;
	}
	
	public int getShieldPower()
	{
		return shieldPower;
	}
	
	public int getMaxShieldPower()
	{
		return MAX_SHIELD_POWER;
	}
	
	//check if the city was hit by enemy ordnance
	public boolean wasHit(Ordnance o)
	{
		boolean hit = false;
		
		//if shield has power, check if ordnance hit shield
		if(shieldPower > 0)
		{
			if(Math.sqrt(Math.pow(o.getX() - getMidX(), 2) + Math.pow(o.getY() - (y + getHeight()), 2)) <= shieldSize)
			{
				shieldPower -= o.getDamage();
				hit = true;
				updateShieldTransparency();
			}
		}
		
		//if no shield power, check it ordnance hit city
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
	
	//increase the reload bonus level to a maximum of 3
	public void increaseReloadBonusLevel()
	{
		if(reloadBonusLevel < 3)
			reloadBonusLevel++;
	}
	
	//increase the missile speed bonus level to a maximum of 3
	public void increaseMissileSpeedBonusLevel()
	{
		if(missileSpeedBonusLevel < 3)
			missileSpeedBonusLevel++;
	}
	
	//add power to the shield
	public void powerShield(int power)
	{
		if(shieldPower < MAX_SHIELD_POWER)
		{
			shieldPower = Math.min(shieldPower + power, MAX_SHIELD_POWER);
			updateShieldTransparency();
		}
	}
	
	
	//heal the city to a maximum defined by MAX_HEALTH
	public void heal(int healAmount)
	{
		health = Math.min(health + healAmount, MAX_HEALTH);
	}
	
	public void damage(int damage)
	{
		if(shieldPower > 0)
		{
			shieldPower = Math.max(shieldPower - damage, 0);
			updateShieldTransparency();
		}
		
		else if(afterDamageWait <= 0)
		{
			health = Math.max(health - damage, 0);
			afterDamageWait = MAX_RELOAD_TIME;
		}	
	}
	
	//update the transparency of the shield to reflect current power
	public void updateShieldTransparency()
	{
		shieldPaint.setAlpha((int)(((double)shieldPower/(double)MAX_SHIELD_POWER)*((double)255/(double)2)));
	}
	
	public void update()
	{
		if(reloadTimeLeft > 0)
			reloadTimeLeft -= (1 + reloadBonusLevel);
		if(afterDamageWait > 0)
		{
			afterDamageWait--;
			if(cityPaint.getAlpha() < 255)
				cityPaint.setAlpha(255);
			else
				cityPaint.setAlpha(255/2);
		}
		else
		{
			cityPaint.setAlpha(255);
		}
	}
	
	public void draw(Canvas canvas)
	{
		canvas.drawBitmap(bitmap, x, y, cityPaint);
		if(shieldPower > 0)
		{
			canvas.drawCircle(getMidX(), y + getHeight(), shieldSize, shieldPaint);
		}
	}
	
	public City(Bitmap bitmap, int screenWidth, int screenHeight)
	{
		this.bitmap = bitmap;
		this.x = (screenWidth/2) - (this.bitmap.getWidth()/2);
		this.y = screenHeight - this.bitmap.getHeight();
		this.shieldSize = (int)((double)this.bitmap.getWidth() * .75);
		
		cityPaint = new Paint();
		//make the shield cyan, and have 50% opacity to start with
		shieldPaint = new Paint();
		shieldPaint.setARGB(255/5, 0, 255, 255);
	}

}
