package cse4322.mchd;

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
	private Ship ship;
	
	//the fps to be displayed
	private String avgFPS;

	public GamePanel(Context context)
	{
		super(context);
		
		//adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		//create ship and load bitmap
		ship = new Ship(BitmapFactory.decodeResource(getResources(), R.drawable.frigate_l), 360, 640);
		
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
			ship.handleActionDown((int)event.getX(), (int)event.getY());
			
			//end the game if the bottom of the screen is tapped
			if(event.getY() > getHeight() - 50)
			{
				thread.setRunning(false);
				((Activity) getContext()).finish();
			}
			else
			{
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			//the gestures
			if(ship.isTouched())
			{
				//the ship was picked up and is being dragged
				ship.setX((int)event.getX());
				ship.setY((int)event.getY());
			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			//touch was released
			if(ship.isTouched())
			{
				ship.setTouched(false);
			}
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		render(canvas);
	}
	
	protected void render(Canvas canvas)
	{
		//fill the canvas with black
		canvas.drawColor(Color.BLACK);
		ship.draw(canvas);
		//display fps
		displayFPS(canvas, avgFPS);
	}
	
	protected void update()
	{
		
	}
	
	public void setAvgFPS(String avgFPS)
	{
		this.avgFPS = avgFPS;
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
