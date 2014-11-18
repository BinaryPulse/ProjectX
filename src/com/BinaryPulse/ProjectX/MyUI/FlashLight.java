package com.BinaryPulse.ProjectX.MyUI;
import java.lang.Math;
import java.text.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;

import com.BinaryPulse.ProjectX.R;
import com.BinaryPulse.ProjectX.MyFont.MyFont;
import com.BinaryPulse.ProjectX.common.RawResourceReader;
import com.BinaryPulse.ProjectX.common.ShaderHelper;
import com.BinaryPulse.ProjectX.common.TextureHelper;

import android.opengl.Matrix;
import android.util.DisplayMetrics;

public class FlashLight extends UIControlUnit {


	protected int[]    m_Color;
	protected float[]  m_DivValueY;//[];
	protected float    m_DivValueX;
	protected float    m_CuveLineWidth;
	protected static int m_ActiveMouseX;
	protected static int m_ActiveMouseY;
	protected static int m_ReleaseMouseX;
	protected static int m_ReleaseMouseY;

	/** This is a handle to our cube shading program. */
	protected  int program[];						   // OpenGL Program object
	protected int ColorHandle;						   // Shader color handle	
	protected int TextureUniformHandle;                 // Shader texture handle
	protected int TextureMoveUniformHandle;
	protected int textureId; 
	/** OpenGL handles to our program uniforms. */
	protected  int mvpMatrixUniform;
	protected  int mvMatrixUniform;
	protected  int lightPosUniform;

	
	/** OpenGL handles to our program attributes. */
	protected int positionAttribute;
	protected int texcordAttribute;
	protected int colorAttribute;	
	protected int BoundaryHandle;
	/** Identifiers for our uniforms and attributes inside the shaders. */	
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	//private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";


	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String COLOR_ATTRIBUTE = "a_Color";

	public float[] mBoundary ={1.0f,-1.0f,1.0f,-1.0f};
	
	protected int[] vbo = new int[1];
	protected int[] ibo = new int[1];
	
	//private float[] mVPMatrix;		
	protected float[] mMVPMatrix = new float[16];	
	protected float[] mcolor;
	
    protected int mWindowWidth;
    protected int mWindowHeight;


    protected float m_MouseOffSetY;
    protected float m_DragBoxLength;
    protected float m_DragBoxHeight;
    protected float m_DragBoxOffSetY;
    protected float m_DragBoxOffSetX;

    protected float m_SlideLineWidthPer;
    
    
    protected int m_DropDownFontList;

    protected boolean  m_IsOnDrag;
    
    protected boolean  m_DynamicShow;
    protected float m_timer,m_timerX ;

/*##############################################################################
           
		         对象模块功能描述： DropDownList（示波器）

###############################################################################*/

public void InitGLDataForBorder()
{
	  
    // initiate vertex buffer for border 	
	float zaxis = 0.0f;
	float[] modleMatrix1 =new float[16];
	float vertexBuffer[] = {
      0.48f, 0.0f,
      0.0f, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - 0.0f,zaxis,	  
	  0.48f, 0.1f,
      m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -m_BorderWidth,zaxis,

	  0.8f, 0.0f,
      m_Width*m_Scale+2*m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - 0.0f ,zaxis,	 
	  0.8f, 0.1f,
      m_Width*m_Scale+m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - m_BorderWidth,zaxis,

	  1.0f, 0.0f,
      m_Width*m_Scale+2*m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - (m_SlideLineWidthPer*m_Height*m_Scale+2*m_BorderWidth) ,zaxis,	  
	  1.0f, 0.1f,
      m_Width*m_Scale+m_BorderWidth,  m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - (m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth),zaxis,

	  0.48f, 0.0f,
	  m_Width*m_Scale+2*m_BorderWidth,  m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - (m_SlideLineWidthPer*m_Height*m_Scale+2*m_BorderWidth) ,zaxis,	  
	  0.48f, 0.1f,
	  m_Width*m_Scale+m_BorderWidth,  m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - (m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth) ,zaxis, 	 

	  0.8f, 0.0f,
	  0.0f,  m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - (m_SlideLineWidthPer*m_Height*m_Scale+2*m_BorderWidth),zaxis,	  
	  0.8f, 0.1f,
	  m_BorderWidth,  m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - (m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth),zaxis,

	  1.0f, 0.0f,
	  0.0f,  m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - 0.0f,zaxis,	  
	  1.0f, 0.1f,
	  m_BorderWidth,  m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth - m_BorderWidth ,zaxis

	};

    short indexBuffer[]  ={0,1,2,3,4,5,6,7,8,9,10,11};	
    vertexBufferForBorder = new float[12*5];
    indexBufferForBorder = new short[12];
    
	for(int i =0;i<12;i++)
	{
		indexBufferForBorder[i] = indexBuffer[i];
		vertexBufferForBorder[i*5] = vertexBuffer[i*5];
		vertexBufferForBorder[i*5 +1] = vertexBuffer[i*5 +1];
		vertexBufferForBorder[i*5 +2] = vertexBuffer[i*5 +2];
		vertexBufferForBorder[i*5 +3] = vertexBuffer[i*5 +3];
		vertexBufferForBorder[i*5 +4] = vertexBuffer[i*5 +4];
	}
    
	Matrix.setIdentityM(modleMatrix1, 0);
	Matrix.translateM(modleMatrix1, 0, m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);		
	for(int i =0;i<16;i++)
		mMVPMatrixForBorder[i] = modleMatrix1[i];	

}


public void InitGLDataForArea()
{
	float[] modleMatrix1 =new float[16];
 
	float m_AreaColor =0.0f;
	float m_AreaColor1 =0.0f;
	float m_AreaColor2 =0.5f;
	float m_Alph =0.4f;
	float vertexBuffer[] = {

			 //0.5f+0.5f*m_PositionCoordinate,0.5f-0.5f*m_PositionCoordinate,
			 m_Width*m_Scale+m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -m_BorderWidth, 0.0f,
			 0.8f,0.8f,0.8f,0.3f,
			 //0.5f-0.45f*m_PositionCoordinate,0.5f-0.5f*m_PositionCoordinate,         
			 m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -m_BorderWidth, 0.0f,
			 0.8f,0.8f,0.8f,0.3f,             
			 // 0.5f+0.5f*m_PositionCoordinate,0.5f-0.45f*m_PositionCoordinate,         
			 m_Width*m_Scale+m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -(m_SlideLineWidthPer*m_Width*0.5f*m_CornerProportion*m_Scale+1.5f*m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,              
			 //0.5f-0.5f*m_PositionCoordinate,0.5f-0.45f*m_PositionCoordinate,
			 m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -(m_SlideLineWidthPer*m_Width*0.5f*m_CornerProportion*m_Scale+1.5f*m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,     
			 //0.5f+0.5f*m_PositionCoordinate,0.5f+0.45f*m_PositionCoordinate,
			 m_Width*m_Scale+m_BorderWidth,m_Height*0.5f+ m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -((m_SlideLineWidthPer*m_Height-m_SlideLineWidthPer*m_Width*m_CornerProportion)*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,    

			 //0.5f-0.5f*m_PositionCoordinate,0.5f+0.45f*m_PositionCoordinate,
			 m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -((m_SlideLineWidthPer*m_Height-m_SlideLineWidthPer*m_Width*m_CornerProportion)*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,         
			 //0.5f+0.45f*m_PositionCoordinate,0.5f+0.5f*m_PositionCoordinate,  
			 m_Width*m_Scale+m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -(m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,      
			 //0.5f-0.5f*m_PositionCoordinate,0.5f+0.5f*m_PositionCoordinate,		 
			 m_BorderWidth, m_Height*0.5f+m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth -(m_SlideLineWidthPer*m_Height*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph 
	  
	};
    short indexBuffer[]  ={0,1,2,3,4,5,6,7};	
    
 
    vertexBufferForArea = new float[8*7];
    indexBufferForArea = new short[8];
    
	for(int i =0;i<8;i++)
	{
		indexBufferForArea[i] = indexBuffer[i];
		vertexBufferForArea[i*7] = vertexBuffer[i*7];
		vertexBufferForArea[i*7 +1] = vertexBuffer[i*7 +1];
		vertexBufferForArea[i*7 +2] = vertexBuffer[i*7 +2];
		vertexBufferForArea[i*7 +3] = vertexBuffer[i*7 +3];
		vertexBufferForArea[i*7 +4] = vertexBuffer[i*7 +4];
		vertexBufferForArea[i*7 +5] = vertexBuffer[i*7 +5];
		vertexBufferForArea[i*7 +6] = vertexBuffer[i*7 +6];
	}  
	Matrix.setIdentityM(modleMatrix1, 0);
	Matrix.translateM(modleMatrix1, 0, m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);		
	for(int i =0;i<16;i++)
		mMVPMatrixForArea[i] = modleMatrix1[i];
 
}

/***********************************************************************************
子函数描述：DrawBackPanel(), 绘制示波器背景（说明文字、单位以及绘制网格刻度）
************************************************************************************/
 void DrawBackPanel(float[] modelMatrix,float[] Boundary){//boolean AnimationEnabled ){
		
		Matrix.setIdentityM(mMVPMatrix, 0);
		Matrix.translateM(mMVPMatrix, 0,  m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);	
		Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
		  
		if (vbo[0] > 0 && ibo[0] > 0) {		
			//GLES20.glDisable(GLES20.GL_BLEND);
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);	 //(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); 
			GLES20.glUseProgram(program[0]);
	
			// Set program handles for cube drawing.
			mvpMatrixUniform = GLES20.glGetUniformLocation(program[0], MVP_MATRIX_UNIFORM);
			GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
			positionAttribute = GLES20.glGetAttribLocation(program[0], POSITION_ATTRIBUTE);
			colorAttribute = GLES20.glGetAttribLocation(program[0], COLOR_ATTRIBUTE);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
			// Bind Attributes
			GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,7*4, 0);
			GLES20.glEnableVertexAttribArray(positionAttribute);			
			
			GLES20.glVertexAttribPointer(colorAttribute, 4, GLES20.GL_FLOAT, false,7*4, 3*4);
			GLES20.glEnableVertexAttribArray(colorAttribute);	

			mBoundary              = Boundary;
			BoundaryHandle          = GLES20.glGetUniformLocation(program[0], "u_Boundary");		
			GLES20.glUniform4fv(BoundaryHandle, 1, mBoundary , 0); 
			GLES20.glEnableVertexAttribArray(BoundaryHandle);	   			
			
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
			
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,4, GLES20.GL_UNSIGNED_SHORT, 0);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);			
			GLES20.glUseProgram(0);
			GLES20.glDisable(GLES20.GL_BLEND);
		}

}
 public void InitGLDataForBackPanel()
 {

	if(m_timer>=1.0f || m_timer<0 ){
		m_timer=1.0f;
	}
	else
		m_timer += 0.2f;
	

	
	if(m_timerX>=0.5f || m_timerX<0 ){
		m_timerX=0.5f;
	}
	else
	{
		if(m_timer ==1.0f)
		{
			m_timerX+=0.1f;
		}
	}
	float 	vertexBuffer[] = {

			//Drag Box
			
			mWindowWidth-mWindowWidth*(1.0f-m_timerX), 0.5f*mWindowHeight-10.0f*(1.1f-m_timer),0.0f,  
			1.0f, 1.0f, 1.0f, 1.0f,		
			mWindowWidth*(1.0f-m_timerX), 0.5f*mWindowHeight-10.0f*(1.1f-m_timer),0.0f,  
			1.0f, 1.0f, 1.0f, 1.0f,			
			
			mWindowWidth-mWindowWidth*(1.0f-m_timerX), 0.5f*mWindowHeight+15.0f*(1.1f-m_timer),0.0f,  
			1.0f, 1.0f, 1.0f, 1.0f,	
			mWindowWidth*(1.0f-m_timerX), 0.5f*mWindowHeight+10.0f*(1.1f-m_timer),0.0f,  
			1.0f, 1.0f, 1.0f, 1.0f,	


	};

    short indexBuffer[] = new short[8];
    for(int i =0;i<4;i++)
    {
    	indexBuffer[i] =(short)i;
    }
		
 	final FloatBuffer VertexDataBuffer = ByteBuffer
 				.allocateDirect(vertexBuffer.length * 4).order(ByteOrder.nativeOrder())
 				.asFloatBuffer();
 	VertexDataBuffer.put(vertexBuffer).position(0);
 		
 	final ShortBuffer IndexDataBuffer = ByteBuffer
 				.allocateDirect(indexBuffer.length * 2).order(ByteOrder.nativeOrder())
 				.asShortBuffer();
 	IndexDataBuffer.put(indexBuffer).position(0);



 	if (vbo[0] > 0 && ibo[0] > 0) {
 			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
 			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VertexDataBuffer.capacity() * 4,
 					VertexDataBuffer, GLES20.GL_STATIC_DRAW);

 			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
 			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
 					* 2, IndexDataBuffer, GLES20.GL_STATIC_DRAW);

 			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
 			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
 	} 
 } 


/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
FlashLight(Context context){
	
	super(context,BUTTON_SQUARE,0.0f,0.0f,1.0f,4.0f); //m_TextRenderList=DEFAULT_CAPTION_DISPLAYLIST;
	 m_ControlType =CONTROL_UNIT_OSCILLO;
	 
	ShaderRelatedInit(m_Context);
}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
FlashLight(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float BorderWith){
	 
	  super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);
	  m_Width=ContrlUnitWidth[ControlType]*m_Scale;
	  m_Height=ContrlUnitHeight[ControlType]*m_Scale;
	  m_ControlType =CONTROL_UNIT_OSCILLO;
	  
	  ShaderRelatedInit(m_Context);

}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/

 public FlashLight(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float Width,float Height,float BorderWith)
{
	super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);	  
	m_Width=Width*m_Scale-2.0f*BorderWith;
	m_Height=Height*m_Scale-2.0f*BorderWith;
	m_ControlType =CONTROL_UNIT_OSCILLO;	

	m_Width=Width*m_Scale;
	m_Height=Height*m_Scale;

	m_DragBoxOffSetX=0;
    m_DragBoxOffSetY=0;
    m_DragBoxHeight = m_Height;
    m_SlideLineWidthPer =0.2f;

    m_DynamicShow =false;
    m_timer       = 0;
    m_timerX      =0;
	ShaderRelatedInit(m_Context);
	

}



void ShaderRelatedInit(Context context){
	
	 program =new int[1];
	String vertexShader1 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_droplist);
	String fragmentShader1 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_area);

	int vertexShaderHandle1 = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader1);
	int fragmentShaderHandle1 = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader1);

	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program[0] = ShaderHelper.createAndLinkProgram(vertexShaderHandle1, fragmentShaderHandle1, new String[] {
			POSITION_ATTRIBUTE,COLOR_ATTRIBUTE});	

		
	GLES20.glGenBuffers(1, vbo, 0);
	GLES20.glGenBuffers(1, ibo, 0);	
	
}
/***********************************************************************************
子函数描述：~OscilloScope(), 注销
************************************************************************************/
/* OscilloScope::~OscilloScope(){
	 //delete m_TextRenderList
     delete []m_tempLabelString;
     delete []m_OriginValueY;//=new float(CurveNum);
  
	 delete []m_RecievedTime;//=new float(DataSize);//[];
	 delete []m_RecievedData;//=new float(CurveNum,DataSize);//[][];

	 delete []m_DataDrawCof;//=new float(CurveNum);
	 delete []m_DisplayDataRange;// = new float(CurveNum);
	 delete []m_MaxRecievedData;//  = new float(CurveNum);
	 delete []m_MinRecievedData;//  = new float(CurveNum);

	 delete []m_UserSelectedStartY;// = new float(CurveNum);
	 delete []m_UserSelectedEndY;// = new float(CurveNum)
	 delete []m_LabelStringY;//=new char(CurveNum,10);
	 delete []m_Color;
}*/
 /***********************************************************************************
子函数描述：IsOnFocus(), 是否获得控制权
************************************************************************************/
@Override
public boolean IsOnFocus(){
    return m_IsOnfocus;
}

public void SetDynamicShowTrue(){
    m_timer       = 0;
    m_timerX      =0;
	m_DynamicShow= true;
}
public void SetDynamicShowFalse(){
	m_DynamicShow=false;
}
public void SetDispWiodowSize(int width, int height)
{
	mWindowWidth = width;
	mWindowHeight = height;
	
	
	  InitGLDataForBorder();
	  InitGLDataForArea();
	 InitGLDataForBackPanel(); // this must be done after WindowWidth  WindowHeight been Set
}
/***********************************************************************************
 子函数描述：AddCaption(), 主题标签
 ************************************************************************************/
public void AddCaption(String TextString)
{
	 m_TextString  = TextString;

}

public void SetDisplayArea(float x1, float x2, float y1, float y2) {

	
	// set color TODO: only alpha component works, text is always black #BUG
	mBoundary[0] = x1; 
	mBoundary[1] = x2; 
	mBoundary[2] = y1; 
	mBoundary[3] = y2; 
	

	//mVPMatrix = pMatrix;		
}	

/***********************************************************************************
 子函数描述：Render(),绘制整个示波器模块
 ************************************************************************************/
public void  Render(float[] modelMatrix,float[] Boundary){  

	InitGLDataForBackPanel(); // this must be done after WindowWidth  WindowHeight been Set
	DrawBackPanel(modelMatrix,Boundary);

 }

public void  RenderFont(float[] modelMatrix,float[] Boundary){    

 	GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	m_Font.SetMvpMatrix(modelMatrix);

	m_Font.SetDisplayArea(Boundary);  
	//m_Font.draw( m_TextString,m_OffSetX+m_Width, m_OffSetY, 0);
	m_Font.draw(m_TextString ,m_OffSetX -(mWindowWidth-m_BorderWidth)/2.0f +m_DragBoxOffSetX, m_OffSetY -(mWindowHeight-m_BorderWidth)/2.0f +m_Height*m_Scale, 0);
    m_Font.RenderFont();	
  	m_Font.RenderFont();
	GLES20.glDisable(GLES20.GL_BLEND);

 }
/***********************************************************************************
 子函数描述：UserKeyInput(),读取用户键盘输入
 ************************************************************************************/
void UserKeyInput(int InputKey){

}

/***********************************************************************************
 子函数描述：UserMouseMove(),鼠标移动事件
 ************************************************************************************/
public void UserMouseMove(int pointerId,float wParam, float lParam){
	
   	int tempMouseX = ( short )( wParam );
	int tempMouseY = ( short )( lParam ); 

		tempMouseX=tempMouseX-(int)m_OffSetX;
		tempMouseY=tempMouseY-(int)m_OffSetY;
	    if (tempMouseY>= 0 && tempMouseY<=m_Height  && tempMouseX>0  && tempMouseX<m_Width){			
	
     		   if(m_IsOnDrag==true){	     			   	
     			   m_DragBoxOffSetX =tempMouseX-m_MouseOffSetY;	     			   	           
     			   if(m_DragBoxOffSetX>m_Width-m_DragBoxHeight)	     			   	     
     				   m_DragBoxOffSetX=m_Width-m_DragBoxHeight	;     

   	               if(m_DragBoxOffSetX<0)
   	             		m_DragBoxOffSetX=0;
	   	
     		   }

	    }
 
}

/***********************************************************************************
 子函数描述：UserMouseDown(),鼠标点击事件
 ************************************************************************************/
public void UserMouseDown(int pointerId,float wParam, float lParam){

		int tempMouseX = ( short )( wParam );
	   	int tempMouseY = ( short )( lParam ); 
		int tempInt;


	  		if(tempMouseX-m_OffSetX<m_Width && tempMouseY-m_OffSetY<m_Height && tempMouseX>m_OffSetX && tempMouseY>m_OffSetY){
	   			 m_IsOnfocus=true; 
	   			 m_IsActive =true; 
	   			 
				 tempMouseX = ( short )tempMouseX-(int)m_OffSetX;
				 
			     if (tempMouseX<m_DragBoxOffSetX+ m_DragBoxHeight && tempMouseX>m_DragBoxOffSetX){

		              m_MouseOffSetY= -m_DragBoxOffSetX + tempMouseX;
				      m_IsOnDrag=true;
				 }	 
	   			 
			}
	  		else{
	   			 m_IsOnfocus=false; 
	   			 m_IsActive =false; 	
	  			
	  		}
               

 }

/***********************************************************************************
 子函数描述：UserMouseUp(),鼠标释放事件
 ************************************************************************************/	 
 public void  UserMouseUp(int pointerId,float wParam, float lParam){
		m_IsOnfocus=false;
		m_IsActive=false;
        m_IsOnDrag=false;
 }

};