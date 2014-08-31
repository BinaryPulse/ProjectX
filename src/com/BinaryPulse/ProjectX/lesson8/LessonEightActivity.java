package com.BinaryPulse.ProjectX.lesson8;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.view.MotionEvent;



public class LessonEightActivity extends Activity {	
	private LessonEightGLSurfaceView glSurfaceView;
	private LessonEightRenderer renderer;
	//private SystemUiHider mSystemUiHider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
				
		super.onCreate(savedInstanceState);		
		
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	   
		glSurfaceView = new LessonEightGLSurfaceView(this);
	
		//glSurfaceView.setSystemUiVisibility(glSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION);  
  		//this.hideSystemUI();
		setContentView(glSurfaceView);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

		if (supportsEs2) {
			// Request an OpenGL ES 2.0 compatible context.
			glSurfaceView.setEGLContextClientVersion(2);

			final DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

			// Set the renderer to our demo renderer, defined below.
			renderer = new LessonEightRenderer(this, glSurfaceView);
			glSurfaceView.setRenderer(renderer, displayMetrics.density);
		} else {
			// This is where you could create an OpenGL ES 1.x compatible
			// renderer if you wanted to support both ES 1 and ES 2.
			return;
		}		
	}

	@Override
	protected void onResume() {
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
		glSurfaceView.onResume();
  
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		glSurfaceView.onPause();	  
	}

	private void hideSystemUI()
	{
		    
		//glSurfaceView.setSystemUiVisibility(glSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//.SYSTEM_UI_FLAG_HIDE_NAVIGATION;)
		glSurfaceView.setSystemUiVisibility(glSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| glSurfaceView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//.SYSTEM_UI_FLAG_HIDE_NAVIGATION;)
		
	}

}