
package com.BinaryPulse.ProjectX.MyUI;

import android.content.Context;

import com.BinaryPulse.ProjectX.MyFont.MyFont;

public abstract class  UIControlUnit{
	  

	  public static final int   BUTTON_SQUARE   = 0;
	  public static final int   BUTTON_POLYGON  = 1;
	  public static final int   CKECKBOX_SQUARE = 2;
	  public static final int   TEXTBOX_SQUARE  = 3;
	  public static final int   LISTBOX_SQUARE  = 4;
	  public static final int   BIGBOARD_SQUARE = 5;
	  public static final int   BIGBOARD_POLYGON= 6;
	  public static final int   BUTTON_CIRCLE   = 7;
	  public static final int   LABEL_BLANK     = 8;
	  public static final float ContrlUnitWidth[]={80.0f,40.0f,20.0f,45.0f,45.0f,100.0f,100.0f};
	  public static final float ContrlUnitHeight[] ={5.0f,13.0f,20.0f,12.0f,15.0f,80.0f,80.0f};
	 
	  
	  protected  int     	m_ControlType;
	  protected  static float   	m_OffSetX,m_OffSetY,m_Scale,m_Width,m_Height,m_PositionCoordinate,m_IntervalCoordinate;
	  protected	 boolean    m_IsOnfocus;
	  protected	 boolean    m_IsActive;

	  protected	 int     m_TextureObject1;
	  protected	 int     m_TextureObject2;
	  protected	 float 	 m_BorderWidth; 
	  protected	 float 	 m_CornerProportion;
	  protected	 UIControlUnit  m_NextUIControlUnit;// = new UIControlUnit;

	  protected	 float   m_FontOffsetX,m_FontOffsetY;
	  protected	 String  m_TextString;
	  protected	 int     m_FontLength;
	  protected	 float   m_FontSize;
	  protected  Context m_Context;
	  protected  MyFont  m_Font;

	  
	  UIControlUnit(Context context){	
		  	 m_Context = context;
			 m_ControlType=BUTTON_SQUARE;
			 m_OffSetX=m_OffSetY=100.0f;
			 m_Width=ContrlUnitWidth[m_ControlType];
		     m_Height=ContrlUnitHeight[m_ControlType];
	         m_Scale=1.0f;

			 m_IsOnfocus=false;
			 m_IsActive=false;
			 m_BorderWidth=4;
			 m_CornerProportion=0.1f;
			 m_PositionCoordinate=0;
			 m_IntervalCoordinate=0;
		 }
	  
	     UIControlUnit(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float BorderWidth)
	     {
	    	 m_Context = context; 
			 m_ControlType=ControlType;
		     m_OffSetX=OffSetX;//+100;
			 m_OffSetY=OffSetY;//+100;
	         m_Scale=1.0f;
			 m_Width=ContrlUnitWidth[m_ControlType]*m_Scale;
		     m_Height=ContrlUnitHeight[m_ControlType]*m_Scale;


	         //m_OnEventProcessPointer=NULL;
			 //m_SetActiveKey=SET_ACTIVE_KEY;
			 //m_SetTriggeredKey=SET_TRIGGERED_KEY;
			 //m_KeyBoardActive=false;
			 //m_KeyBoardTriggered=false;
			 m_IsOnfocus=false;
			 m_IsActive=false;
			 m_BorderWidth=BorderWidth;
			 m_CornerProportion =0.1f;
			 m_PositionCoordinate =0;
			 m_IntervalCoordinate =0;
			 }

	     //abstract ~UIControlUnit(){}

	     abstract void  UserKeyInput(int InputKey);

	     abstract void  UserMouseMove(float wParam, float lParam);

	     abstract void  UserMouseDown(float wParam, float lParam);
		 
	     abstract void  UserMouseUp(float wParam, float lParam);

	     abstract void  Render(float[] modelMatrix);

	     abstract boolean IsOnFocus();//{return false;}
	   
	     void SetNext(UIControlUnit  NextUIControlUnit){m_NextUIControlUnit=NextUIControlUnit;}

	     UIControlUnit GetNext(){return m_NextUIControlUnit;}


		
	};
