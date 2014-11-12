package com.BinaryPulse.ProjectX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;



import com.BinaryPulse.ProjectX.MainActivity;
import com.BinaryPulse.ProjectX.MainActivityGLSurfaceView;
import com.BinaryPulse.ProjectX.MainActivityRenderer;
public class MainActivity extends Activity 
{
	
	private static MainActivityGLSurfaceView glSurfaceView;
	private static MainActivityRenderer renderer;
	
	//private SystemUiHider mSystemUiHider;
	//private View decorView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
				
		super.onCreate(savedInstanceState);		
		
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	   
	    
		glSurfaceView = new MainActivityGLSurfaceView(this);	
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
			renderer = new MainActivityRenderer(this, glSurfaceView);
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
		//glSurfaceView.onResume();
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);   }  
  
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		//glSurfaceView.onPause();	  
	}

	/*	@Override
	public void onWindowFocusChanged(boolean hasFocus){
			
		glSurfaceView.setFocusable(true);
		glSurfaceView.requestFocus();
		super.onWindowFocusChanged(hasFocus);
		
	}
*/	
	public void hideSystemUI()
	{
		    
		//glSurfaceView.setSystemUiVisibility(glSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//.SYSTEM_UI_FLAG_HIDE_NAVIGATION;)
		glSurfaceView.setSystemUiVisibility(MainActivityGLSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| MainActivityGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |MainActivityGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_STABLE|MainActivityGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN |MainActivityGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);// |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_IMMERSIVE); 
		
	}
	

			
	
	
}
