package cse4322.mchd;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
	
	private static final String TAG = MainThread.class.getSimpleName();
	//flag to hold game state
	private boolean running;
	
	//desired fps
	private final static int MAX_FPS = 50;
	//maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	//the frame period
	private final static int FRAME_PERIOD = 1000/MAX_FPS;
	
	//Stuff for stats
	private DecimalFormat df = new DecimalFormat("0.##");	//2 dp
	//we'll be reading the state every second
	private final static int STAT_INTERVAL = 1000;	//ms
	//the average will be calculated by storing the last n FPS's
	private final static int FPS_HISTORY_NR = 10;
	//last time the status was stored
	private long lastStatusStore = 0;
	//the status time counter
	private long statusIntervalTimer = 0l;
	//number of frames skipped since the game started
	private long totalFramesSkipped = 0l;
	//number of frames skipped in a store cycle (1 sec)
	private long framesSkippedPerStatCycle = 0l;
	
	//number of rendered frames in an interval
	private int frameCountPerStatCycle = 0;
	private long totalFrameCount = 0l;
	//the last FPS values
	private double fpsStore[];
	//the number of times the stat has been read
	private long statsCount = 0;
	//the average FPS since the game started
	private double averageFPS = 0.0;
	
	//Surface golder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	//The actual view that handles inputs and draws to the surface
	private GamePanel gamePanel;
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	@Override
	public void run()
	{
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		
		//initialize timing elements for stat gathering
		initTimingElements();
		
		long beginTime;		//the time when the cycle began
		long timeDiff;		//the time it took for the cycle to execute
		int sleepTime;		//ms to sleep (<0 if we're behind)
		int framesSkipped;	//number of frames being skipped
		
		sleepTime = 0;
		
		while(running)
		{
			canvas = null;
			//try locking the canvas for exclusive pixel editing on the surface
			try
			{
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder)
				{
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;	//resetting the frames skipped
					//update game state
					this.gamePanel.update();
					//render the state to the screen
					//draws the canvas on the panel
					this.gamePanel.render(canvas);
					//calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;
					//calculate the sleep time
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					if(sleepTime > 0)
					{
						//if sleepTime > 0, we're OK
						try
						{
							//send the thread to sleep for a short period
							//very useful for battery saving
							Thread.sleep(sleepTime);
						}
						catch(InterruptedException e)
						{
							
						}
					}
					
					while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
					{
						//we need to catch up
						//update without rendering
						this.gamePanel.update();
						//add frame period to check if in next frame
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
					
					if(framesSkipped > 0)
					{
						Log.d(TAG, "Skipped:" + framesSkipped);
					}
					
					//for statistics
					framesSkippedPerStatCycle += framesSkipped;
					//calling the method to store the gathered statistics
					storeStats();
				}
			}
			finally
			{
				//in case of an exception the surface is not left in an inconsistent state
				if(canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}//end finally
		}
	}
	
	/**
	 * The statistics - it is called every cycle, it checks if time since last store is greater
	 * than the statistics gathering period (1 sec) and if so it calculates the FPS for the last
	 * period and stores it.
	 * 
	 * It tracks the number of frames per period.  The number of frames since the start of the
	 * period are summed up and the calculation takes part only if the next period and the frame
	 * count is reset to 0.
	 */
	private void storeStats()
	{
		frameCountPerStatCycle++;
		totalFrameCount++;
		
		//check the actual time
		statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);
		
		if(statusIntervalTimer >= lastStatusStore + STAT_INTERVAL)
		{
			//calculate the actual frames per status check interval
			double actualFPS = (double)(frameCountPerStatCycle / (STAT_INTERVAL/1000));
			
			//stores the latest fps in the array
			fpsStore[(int)statsCount % FPS_HISTORY_NR] = actualFPS;
			
			//increase the number of times statistics was calculated
			statsCount++;
			
			double totalFPS = 0.0;
			//sum up the stored fps values
			for(int i = 0; i < FPS_HISTORY_NR; i++)
			{
				totalFPS += fpsStore[i];
			}
			
			//obtain the average
			if(statsCount < FPS_HISTORY_NR)
			{
				//in care of the first 10 triggers
				averageFPS = totalFPS/statsCount;
			}
			else
			{
				averageFPS = totalFPS/FPS_HISTORY_NR;
			}
			
			//saving the number of total frames skipped
			totalFramesSkipped += framesSkippedPerStatCycle;
			//resetting the counter after a status record (1 sec)
			framesSkippedPerStatCycle = 0;
			statusIntervalTimer = 0;
			frameCountPerStatCycle = 0;
			
			statusIntervalTimer = System.currentTimeMillis();
			lastStatusStore = statusIntervalTimer;
			//Log.d(TAG, "Average FPS:" + df.format(averageFPS));
			gamePanel.setAvgFPS("FPS: " + df.format(averageFPS));
		}
	}
	
	
	private void initTimingElements()
	{
		//initialize timing elements
		fpsStore = new double[FPS_HISTORY_NR];
		for(int i = 0; i < FPS_HISTORY_NR; i++)
		{
			fpsStore[i] = 0.0;
		}
		Log.d(TAG + ".initTimingElements()", "Timing elements for stats initialized");
	}
	public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel)
	{
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

}
