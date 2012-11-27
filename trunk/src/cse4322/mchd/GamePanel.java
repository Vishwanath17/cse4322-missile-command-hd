package cse4322.mchd;

import java.util.ArrayList;

import android.app.Activity;
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
	//private Ship ship;
	private City city;
	private ArrayList<CityMissile> cityMissiles;
	private ArrayList<CityMissile> deadCityMissiles;
	
	//the fps to be displayed
	private String avgFPS;
	private static final String scoreText = "Score: ";
	private static final String healthText = "City Health: ";
	
	private Paint hudPaint;

	public GamePanel(Context context)
	{
		super(context);
		
		//adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		//create ship and load bitmap
		//ship = new Ship(BitmapFactory.decodeResource(getResources(), R.drawable.frigate_l), 360, 640);
		cityMissiles = new ArrayList<CityMissile>();
		deadCityMissiles = new ArrayList<CityMissile>();
		
		hudPaint = new Paint();
		
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
					cityMissiles.add(new CityMissile(BitmapFactory.decodeResource(getResources(), R.drawable.city_missile), BitmapFactory.decodeResource(getResources(), R.drawable.target), city.getMidX(), city.getMidY(), (int)event.getX(), (int)event.getY()));
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
	
	private void cleanupSprites()
	{
		cityMissiles.removeAll(deadCityMissiles);
		deadCityMissiles.clear();
	}
	
	protected void render(Canvas canvas)
	{
		//fill the canvas with black
		canvas.drawColor(Color.BLACK);
		//ship.draw(canvas);
		if(city != null)
			city.draw(canvas);
		for(CityMissile c : cityMissiles)
		{
			c.draw(canvas);
		}
		//display HUD
		displayHUD(canvas);
		//display fps
		displayFPS(canvas, avgFPS);
	}
	
	protected void update()
	{
		if(city != null)
			city.update();
		for(CityMissile c : cityMissiles)
		{
			c.update();
			if(c.hasDetonated())
				deadCityMissiles.add(c);
		}
		if(!deadCityMissiles.isEmpty())
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
			Paint paint = new Paint();
			paint.setARGB(255, 0, 255, 0);
			paint.setTextSize(20);
			canvas.drawText(scoreText, 30, 30, paint);
		}
	}
	
	private void displayFPS(Canvas canvas, String fps)
	{
		if(canvas != null & fps != null)
		{
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps,  this.getWidth() - 50, 20, paint);
		}
	}

}
