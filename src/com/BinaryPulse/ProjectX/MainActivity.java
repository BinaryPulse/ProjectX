package com.BinaryPulse.ProjectX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;



import com.BinaryPulse.ProjectX.MainActivity;
import com.BinaryPulse.ProjectX.MainActivityGLSurfaceView;
import com.BinaryPulse.ProjectX.MainActivityRenderer;
import com.BinaryPulse.ProjectX.util.SystemUiHider;


public class MainActivity extends Activity 
{
	
	private static MainActivityGLSurfaceView glSurfaceView;
	private static MainActivityRenderer renderer;
	
	//private SystemUiHider mSystemUiHider;
	//private View decorView;

	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_FULLSCREEN;//FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
				
		super.onCreate(savedInstanceState);		
		
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    //getWindow().setFlags(WindowManager.LayoutParams.FLA, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	   
	    
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
			/*glSurfaceView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				
				@Override
				public void onSystemUiVisibilityChange( int visibility) {
					// TODO Auto-generated method stub
					if((visibility & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) ==0){
						
					}
					else{
						
					}
					
				}

				
			});*/
			// hideSystemUI();
				
			
			
			
		} else {
			// This is where you could create an OpenGL ES 1.x compatible
			// renderer if you wanted to support both ES 1 and ES 2.
			return;
		}	
		

		mSystemUiHider = SystemUiHider.getInstance(this, glSurfaceView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		//mSystemUiHider.hide();

		//delayedHide(200);
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
	
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.

						}

						//if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(1000);
						//}
					}
				});	
		
		// Set up the user interaction to manually show or hide the system UI.
		/*glSurfaceView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//if (TOGGLE_ON_CLICK) {
				//	mSystemUiHider.hide();
				//} else {
				//	mSystemUiHider.show();
				//}
				//mSystemUiHider.hide();
			}
		});*/

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		//findViewById(R.id.dummy_button).setOnTouchListener(
		//		mDelayHideTouchListener);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(2000);
	}


	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
			//hideSystemUI();
		}
	};


	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	
	

	@Override
	protected void onResume() {
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
		//glSurfaceView.onResume();
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);   }  
		// hideSystemUI();
		
		//View decorView =getWindow().getDecorView();
		//decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		//		| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		//glSurfaceView.onPause();	  
	}

		@Override
	public void onWindowFocusChanged(boolean hasFocus){
			
		glSurfaceView.setFocusable(true);
		glSurfaceView.requestFocus();
		super.onWindowFocusChanged(hasFocus);
		// hideSystemUI();
		
	}
	
	public void hideSystemUI()
	{
		    
		//glSurfaceView.setSystemUiVisibility(glSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//.SYSTEM_UI_FLAG_HIDE_NAVIGATION;)
		glSurfaceView.setSystemUiVisibility(MainActivityGLSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| MainActivityGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |MainActivityGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_STABLE|MainActivityGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN |MainActivityGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);// |LessonEightGLSurfaceView.SYSTEM_UI_FLAG_IMMERSIVE); 
		
	}
	

			
	
	
}
