package cse4322.mchd;

/**
 * Missile Command HD
 * Android port of Missile Command HD from TouchDevelop
 * For CSE 4322, Team 2
 * 
 * HD remake of Missile Command with additional features:
 * - Survival gameplay instead of round-based
 * - One city with health to defend
 * - Unlimited ammo with reload time
 * - Power-ups: missile speed, reload time, city health, shield power
 * - Additional enemies: jets w/ missiles, frigates w/ bombs
 * 
 * @author Shawn Gilleland
 * @author Bilal Nawaz
 * @author Sabin Shrestha
 */

import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	
	private static final String TAG = MainThread.class.getSimpleName();
	
	private GamePanel gamePanel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //force the screen into portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //making it full screen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //set out GamePanel as the View
        gamePanel = new GamePanel(this);
        setContentView(gamePanel);
        Log.d(TAG, "View added");
    }
    
    @Override
    protected void onDestroy()
    {
    	Log.d(TAG, "Destroying...");
    	super.onDestroy();
    }
    
    @Override
    protected void onStop()
    {
    	Log.d(TAG, "Stopping...");
    	super.onStop();
    }
    
    @Override
    protected void onResume()
    {
    	Log.d(TAG, "Resuming...");
    	super.onResume();
    	gamePanel.resume();
    }
    
    @Override
    protected void onPause()
    {
    	Log.d(TAG, "Pausing...");
    	super.onPause();
    	gamePanel.pause();
    }
}
