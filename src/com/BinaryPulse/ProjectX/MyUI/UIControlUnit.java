
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
	  
	  

	  public final static int CONTROL_UNIT_BUTTON   = 0;        // enum of control unit: button
	  public final static int CONTROL_UNIT_MESSAGE  = 1;        // enum of control unit: message box
	  public final static int CONTROL_UNIT_SLIDEBAR = 2;        // enum of control unit: slide bar
	  public final static int CONTROL_UNIT_DROPLIST = 3;        // enum of control unit: drop down list
	  public final static int CONTROL_UNIT_SELBOX   = 4;        // enum of control unit: select box
	  public final static int CONTROL_UNIT_OSCILLO  = 5;        // enum of control unit: OSCILLOSCOPE	  
	  
	  public static final float ContrlUnitWidth[]={80.0f,40.0f,20.0f,45.0f,45.0f,100.0f,100.0f};
	  public static final float ContrlUnitHeight[] ={5.0f,13.0f,20.0f,12.0f,15.0f,80.0f,80.0f};
	 
	  
	  protected  int     	m_ControlType;
	  protected  int     	m_BorderStyle;
	  protected  float   	m_OffSetX,m_OffSetY,m_Scale,m_Width,m_Height,m_PositionCoordinate,m_IntervalCoordinate;
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
	  
	    protected float vertexBufferForBorder[];
	    protected short indexBufferForBorder[];
	    protected float vertexBufferForArea[];
	    protected short indexBufferForArea[];
	    
		protected float[] mMVPMatrixForBorder = new float[16];	
		protected float[] mMVPMatrixForArea = new float[16];	
	  
	  UIControlUnit(Context context){	
		  	 m_Context = context;
		  	 m_BorderStyle=BUTTON_SQUARE;
			 m_OffSetX=m_OffSetY=100.0f;
			 m_Width=ContrlUnitWidth[m_BorderStyle];
		     m_Height=ContrlUnitHeight[m_BorderStyle];
	         m_Scale=1.0f;

			 m_IsOnfocus=false;
			 m_IsActive=false;
			 m_BorderWidth=4;
			 m_CornerProportion=0.1f;
			 m_PositionCoordinate=0;
			 m_IntervalCoordinate=0;
			 m_ControlType =0;
		 }
	  
	     UIControlUnit(Context context,int BorderStyle,float OffSetX,float OffSetY,float Scale,float BorderWidth)
	     {
	    	 m_Context = context; 
	    	 m_BorderStyle=BorderStyle;
		     m_OffSetX=OffSetX;//+100;
			 m_OffSetY=OffSetY;//+100;
	         m_Scale=1.0f;
			 m_Width=ContrlUnitWidth[m_BorderStyle]*m_Scale;
		     m_Height=ContrlUnitHeight[m_BorderStyle]*m_Scale;


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
			 m_ControlType=0;
			 }

	     //abstract ~UIControlUnit(){}

	     abstract void  UserKeyInput(int InputKey);

	     abstract void  UserMouseMove(float wParam, float lParam);

	     abstract void  UserMouseDown(float wParam, float lParam);
		 
	     abstract void  UserMouseUp(float wParam, float lParam);

	     abstract void  Render(float[] modelMatrix);
	     
	     abstract void  RenderFont(float[] modelMatrix);
	     
	     void  SetFont( MyFont Font){m_Font = Font;}

	     abstract boolean IsOnFocus();//{return false;}
	   
	     void SetNext(UIControlUnit  NextUIControlUnit){m_NextUIControlUnit=NextUIControlUnit;}

	     UIControlUnit GetNext(){return m_NextUIControlUnit;}


		
	};
