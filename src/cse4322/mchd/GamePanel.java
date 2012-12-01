package cse4322.mchd;

import java.util.ArrayList;

import android.content.Context;
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
	private MainThread thread;
	
	private int width, height;
	private int score = 0;
	private boolean cleanupNeeded = false;
	//private Ship ship;
	private City city;
	private ArrayList<CityMissile> cityMissiles;
	private ArrayList<CityMissile> queuedCityMissiles;
	private ArrayList<CityMissile> deadCityMissiles;
	
	private ArrayList<Explosion> explosions;
	private ArrayList<Explosion> deadExplosions;
	
	//the fps to be displayed
	private String avgFPS;
	private String scoreText;
	private String healthText;
	
	private Paint hudPaint;
	private Paint fpsPaint;

	public GamePanel(Context context)
	{
		super(context);
		
		//adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		scoreText = context.getString(R.string.score);
		healthText = context.getString(R.string.city_health);
		
		//create ship and load bitmap
		//ship = new Ship(BitmapFactory.decodeResource(getResources(), R.drawable.frigate_l), 360, 640);
		cityMissiles = new ArrayList<CityMissile>();
		queuedCityMissiles = new ArrayList<CityMissile>();
		deadCityMissiles = new ArrayList<CityMissile>();
		
		explosions = new ArrayList<Explosion>();
		deadExplosions = new ArrayList<Explosion>();
		
		hudPaint = new Paint();
		hudPaint.setARGB(255, 0, 255, 0);
		hudPaint.setTextSize(20);
		
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
		
		if(cleanupNeeded)
			cleanupSprites();
	}
	
	public void setAvgFPS(String avgFPS)
	{
		this.avgFPS = avgFPS;
	}
	
	private void displayHUD(Canvas canvas)
	{
		if(canvas != null)
		{
			canvas.drawText(scoreText, 30, 30, hudPaint);
		}
	}
	
	private void displayFPS(Canvas canvas, String fps)
	{
		if(canvas != null & fps != null)
		{
			canvas.drawText(fps,  this.getWidth() - 100, 20, fpsPaint);
		}
	}

}
