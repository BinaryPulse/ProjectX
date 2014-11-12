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
import android.view.View;
import android.opengl.GLSurfaceView;
import android.content.pm.ActivityInfo;
public class LessonEightActivity extends Activity {	
	private static LessonEightGLSurfaceView glSurfaceView;
	private static LessonEightRenderer renderer;
	
	//private SystemUiHider mSystemUiHider;
	//private View decorView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
				
		super.onCreate(savedInstanceState);		
		
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	   
	    
		glSurfaceView = new LessonEightGLSurfaceView(this);	
/*
		glSurfaceView.setSystemUiVisibility(LessonEightGLSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION //|LessonEightGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN);//
				|LessonEightGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_STABLE|LessonEightGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ); 
*/
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
			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			// Set the renderer to our demo renderer, defined below.
			renderer = new LessonEightRenderer(this, glSurfaceView);
			glSurfaceView.setRenderer(renderer, displayMetrics.density);
			
			//if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);   }
/*			
			glSurfaceView.setOnSystemUiVisibilityChangeListener(new LessonEightGLSurfaceView.OnSystemUiVisibilityChangeListener(){
				@Override
				public void onSystemUiVisibilityChange(int visibility){
					

					
					if((visibility & LessonEightGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN) == 0){
							glSurfaceView.getHandler().postDelayed(new Runnable(){
							@Override
							public void run() {
								glSurfaceView.setSystemUiVisibility(LessonEightGLSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION// |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN ); 
								|LessonEightGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN );     
					    }}, 1000);
					}
					else
					{
						
			        }
			
				}
				
			});		
*/			
/*		
			glSurfaceView.setOnTouchListener( new LessonEightGLSurfaceView.OnTouchListener(){
				@Override
				public boolean onTouch(View view1, MotionEvent event) {

					return glSurfaceView.onTouchEvent(event);
					
				}
					
				});	
*/				
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
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);   }  
  
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		glSurfaceView.onPause();	  
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
		glSurfaceView.setSystemUiVisibility(LessonEightGLSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| LessonEightGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_STABLE|LessonEightGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);// |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_IMMERSIVE); 
		
	}
	

		
	

}