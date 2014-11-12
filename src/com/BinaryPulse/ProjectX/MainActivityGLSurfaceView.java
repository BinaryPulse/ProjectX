package com.BinaryPulse.ProjectX;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.BinaryPulse.ProjectX.R;
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.Window;
import android.view.WindowManager;

public class MainActivityGLSurfaceView extends GLSurfaceView implements ErrorHandler
{	
	private MainActivityRenderer renderer;
	
	// Offsets for touch events	 
    private float previousX;
    private float previousY;
    
    private float density;


        	
	public MainActivityGLSurfaceView(Context context) 
	{
		super(context);		
		
	}
	
	public MainActivityGLSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);	
		

	}
	
	@Override
	public void handleError(final ErrorType errorType, final String cause) {
		// Queue on UI thread.
		post(new Runnable() {
			@Override
			public void run() {
				final String text;

				switch (errorType) {
				case BUFFER_CREATION_ERROR:
					text = String
							.format(getContext().getResources().getString(
									R.string.lesson_eight_error_could_not_create_vbo), cause);
					break;
				default:
					text = String.format(
							getContext().getResources().getString(
									R.string.lesson_eight_error_unknown), cause);
				}

				Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();

			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		
		
		if (event != null)
		{		
			renderer.m_Motion = event;
			float x = event.getX();
			float y = event.getY();
			
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				if (renderer != null)
				{
					float deltaX = (x - previousX) / density / 2f;
					float deltaY = (y - previousY) / density / 2f;
					
					renderer.deltaX += deltaX;
					renderer.deltaY += deltaY;												
				}
			}	
			
			previousX = x;
			previousY = y;
			
			return true;
		}
		else
		{
			return super.onTouchEvent(event);
		}	


		//this.setSystemUiVisibility(super.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		//this.setSystemUiVisibility(this.SYSTEM_UI_FLAG_HIDE_NAVIGATION); 
	}

	// Hides superclass method.
	public void setRenderer(MainActivityRenderer renderer, float density) 
	{
		this.renderer = renderer;
		this.density = density;
		super.setRenderer(renderer);
	}
	
}

	