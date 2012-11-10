package cse4322.mchd;

import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	
	private static final String TAG = MainThread.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        //set out GamePanel as the View
        setContentView(new GamePanel(this));
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
}
