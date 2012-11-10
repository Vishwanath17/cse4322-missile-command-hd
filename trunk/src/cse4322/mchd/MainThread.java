package cse4322.mchd;

import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
	
	private static final String TAG = MainThread.class.getSimpleName();
	//flag to hold game state
	private boolean running;
	
	private SurfaceHolder surfaceHolder;
	private GamePanel gamePanel;
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	@Override
	public void run()
	{
		long tickCount = 0L;
		Log.d(TAG, "Starting game loop");
		while(running)
		{
			tickCount++;
			//update game state
			//render state to the screen
		}
		Log.d(TAG, "Game loop executed " + tickCount + " times");
	}
	
	public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel)
	{
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

}
