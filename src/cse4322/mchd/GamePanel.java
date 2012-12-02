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
		
		//create ship and load bitmap
		//ship = new Ship(BitmapFactory.decodeResource(getResources(), R.drawable.frigate_l), 360, 640);
		cityMissiles = new ArrayList<CityMissile>();
		queuedCityMissiles = new ArrayList<CityMissile>();
		deadCityMissiles = new ArrayList<CityMissile>();
		
		explosions = new ArrayList<Explosion>();
		deadExplosions = new ArrayList<Explosion>();
		
		ships = new ArrayList<Ship>();
		deadShips = new ArrayList<Ship>();
		
		ordnance = new ArrayList<Ordnance>();
		deadOrdnance = new ArrayList<Ordnance>();
		
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
			//delegating event handling to the ship
			//ship.handleActionDown((int)event.getX(), (int)event.getY());
			
			//end the game if the bottom of the screen is tapped
			/*
			if(event.getY() > getHeight() - 50)
			{
				thread.setRunning(false);
				((Activity) getContext()).finish();
			}
			else
			{
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}*/
			
			if(event.getY() < height - city.getHeight())
			{
				if(city.readyToFire())
				{
					//adding a missile directly into the city missile list while updating/drawing could throw a ConcurrentModificationException, crashing the game.
					queuedCityMissiles.add(new CityMissile(BitmapFactory.decodeResource(getResources(), R.drawable.city_missile), BitmapFactory.decodeResource(getResources(), R.drawable.target), city.getMidX(), city.getMidY(), (int)event.getX(), (int)event.getY()));
				}
			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			//the gestures
			/*
			if(ship.isTouched())
			{
				//the ship was picked up and is being dragged
				ship.setX((int)event.getX());
				ship.setY((int)event.getY());
			}*/
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			//touch was released
			/*
			if(ship.isTouched())
			{
				ship.setTouched(false);
			}*/
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
	}
	
	protected void render(Canvas canvas)
	{
		if(canvas != null)
		{
			//fill the canvas with black
			canvas.drawColor(Color.BLACK);
			//ship.draw(canvas);
			if(city != null)
				city.draw(canvas);
			for(Ordnance o : ordnance)
			{
				o.draw(canvas);
			}
			
			for(Ship s : ships)
			{
				s.draw(canvas);
			}

			for(Explosion e : explosions)
			{
				e.draw(canvas);
			}

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
	
	protected void update()
	{
		cleanupNeeded = false;
		
		if(city != null)
			city.update();

		for(Explosion e : explosions)
		{
			e.update();
			if(e.hasExpired())
			{
				deadExplosions.add(e);
				cleanupNeeded = true;
			}
		}

		
		if(!queuedCityMissiles.isEmpty())
		{
			cityMissiles.addAll(queuedCityMissiles);
			queuedCityMissiles.clear();
		}
		
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
	
		for(Ship s : ships)
		{
			s.update(width);
			boolean spawnExplosion = false;
			for(Explosion e : explosions)
			{
				if(s.hasCollided(e))
				{
					spawnExplosion = true;
					addScore(s.getPointsWorth());
					deadShips.add(s);
					cleanupNeeded = true;
				}
			}
			if(spawnExplosion)
				explosions.add(new Explosion((int)s.getX(), (int)s.getY()));
		}
		
		for(Ordnance o : ordnance)
		{
			o.update();
			boolean spawnExplosion = false;
			for(Explosion e : explosions)
			{
				if(o.hasCollided(e))
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
			
			if(spawnExplosion)
				explosions.add(new Explosion((int)o.getX(), (int)o.getY()));
		}
		
		if(cleanupNeeded)
			cleanupSprites();
		
		tryToSpawn();
	}
	
	public void setAvgFPS(String avgFPS)
	{
		this.avgFPS = avgFPS;
	}
	
	private void displayHUD(Canvas canvas)
	{
		if(canvas != null && city != null)
		{
			canvas.drawText(scoreText + score, 30, 30, scorePaint);
			canvas.drawText(healthText + city.getHealth(), 30, height - 50, healthPaint);
		}
	}
	
	private void displayFPS(Canvas canvas, String fps)
	{
		if(canvas != null & fps != null)
		{
			canvas.drawText(fps,  this.getWidth() - 100, 20, fpsPaint);
		}
	}
	
	private void tryToSpawn()
	{
		if((System.currentTimeMillis() - timeLastEnemySpawn) >= ENEMY_SPAWN_INTERVAL)
		{
			timeLastEnemySpawn = System.currentTimeMillis();
			
			int randEnemyType = (int)(100 * Math.random());
			if(randEnemyType >= 90)
			{
				for(int i = 0; i < Math.max(difficultyLevel, difficultyLevel - ships.size()); i++)
				{
					spawnFrigate();
				}
			}
			else if(randEnemyType >= 65)
			{
				for(int i = 0; i < Math.max(difficultyLevel, difficultyLevel - ships.size()); i++)
				{
					spawnJet();
				}
			}
			else
			{
				for(int i = 0; i < Math.max(difficultyLevel, difficultyLevel - ordnance.size()); i++)
				{
					spawnMissile();
				}
			}
			
		}
	}

	//spawn a jet with a random direction
	private void spawnJet()
	{
		Bitmap jetBitmap;
		
		int randDirection = (int)(Math.random() * 10);
		int randHeight = (int)(((float)height * .1) + (Math.random() * (float)height * .3));
		
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
		int randHeight = (int)(((float)height * .1) + (Math.random() * (float)height * .3));
		
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
			ships.add(new Jet(frigateBitmap, width, randHeight));
		}
	}
	
	//spawn a missile from orbit aimed at the city
	private void spawnMissile()
	{
		Bitmap missileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemy_missile);
		ordnance.add(new Missile(missileBitmap, (int)(width * Math.random()), 0 - missileBitmap.getHeight(), city.getMidX(), city.getMidY()));
	}
}
