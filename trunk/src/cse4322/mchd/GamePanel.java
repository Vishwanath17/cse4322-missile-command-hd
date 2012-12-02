package cse4322.mchd;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final String TAG = MainThread.class.getSimpleName();
	private static final long ENEMY_SPAWN_INTERVAL = 10000;
	private static final long PRIZE_SPAWN_INTERVAL = 30000;
	private static final long DIFFICULTY_INCREASE_INTERVAL = ENEMY_SPAWN_INTERVAL * 10;
	
	private MainThread thread;
	
	private int width, height;
	private int score = 0;
	
	private long timeLastEnemySpawn;
	private long timeLastPrizeSpawn;
	private long timeLastDifficultyIncrease;
	
	private int difficultyLevel = 1;
	
	private boolean cleanupNeeded = false;
	//private Ship ship;
	private City city;
	private ArrayList<CityMissile> cityMissiles;
	private ArrayList<CityMissile> queuedCityMissiles;
	private ArrayList<CityMissile> deadCityMissiles;
	
	private ArrayList<Explosion> explosions;
	private ArrayList<Explosion> deadExplosions;
	
	private ArrayList<Ship> ships;
	private ArrayList<Ship> deadShips;
	
	private ArrayList<Ordnance> ordnance;
	private ArrayList<Ordnance> deadOrdnance;
	
	private ArrayList<Prize> prizes;
	private ArrayList<Prize> deadPrizes;
	
	//the fps to be displayed
	private String avgFPS;
	private String scoreText;
	private String healthText;
	
	private Paint scorePaint;
	private Paint healthPaint;
	private Paint fpsPaint;

	public GamePanel(Context context)
	{
		super(context);
		
		//adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		scoreText = context.getString(R.string.score);
		healthText = context.getString(R.string.city_health);
		
		timeLastEnemySpawn = System.currentTimeMillis();
		timeLastPrizeSpawn = System.currentTimeMillis();
		timeLastDifficultyIncrease = System.currentTimeMillis();
		
		cityMissiles = new ArrayList<CityMissile>();
		queuedCityMissiles = new ArrayList<CityMissile>();
		deadCityMissiles = new ArrayList<CityMissile>();
		
		explosions = new ArrayList<Explosion>();
		deadExplosions = new ArrayList<Explosion>();
		
		ships = new ArrayList<Ship>();
		deadShips = new ArrayList<Ship>();
		
		ordnance = new ArrayList<Ordnance>();
		deadOrdnance = new ArrayList<Ordnance>();
		
		prizes = new ArrayList<Prize>();
		deadPrizes = new ArrayList<Prize>();
		
		scorePaint = new Paint();
		scorePaint.setARGB(255, 0, 255, 0);
		scorePaint.setTextSize(20);
		
		healthPaint = new Paint();
		healthPaint.setARGB(255, 0, 255, 0);
		healthPaint.setTextSize(20);
		
		fpsPaint = new Paint();
		fpsPaint.setARGB(255, 255, 255, 255);
		fpsPaint.setTextSize(20);
		
		
		//create the game loop thread
		thread = new MainThread(getHolder(), this);
		
		//make the GamePanel focusable so it can handle events
		setFocusable(true);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		//at this point the surface is created and we can safely start the game loop
		thread.setRunning(true);
		thread.start();
		
		//get width and height of screen, cannot be done in constructor
		width = this.getWidth();
		height = this.getHeight();
		
		//create city and load bitmap
		if(city == null)
			city = new City(BitmapFactory.decodeResource(getResources(), R.drawable.dallas), this.getWidth(), this.getHeight());
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.d(TAG, "Surface is being destroyed");
		//tell the thread to shut down and wait for it to finish
		//this is a clean shutdown
		boolean retry = true;
		while(retry)
		{
			try
			{
				thread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
				//try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{	
			if(event.getY() < height - city.getHeight())
			{
				if(city.readyToFire())
				{
					//adding a missile directly into the city missile list while updating/drawing could throw a ConcurrentModificationException, crashing the game.
					queuedCityMissiles.add(new CityMissile(BitmapFactory.decodeResource(getResources(), R.drawable.city_missile), BitmapFactory.decodeResource(getResources(), R.drawable.target), city.getMidX(), city.getMidY(), (int)event.getX(), (int)event.getY(), city.getSpeedBonus()));
				}
			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{

		}
		
		if(event.getAction() == MotionEvent.ACTION_UP)
		{

		}
		return true;
	}
	
	public void pause()
	{
		thread.setRunning(false);
	}
	
	public void resume()
	{
		thread.setRunning(true);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		render(canvas);
	}
	
	private void addScore(int points)
	{
		score += points;
	}
	
	//go through all sprite arraylists and cleanup "dead" sprites
	private void cleanupSprites()
	{
		cityMissiles.removeAll(deadCityMissiles);
		deadCityMissiles.clear();
		
		explosions.removeAll(deadExplosions);
		deadExplosions.clear();
		
		ships.removeAll(deadShips);
		deadShips.clear();
		
		ordnance.removeAll(deadOrdnance);
		deadOrdnance.clear();
		
		prizes.removeAll(deadPrizes);
		deadPrizes.clear();
	}
	
	//update the picture on the screen, order is important here
	protected void render(Canvas canvas)
	{
		if(canvas != null)
		{
			//fill the canvas with black
			canvas.drawColor(Color.BLACK);
			
			//draw the city
			if(city != null)
				city.draw(canvas);
			
			//draw all prizes
			for(Prize p : prizes)
			{
				p.draw(canvas);
			}
			
			//draw all enemy ordnance
			for(Ordnance o : ordnance)
			{
				o.draw(canvas);
			}
			
			//draw all enemy ships
			for(Ship s : ships)
			{
				s.draw(canvas);
			}

			//draw all explosions
			for(Explosion e : explosions)
			{
				e.draw(canvas);
			}

			//draw all city missiles
			for(CityMissile c : cityMissiles)
			{
				c.draw(canvas);
			}
			
			//display HUD
			displayHUD(canvas);
			//display fps
			displayFPS(canvas, avgFPS);
		}
	}
	
	//update game entities
	protected void update()
	{
		//don't call the sprite cleanup if we don't need to
		cleanupNeeded = false;
		
		//update the city
		if(city != null)
			city.update();

		//update explosions
		for(Explosion e : explosions)
		{
			e.update();
			//cleanup expired explosions
			if(e.hasExpired())
			{
				deadExplosions.add(e);
				cleanupNeeded = true;
			}
		}

		//add any queued up city missiles to active city missiles
		if(!queuedCityMissiles.isEmpty())
		{
			cityMissiles.addAll(queuedCityMissiles);
			queuedCityMissiles.clear();
		}
		
		//update active city missiles
		for(CityMissile c : cityMissiles)
		{
			c.update();
			//check if missile reached target
			if(!c.hasDetonated())
			{
				//check if missile has hit an explosion
				boolean hitExplosion = false;
				for(Explosion e : explosions)
					if(c.hasCollided(e))
						hitExplosion = true;
				if(hitExplosion)
				{
					//if so, kill the missile and spawn an explosion
					deadCityMissiles.add(c);
					explosions.add(new Explosion((int)c.getX(), (int)c.getY()));
					cleanupNeeded = true;
				}
			}
			else if(c.hasDetonated())
			{
				//if missile has detonated, kill the missile and spawn an explosion
				deadCityMissiles.add(c);
				explosions.add(new Explosion((int)c.getX(), (int)c.getY()));
				cleanupNeeded = true;
			}
		}
	
		//update all enemy ships
		for(Ship s : ships)
		{
			s.update(width);
			boolean spawnExplosion = false;
			for(Explosion e : explosions)
			{
				if(s.hasCollided(e) && !spawnExplosion)
				{
					spawnExplosion = true;
					addScore(s.getPointsWorth());
					deadShips.add(s);
					cleanupNeeded = true;
				}
			}
			//create an explosion where the ship was if it touched an explosion
			if(spawnExplosion)
				explosions.add(new Explosion((int)s.getX(), (int)s.getY()));
		}
		
		//update enemy ordnance
		for(Ordnance o : ordnance)
		{
			o.update();
			boolean spawnExplosion = false;
			for(Explosion e : explosions)
			{
				if(o.hasCollided(e) && !spawnExplosion)
				{
					spawnExplosion = true;
					addScore(o.getPointsWorth());
					deadOrdnance.add(o);
					cleanupNeeded = true;
				}
			}
			
			//Don't check for city collision if collided with explosion, favors player
			if(!spawnExplosion)
			{
				//checks if city was hit by ordnance, city handles any damage taken
				if(city.wasHit(o))
				{
					spawnExplosion = true;
					deadOrdnance.add(o);
					cleanupNeeded = true;
				}
			}
			
			//create an explosions where the ordnance was if it touched an explosion or city
			if(spawnExplosion)
				explosions.add(new Explosion((int)o.getX(), (int)o.getY()));
		}
		
		//update prizes
		for(Prize p : prizes)
		{
			p.update();
			boolean wasDestroyed = false;
			for(Explosion e : explosions)
			{
				if(p.hasCollided(e) && !wasDestroyed)
				{
					addScore(p.getPointsWorth());
					p.applyBonus(city);
					deadPrizes.add(p);
					cleanupNeeded = true;
				}
			}
		}
		
		if(cleanupNeeded)
			cleanupSprites();
		
		//update the difficulty level if it is less than 10, 10 is highest
		if(difficultyLevel < 10)
			updateDifficultyLevel();
		
		//attempt to spawn any new enemy entities or power-ups
		tryToSpawn();
	}
	
	public void setAvgFPS(String avgFPS)
	{
		this.avgFPS = avgFPS;
	}
	
	//draw the HUD on the screen (Score, Health, reload/shield power bars)
	private void displayHUD(Canvas canvas)
	{
		if(canvas != null && city != null)
		{
			canvas.drawText(scoreText + score, 30, 30, scorePaint);	
			
			//change color of health text based on city's current health
			if(city.getHealth() < 33)
			{
				healthPaint.setARGB(255, 255, 0, 0);
			}
			else if(city.getHealth() < 66)
			{
				healthPaint.setARGB(255, 255, 255, 0);
			}
			else
			{
				healthPaint.setARGB(255, 0, 255, 0);
			}
			
			canvas.drawText(healthText + city.getHealth(), 30, height - 50, healthPaint);
		}
	}
	
	//display the FPS on the screen
	private void displayFPS(Canvas canvas, String fps)
	{
		if(canvas != null & fps != null)
		{
			canvas.drawText(fps,  this.getWidth() - 100, 20, fpsPaint);
		}
	}
	
	//increase the difficulty level
	private void updateDifficultyLevel()
	{
		//increase difficulty level every X milliseconds, defined in DIFFICULTY_INCREASE_INTERVAL
		if((System.currentTimeMillis() - timeLastDifficultyIncrease) >= DIFFICULTY_INCREASE_INTERVAL)
		{
			//max difficulty level of 10
			difficultyLevel = Math.min(difficultyLevel++, 10);
			timeLastDifficultyIncrease = System.currentTimeMillis();
		}
	}
	
	//try to spawn jets, frigates, missiles, bombs, or power-ups
	private void tryToSpawn()
	{
		//only try to spawn enemy entities every X milliseconds, defined in ENEMY_SPAWN_INTERVAL
		if((System.currentTimeMillis() - timeLastEnemySpawn) >= ENEMY_SPAWN_INTERVAL)
		{
			timeLastEnemySpawn = System.currentTimeMillis();
			
			//spawn random missiles and bombs from ships
			for(Ship s : ships)
			{
				if(s.readyToFire(difficultyLevel))
					ordnance.add(s.getOrdnance(getContext(), city.getMidX(), city.getMidY()));
			}
			
			int randEnemyType = (int)(100 * Math.random());
			//spawn frigates
			if(randEnemyType >= 90)
			{
				for(int i = 0; i < Math.max(difficultyLevel, difficultyLevel - ships.size()); i++)
				{
					spawnFrigate();
				}
			}
			//spawn jets
			else if(randEnemyType >= 65)
			{
				for(int i = 0; i < Math.max(difficultyLevel, difficultyLevel - ships.size()); i++)
				{
					spawnJet();
				}
			}
			//spawn orbital missiles
			else
			{
				for(int i = 0; i < Math.max(difficultyLevel, difficultyLevel - ordnance.size()); i++)
				{
					spawnMissile();
				}
			}
		}
		
		//only try to spawn prizes every X milliseconds, defined in PRIZE_SPAWN_INTERVAL
		if((System.currentTimeMillis() - timeLastPrizeSpawn) >= PRIZE_SPAWN_INTERVAL)
		{
			timeLastPrizeSpawn = System.currentTimeMillis();
			
			int randPrizeType = (int)(40 * Math.random());
			
			if(randPrizeType >= 37)
			{
				spawnShieldPrize();
			}
			else if(randPrizeType >= 30)
			{
				spawnHealthPrize();
			}
			else if(randPrizeType >= 15)
			{
				spawnReloadPrize();
			}
			else
			{
				spawnSpeedPrize();
			}
		}
	}

	//spawn a jet with a random direction
	private void spawnJet()
	{
		Bitmap jetBitmap;
		
		int randDirection = (int)(Math.random() * 10);
		int randHeight = (int)(((float)height * .1) + (Math.random() * (float)height * .4));
		
		//spawn right-facing jet
		if(randDirection < 5)
		{
			jetBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jet_r);
			ships.add(new Jet(jetBitmap, 0 - jetBitmap.getWidth(), randHeight));
		}
		//spawn left-facing jet
		else
		{
			jetBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jet_l);
			ships.add(new Jet(jetBitmap, width, randHeight));
		}
	}
	
	//spawn a frigate with a random direction
	private void spawnFrigate()
	{
		Bitmap frigateBitmap;
		
		int randDirection = (int)(Math.random() * 10);
		int randHeight = (int)(((float)height * .1) + (Math.random() * (float)height * .4));
		
		//spawn right-facing frigate
		if(randDirection < 5)
		{
			frigateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.frigate_r);
			ships.add(new Frigate(frigateBitmap, 0 - frigateBitmap.getWidth(), randHeight));
		}
		//spawn left-facing frigate
		else
		{
			frigateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.frigate_l);
			ships.add(new Frigate(frigateBitmap, width, randHeight));
		}
	}
	
	//spawn a missile from orbit aimed at the city
	private void spawnMissile()
	{
		Bitmap missileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_missile);
		ordnance.add(new Missile(missileBitmap, (int)(width * Math.random()), 0 - missileBitmap.getHeight(), city.getMidX(), city.getMidY()));
	}
	
	//spawn a health prize randomly in the sky
	private void spawnHealthPrize()
	{
		int randX = (int)((width * 0.05) + (Math.random() * width * 0.9));
		int randY = (int)((height * 0.1) + (Math.random() * height * 0.4));
		Bitmap healthPrizeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.health_prize);
		
		prizes.add(new HealthPrize(healthPrizeBitmap, randX, randY));
	}
	
	//spawn a shield prize randomly in the sky
	private void spawnShieldPrize()
	{
		int randX = (int)((width * 0.05) + (Math.random() * width * 0.9));
		int randY = (int)((height * 0.1) + (Math.random() * height * 0.4));
		Bitmap shieldPrizeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.shield_prize);
		
		prizes.add(new ShieldPrize(shieldPrizeBitmap, randX, randY));
	}
	
	//spawn a reload prize randomly in the sky
	private void spawnReloadPrize()
	{
		int randX = (int)((width * 0.05) + (Math.random() * width * 0.9));
		int randY = (int)((height * 0.1) + (Math.random() * height * 0.4));
		Bitmap reloadPrizeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.reload_prize);

		prizes.add(new ReloadPrize(reloadPrizeBitmap, randX, randY));
	}
	
	//spawn a speed prize randomly in the sky
	private void spawnSpeedPrize()
	{
		int randX = (int)((width * 0.05) + (Math.random() * width * 0.9));
		int randY = (int)((height * 0.1) + (Math.random() * height * 0.4));
		Bitmap speedPrizeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.speed_prize);
		
		prizes.add(new SpeedPrize(speedPrizeBitmap, randX, randY));
	}
}
