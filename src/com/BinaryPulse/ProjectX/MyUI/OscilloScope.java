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

public class OscilloScope extends UIControlUnit {

	protected String m_tempLabelString;
	protected float  m_GraphHeight ;
	protected float  m_GraphWidth ;
	protected float  m_GraphOffsetX; 
	protected float  m_GraphOffsetY; 

	protected int    m_DivNumX; 
	protected int    m_DivNumY; 

	protected float  m_GraphUnitHeight; 
	protected float  m_GraphUnitWidth; 

	protected float  m_GraphLabelWidth;
	protected float  m_GraphLabelHeight;

	protected float   m_ScopeLabelFontList;

	protected String[] m_LabelStringY;//[];
	protected String   m_LabelStringX;
	protected int      m_CurveNum;

	protected float     m_AxisLineWidth;
	protected float     m_GridLineWidth;
	protected boolean   m_GridOn;

	protected int[]    m_Color;
	protected float[]  m_DivValueY;//[];
	protected float    m_DivValueX;

	protected float[]  m_OriginValueY;//[];
	protected float    m_OriginValueX;

	protected float    m_CuveLineWidth;

	protected static int m_DisplayDataStartTimeIndex;
	protected static int m_DisplayDataEndTimeIndex;
	protected static int m_DispStartTimeOffsetIndex;
	protected static int m_DisplayDataMaxTimeIndex;
	protected static int m_LatestTimeIndex;
	protected static int m_TotoalRecieveTimeIndex;
	protected static float m_DisplayDataTimeLength;
	protected static float[] m_RecievedTime;//[];
	protected static float[][] m_RecievedData;//[];//[];
	protected static float[][] m_DataColor;//[];//[];
	protected static float m_TimeDrawCof;
	protected static float[] m_DataDrawCof;//[];
	protected static float[] m_DisplayDataRange;//[];
	protected static float[] m_MaxRecievedData;//[];
	protected static float[] m_MinRecievedData;//[];;
	protected static float[] m_MaxDispData;//[];
	protected static float[] m_MinDispData;//[];;	
	
	protected static float[][]   m_MaxSectionData ;
	protected static float[][]   m_MinSectionData;
	protected static float[]   m_SectionDataStartTime; 
	  
	protected static int m_ActiveMouseX;
	protected static int m_ActiveMouseY;

	protected static int m_ReleaseMouseX;
	protected static int m_ReleaseMouseY;
	
	protected static int m_ActiveMouseX1;
	protected static int m_ActiveMouseY1;

	protected static int m_ReleaseMouseX1;
	protected static int m_ReleaseMouseY1;
	
	protected static float m_UserSelectedStartX;
	protected static float m_UserSelectedEndX;
	protected static float[] m_UserSelectedStartY;//[];
	protected static float[] m_UserSelectedEndY;//[];

	
	protected static float m_UserSelectedStartX1;
	protected static float m_UserSelectedEndX1;
	protected static float[] m_UserSelectedStartY1;//[];
	protected static float[] m_UserSelectedEndY1;//[];
	
	protected static float m_TotalMoveX;
	protected static float m_TotalMoveY;
	protected static float m_TotalScaleX;
	protected static float m_TotalScaleY;	
	
	protected static float m_PreScaleX;
	protected static float m_PreScaleY;	
	
	protected static float m_DataSampleTimeInterval;

	protected boolean m_IsResetAxisX;
	protected boolean m_IsResetAxisY;
	protected boolean m_IsResetAxisXY;
	  protected	 boolean    m_IsOnDrag;

	protected static boolean m_AutomaticDisplay; 
	protected static boolean m_StartRecieve;

	protected static float m_RecievedTimeToRealTimeScale;
	protected static float m_RecievedDataToRealDataScale;

	protected static float m_MaxRecievedTime;
	protected static float m_MinRecievedTime;
	
	protected static int m_RefreshMode;


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
	private int BoundaryHandle;						   // Shader color handle
	/** Identifiers for our uniforms and attributes inside the shaders. */	
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	//private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
	private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";

	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String COLOR_ATTRIBUTE = "a_Color";
	private static final String NORMAL_ATTRIBUTE = "a_Normal";
	private static final String TEXCORD_ATTRIBUTE = "a_TexCoordinate";
	private static final String TEXTURE_UNIFORM = "u_Texture";
	private static final String COLOR_UNIFORM = "u_Color";
	private static final String TEXTUREMOV_UNIFORM = "u_Texmove";

	
	public float[] mBoundary ={1.0f,-1.0f,1.0f,-1.0f};
	
	protected int[] vbo = new int[3];
	protected int[] ibo = new int[3];

	protected int[] vbo_data = new int[5];
	protected int[] ibo_data = new int[5];
	protected int[] vbo_data_realtime = new int[1];
	protected int[] ibo_data_realtime= new int[1] ;	
	
	protected int   m_PerSectionDataLength;
	protected int   m_DynamicSectionIndex;
	protected int   m_RealtimeSectionIndex;
	protected int   m_RealTimeDataLength;
	protected int m_SectionNum;
	
	protected float[] m_RecievedSectionData;
	protected short[] m_RecievedSectionDataIndex;
	//private float[] mVPMatrix;		
	protected float[] mMVPMatrix = new float[16];	
	protected float[] mcolor;
	
	protected MyFont m_FontGraph;

    protected int mWindowWidth;
    protected int mWindowHeight;
    
    protected static float m_timer;
    protected static float m_EffectDataStartIndex[];
    protected static float m_TestData[] = new float[4];
    
    protected boolean m_CmdRunState;

/*##############################################################################
           
		         对象模块功能描述： OscilloScope（示波器）

###############################################################################*/


public void InitGLDataForBorder()
{
	  
    // initiate vertex buffer for border 	
	float zaxis = 0.0f;
	float[] modleMatrix1 =new float[16];
	/*	float vertexBuffer[] = {
      0.48f, 0.0f,
      0.0f, 0.0f,zaxis,	  
	  0.48f, 0.1f,
      m_BorderWidth, m_BorderWidth,zaxis,

	  0.8f, 0.0f,
      m_Width*m_Scale+2*m_BorderWidth, 0.0f ,zaxis,	 
	  0.8f, 0.1f,
      m_Width*m_Scale+m_BorderWidth, m_BorderWidth,zaxis,

	  1.0f, 0.0f,
      m_Width*m_Scale+2*m_BorderWidth, m_Height*m_Scale+2*m_BorderWidth ,zaxis,	  
	  1.0f, 0.1f,
      m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth,zaxis,

	  0.48f, 0.0f,
	  m_Width*m_Scale+2*m_BorderWidth, m_Height*m_Scale+2*m_BorderWidth ,zaxis,	  
	  0.48f, 0.1f,
	  m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth ,zaxis, 	 

	  0.8f, 0.0f,
	  0.0f, m_Height*m_Scale+2*m_BorderWidth,zaxis,	  
	  0.8f, 0.1f,
	  m_BorderWidth, m_Height*m_Scale+m_BorderWidth,zaxis,

	  1.0f, 0.0f,
	  0.0f, 0.0f,zaxis,	  
	  1.0f, 0.1f,
	  m_BorderWidth, m_BorderWidth ,zaxis

	};*/
	
	float vertexBuffer[] = {
      0.48f, 0.0f,
      0.0f, m_Height*m_Scale+m_BorderWidth - 0.0f,zaxis,	  
	  0.48f, 0.1f,
      m_BorderWidth, m_Height*m_Scale+m_BorderWidth -m_BorderWidth,zaxis,

	  0.8f, 0.0f,
      m_Width*m_Scale+2*m_BorderWidth, m_Height*m_Scale+m_BorderWidth - 0.0f ,zaxis,	 
	  0.8f, 0.1f,
      m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth - m_BorderWidth,zaxis,

	  1.0f, 0.0f,
      m_Width*m_Scale+2*m_BorderWidth, m_Height*m_Scale+m_BorderWidth - (m_Height*m_Scale+2*m_BorderWidth) ,zaxis,	  
	  1.0f, 0.1f,
      m_Width*m_Scale+m_BorderWidth,  m_Height*m_Scale+m_BorderWidth - (m_Height*m_Scale+m_BorderWidth),zaxis,

	  0.48f, 0.0f,
	  m_Width*m_Scale+2*m_BorderWidth,  m_Height*m_Scale+m_BorderWidth - (m_Height*m_Scale+2*m_BorderWidth) ,zaxis,	  
	  0.48f, 0.1f,
	  m_Width*m_Scale+m_BorderWidth,  m_Height*m_Scale+m_BorderWidth - (m_Height*m_Scale+m_BorderWidth) ,zaxis, 	 

	  0.8f, 0.0f,
	  0.0f,  m_Height*m_Scale+m_BorderWidth - (m_Height*m_Scale+2*m_BorderWidth),zaxis,	  
	  0.8f, 0.1f,
	  m_BorderWidth,  m_Height*m_Scale+m_BorderWidth - (m_Height*m_Scale+m_BorderWidth),zaxis,

	  1.0f, 0.0f,
	  0.0f,  m_Height*m_Scale+m_BorderWidth - 0.0f,zaxis,	  
	  1.0f, 0.1f,
	  m_BorderWidth,  m_Height*m_Scale+m_BorderWidth - m_BorderWidth ,zaxis

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
	//indexBufferForBorder[16] = indexBuffer[16];
    
	Matrix.setIdentityM(modleMatrix1, 0);
	Matrix.translateM(modleMatrix1, 0, m_OffSetX -m_BorderWidth-m_Width/2.0f, m_OffSetY-m_BorderWidth -m_Height/2.0f, 0);	
	for(int i =0;i<16;i++)
		mMVPMatrixForBorder[i] = modleMatrix1[i];	

}



public void InitGLDataForArea()
{
	float[] modleMatrix1 =new float[16];
    // initiate vertex buffer for border 	
/*	float vertexBuffer[] = {

			 //0.5f+0.5f*m_PositionCoordinate,0.5f-0.5f*m_PositionCoordinate,
			 m_Width*m_Scale+m_BorderWidth, m_BorderWidth, 0.0f,
			 0.0f,0.0f,0.6f,0.5f,  
			 //0.5f-0.45f*m_PositionCoordinate,0.5f-0.5f*m_PositionCoordinate,         
			 m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth, m_BorderWidth, 0.0f,
			 0.0f,0.0f,0.6f,0.5f,               
			 // 0.5f+0.5f*m_PositionCoordinate,0.5f-0.45f*m_PositionCoordinate,         
			 m_Width*m_Scale+m_BorderWidth, m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth, 0.0f,
			 0.0f,0.0f,0.6f,0.5f,               
			 //0.5f-0.5f*m_PositionCoordinate,0.5f-0.45f*m_PositionCoordinate,
			 m_BorderWidth, m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth, 0.0f,
			 0.0f,0.0f,0.6f,0.5f,
			 //0.5f+0.5f*m_PositionCoordinate,0.5f+0.45f*m_PositionCoordinate,
			 m_Width*m_Scale+m_BorderWidth, (m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth, 0.0f,
			 0.0f,0.0f,0.6f,0.5f,    

			 //0.5f-0.5f*m_PositionCoordinate,0.5f+0.45f*m_PositionCoordinate,
			 m_BorderWidth, (m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth, 0.0f,
			 0.0f,0.0f,0.6f,0.5f,	     
			 //0.5f+0.45f*m_PositionCoordinate,0.5f+0.5f*m_PositionCoordinate,  
			 m_Width*(1-m_CornerProportion)*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth, 0.0f,
			 0.6f,0.6f,0.6f,0.3f,	    
			 //0.5f-0.5f*m_PositionCoordinate,0.5f+0.5f*m_PositionCoordinate,		 
			 m_BorderWidth, m_Height*m_Scale+m_BorderWidth, 0.0f,
			 0.6f,0.6f,0.6f,0.3f
	  
	};*/
	float m_AreaColor =0.0f;
	float m_AreaColor1 =0.0f;
	float m_AreaColor2 =0.5f;
	float m_Alph =0.2f;
	float vertexBuffer[] = {

			 //0.5f+0.5f*m_PositionCoordinate,0.5f-0.5f*m_PositionCoordinate,
			 m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth -m_BorderWidth, 0.0f,
			 0.8f,0.8f,0.8f,0.3f,
			 //0.5f-0.45f*m_PositionCoordinate,0.5f-0.5f*m_PositionCoordinate,         
			 m_BorderWidth, m_Height*m_Scale+m_BorderWidth -m_BorderWidth, 0.0f,
			 0.8f,0.8f,0.8f,0.3f,             
			 // 0.5f+0.5f*m_PositionCoordinate,0.5f-0.45f*m_PositionCoordinate,         
			 m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth -(m_Width*0.5f*m_CornerProportion*m_Scale+1.5f*m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,              
			 //0.5f-0.5f*m_PositionCoordinate,0.5f-0.45f*m_PositionCoordinate,
			 m_BorderWidth, m_Height*m_Scale+m_BorderWidth -(m_Width*0.5f*m_CornerProportion*m_Scale+1.5f*m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,     
			 //0.5f+0.5f*m_PositionCoordinate,0.5f+0.45f*m_PositionCoordinate,
			 m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth -((m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,    

			 //0.5f-0.5f*m_PositionCoordinate,0.5f+0.45f*m_PositionCoordinate,
			 m_BorderWidth, m_Height*m_Scale+m_BorderWidth -((m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,         
			 //0.5f+0.45f*m_PositionCoordinate,0.5f+0.5f*m_PositionCoordinate,  
			 m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth -(m_Height*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,      
			 //0.5f-0.5f*m_PositionCoordinate,0.5f+0.5f*m_PositionCoordinate,		 
			 m_BorderWidth, m_Height*m_Scale+m_BorderWidth -(m_Height*m_Scale+m_BorderWidth), 0.0f,
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
	Matrix.translateM(modleMatrix1, 0, m_OffSetX -m_BorderWidth-m_Width/2.0f, m_OffSetY-m_BorderWidth -m_Height/2.0f, 0);	
	for(int i =0;i<16;i++)
		mMVPMatrixForArea[i] = modleMatrix1[i];
    

}

/***********************************************************************************
子函数描述：DrawBackPanel(), 绘制示波器背景（说明文字、单位以及绘制网格刻度）
************************************************************************************/
 void DrawBackPanel(float[] modelMatrix){//boolean AnimationEnabled ){
		
		//float[] color = {0.0f,0.3f, 0.3f, 0.3f};		
		float[] color = {0.5f,0.5f, 0.5f, 0.3f};		

		
		Matrix.setIdentityM(mMVPMatrix, 0);
		Matrix.translateM(mMVPMatrix, 0, m_OffSetX, m_OffSetY, 0);	
		Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
		
		  if(m_IntervalCoordinate<0.48f && m_IntervalCoordinate>=0f)

		       m_IntervalCoordinate+=0.001f;
	     else
	          m_IntervalCoordinate=0.0f;  
		  
		if (vbo[0] > 0 && ibo[0] > 0) {		
			//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			GLES20.glUseProgram(program[0]);
			ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);	        
			GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
			GLES20.glEnableVertexAttribArray(ColorHandle);
			
			BoundaryHandle          = GLES20.glGetUniformLocation(program[0], "u_Boundary");		
			GLES20.glUniform4fv(BoundaryHandle, 1, mBoundary , 0); 
			GLES20.glEnableVertexAttribArray(BoundaryHandle);	
			
			// Set program handles for cube drawing.
			mvpMatrixUniform = GLES20.glGetUniformLocation(program[0], MVP_MATRIX_UNIFORM);
			GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
			positionAttribute = GLES20.glGetAttribLocation(program[0], POSITION_ATTRIBUTE);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
			// Bind Attributes
			GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,3*4, 0);
			GLES20.glEnableVertexAttribArray(positionAttribute);
		
			GLES20.glLineWidth(3.0f);
			// Draw
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
			GLES20.glDrawElements(GLES20.GL_LINE_LOOP, (5), GLES20.GL_UNSIGNED_SHORT, 0);
			GLES20.glDrawElements(GLES20.GL_LINES,  (m_DivNumY)*2, GLES20.GL_UNSIGNED_SHORT, 5*2);	
			GLES20.glDrawElements(GLES20.GL_LINES,  (m_DivNumX)*2, GLES20.GL_UNSIGNED_SHORT, 5*2 + (m_DivNumY)*4);
			
			ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);
			GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
			GLES20.glLineWidth(1.0f);
			GLES20.glDrawElements(GLES20.GL_LINES,  (m_DivNumY)*2, GLES20.GL_UNSIGNED_SHORT, 5*2 + (m_DivNumY+m_DivNumX)*4);
			GLES20.glDrawElements(GLES20.GL_LINES,  (m_DivNumX)*2, GLES20.GL_UNSIGNED_SHORT, 5*2 + (2*m_DivNumY+m_DivNumX)*4);

			for(int i =0;i<m_CurveNum;i++)
			{	
				ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);
				GLES20.glUniform4fv(ColorHandle, 1, m_DataColor[i] , 0); 
				GLES20.glDrawElements(GLES20.GL_LINES, (4+m_DivNumY*2), GLES20.GL_UNSIGNED_SHORT, ((m_DivNumY+m_DivNumX)*4 +5 +(4*i+m_DivNumY*2*i))*2);
			}

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);			
			GLES20.glUseProgram(0);
		}

}
 
 /***********************************************************************************
 子函数描述：DrawBackPanel(), 绘制示波器背景（说明文字、单位以及绘制网格刻度）
 ************************************************************************************/
  void DrawDataBackground(float[] modelMatrix){//boolean AnimationEnabled ){
 		
 		float[] color = {0.0f,0.0f, 0.0f, 0.3f};		
 		
 		Matrix.setIdentityM(mMVPMatrix, 0);
 		Matrix.translateM(mMVPMatrix, 0, m_OffSetX, m_OffSetY, 0);	
 		Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
 		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);	 
 		  
 		if (vbo[0] > 0 && ibo[0] > 0) {		
 			//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
 			GLES20.glUseProgram(program[0]);
 			ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);	        
 			GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
 			GLES20.glEnableVertexAttribArray(ColorHandle);
 
			BoundaryHandle          = GLES20.glGetUniformLocation(program[0], "u_Boundary");		
			GLES20.glUniform4fv(BoundaryHandle, 1, mBoundary , 0); 
			GLES20.glEnableVertexAttribArray(BoundaryHandle);				
 			
 			// Set program handles for cube drawing.
 			mvpMatrixUniform = GLES20.glGetUniformLocation(program[0], MVP_MATRIX_UNIFORM);
 			GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
 			positionAttribute = GLES20.glGetAttribLocation(program[0], POSITION_ATTRIBUTE);
 			
 			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[2]);
 			// Bind Attributes
 			GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,3*4, 0);
 			GLES20.glEnableVertexAttribArray(positionAttribute);
 		
 			//GLES20.glLineWidth(3.0f);
 			// Draw
 			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[2]);
 			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, (4), GLES20.GL_UNSIGNED_SHORT, 0);

 			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
 			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);			
 			GLES20.glUseProgram(0);
 			GLES20.glDisable(GLES20.GL_BLEND);
 		}

 }
  public void InitGLDataForDataBackground()
  {

 	float 	vertexBuffer[] = {
	
 		 	m_GraphOffsetX,m_GraphOffsetY,0.0f,
 		 	m_GraphOffsetX,m_GraphOffsetY+m_GraphHeight,0.0f,
 		 	m_GraphOffsetX+m_GraphWidth,m_GraphOffsetY,0.0f,
 		 	m_GraphOffsetX+m_GraphWidth,m_GraphOffsetY+m_GraphHeight,0.0f,
 	};

     short indexBuffer[] = new short[6];
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



  	if (vbo[2] > 0 && ibo[2] > 0) {
  			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[2]);
  			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VertexDataBuffer.capacity() * 4,
  					VertexDataBuffer, GLES20.GL_STATIC_DRAW);

  			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[2]);
  			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
  					* 2, IndexDataBuffer, GLES20.GL_STATIC_DRAW);

  			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
  			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
  	} 
  } 
  
 public void InitGLDataForBackPanel()
 {
 	  
     // initiate vertex buffer for border 	
	int i,j,k;
	float vertexBuffer[] =new float[(5 + (m_DivNumY+m_DivNumX)*4 + m_CurveNum*(4+m_DivNumY*2))*3];	 
    short indexBuffer[]  =new short[(5 + (m_DivNumY+m_DivNumX)*4 + m_CurveNum*(4+m_DivNumY*2))*3];

 	vertexBuffer[0] = m_GraphOffsetX;
 	vertexBuffer[1] = m_GraphOffsetY;
 	vertexBuffer[2] = 0.0f;
 	vertexBuffer[3] = m_GraphOffsetX;
 	vertexBuffer[4] = m_GraphOffsetY+m_GraphHeight;
 	vertexBuffer[5] =  0.0f;
 	vertexBuffer[6] = m_GraphOffsetX+m_GraphWidth;
 	vertexBuffer[7] = m_GraphOffsetY+m_GraphHeight;
 	vertexBuffer[8] = 0.0f;
 	vertexBuffer[9] = m_GraphOffsetX+m_GraphWidth;
 	vertexBuffer[10] = m_GraphOffsetY;
 	vertexBuffer[11] = 0.0f;
 	vertexBuffer[12] = m_GraphOffsetX;
 	vertexBuffer[13] = m_GraphOffsetY;
 	vertexBuffer[14] = 0.0f;          
    // length =15;
	for( i =0;i<m_DivNumY;i++){			
		  vertexBuffer[15+6*i] = m_GraphOffsetX;
		  vertexBuffer[16+6*i] =m_GraphOffsetY+m_GraphUnitHeight*i;
		  vertexBuffer[17+6*i] =0.0f;
		  vertexBuffer[18+6*i] =m_GraphOffsetX+15;
		  vertexBuffer[19+6*i] =m_GraphOffsetY+m_GraphUnitHeight*i;
		  vertexBuffer[20+6*i] =0.0f;
	 }
	
	 for(i =0;i<m_DivNumX;i++){
		 
		  vertexBuffer[21+6*(m_DivNumY -1 +i)] = m_GraphOffsetX+m_GraphUnitWidth*(i+1);
		  vertexBuffer[22+6*(m_DivNumY -1 +i)] = m_GraphOffsetY;//+m_GraphHeight;
		  vertexBuffer[23+6*(m_DivNumY -1 +i)] =0.0f;
		  vertexBuffer[24+6*(m_DivNumY -1 +i)] =m_GraphOffsetX+m_GraphUnitWidth*(i+1);
		  vertexBuffer[25+6*(m_DivNumY -1 +i)] =m_GraphOffsetY+15;//+m_GraphHeight-15;
		  vertexBuffer[26+6*(m_DivNumY -1 +i)] =0.0f;		 

	 }	
     //length = 15 +6*(m_DivNumY+m_DivNumX)
	 
		for( i =0;i<m_DivNumY;i++){			
			  vertexBuffer[15 +6*(m_DivNumY+m_DivNumX)+6*i] = m_GraphOffsetX;
			  vertexBuffer[16 +6*(m_DivNumY+m_DivNumX)+6*i] =m_GraphOffsetY+m_GraphUnitHeight*i;
			  vertexBuffer[17 +6*(m_DivNumY+m_DivNumX)+6*i] =0.0f;
			  vertexBuffer[18 +6*(m_DivNumY+m_DivNumX)+6*i] =m_GraphOffsetX+m_GraphWidth;
			  vertexBuffer[19 +6*(m_DivNumY+m_DivNumX)+6*i] =m_GraphOffsetY+m_GraphUnitHeight*i;
			  vertexBuffer[20 +6*(m_DivNumY+m_DivNumX)+6*i] =0.0f;
		 }
		
		 for(i =0;i<m_DivNumX;i++){			 
			  vertexBuffer[15 +6*(2*m_DivNumY+m_DivNumX)+6*i] = m_GraphOffsetX+m_GraphUnitWidth*(i+1);
			  vertexBuffer[16 +6*(2*m_DivNumY+m_DivNumX)+6*i] =m_GraphOffsetY+m_GraphHeight;
			  vertexBuffer[17 +6*(2*m_DivNumY+m_DivNumX)+6*i] =0.0f;
			  vertexBuffer[18 +6*(2*m_DivNumY+m_DivNumX)+6*i] =m_GraphOffsetX+m_GraphUnitWidth*(i+1);
			  vertexBuffer[19 +6*(2*m_DivNumY+m_DivNumX)+6*i] =m_GraphOffsetY;
			  vertexBuffer[20 +6*(2*m_DivNumY+m_DivNumX)+6*i] =0.0f;
		  }	
		  //length = 15 +12*(m_DivNumY+m_DivNumX)	 
	  
        k=0;
		
		for(i =0;i<m_CurveNum;i++){
			if(i<2){
			  vertexBuffer[15 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] = m_GraphOffsetX-(i+1f)*m_GraphLabelWidth;
			  vertexBuffer[16 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] =m_GraphOffsetY;
			  vertexBuffer[17 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] =0.0f;	 
			  vertexBuffer[18 +12*(m_DivNumY+m_DivNumX) +i*(6+k*6)] = m_GraphOffsetX-(i+1f)*m_GraphLabelWidth;
			  vertexBuffer[19 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] =m_GraphOffsetY+m_GraphHeight;
			  vertexBuffer[20 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] =0.0f;	 
			}
			else{
			  vertexBuffer[15 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] = m_GraphOffsetX+ m_GraphWidth*m_Scale+((i-2)+0.1f)*m_GraphLabelWidth;
			  vertexBuffer[16 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] =m_GraphOffsetY;
			  vertexBuffer[17 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] =0.0f;	 
			  vertexBuffer[18 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] = m_GraphOffsetX+m_GraphWidth*m_Scale+((i-2)+0.1f)*m_GraphLabelWidth;
			  vertexBuffer[19 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] =m_GraphOffsetY+m_GraphHeight;
			  vertexBuffer[20 +12*(m_DivNumY+m_DivNumX) + i*(6+k*6)] =0.0f;	
			}
			
			for(j =0;j<m_DivNumY+1;j++){		 
				 
				//for(k =0;i<4;i++){
				 if(i<2){
					 vertexBuffer[15 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] = m_GraphOffsetX-(i+1)*m_GraphLabelWidth;
					 vertexBuffer[16 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] = m_GraphOffsetY+m_GraphUnitHeight*j;
					 vertexBuffer[17 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] =0.0f;	 
					 vertexBuffer[18 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] = m_GraphOffsetX-(i+1)*m_GraphLabelWidth+10;
					 vertexBuffer[19 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] =m_GraphOffsetY+m_GraphUnitHeight*j;
					 vertexBuffer[20 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] =0.0f;	 
				 }
				 else{
					 vertexBuffer[15 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] = m_GraphOffsetX+ m_GraphWidth*m_Scale+((i-2)+0.1f)*m_GraphLabelWidth;;
					 vertexBuffer[16 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] =m_GraphOffsetY+m_GraphUnitHeight*j;
					 vertexBuffer[17 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] =0.0f;	 
					 vertexBuffer[18 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] =m_GraphOffsetX+ m_GraphWidth*m_Scale+((i-2)+0.1f)*m_GraphLabelWidth+10;
					 vertexBuffer[19 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] =m_GraphOffsetY+m_GraphUnitHeight*j;
					 vertexBuffer[20 +12*(m_DivNumY+m_DivNumX)  + 6 + i*(12+m_DivNumY*6) +j*6] =0.0f;	 
				 }
				//}
			}	
			k=j;//*(m_DivNumY+1);
			
		}

	for( i =0;i<(5 + (m_DivNumY+m_DivNumX)*4 + m_CurveNum*(4+m_DivNumY*2))*3;i++){	
		indexBuffer[i] = (short)i;
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
 
 public void DrawLables(float[] modelMatrix)
 {      
	  	int i,j;
	  	float x,y,z;
	  	 String s;
		GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_CULL_FACE);
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		// TEST: render some strings with the font
		m_FontGraph.SetMvpMatrix(modelMatrix);
		m_FontGraph.SetDisplayArea(mBoundary);  
        DecimalFormat format = new DecimalFormat("#.00");
		
		for(i =0;i<m_CurveNum;i++){
			//GLES20.glUniform4fv(ColorHandle, 1, m_DataColor[i] , 0); 
			m_FontGraph.SetColor(  m_DataColor[i][0],  m_DataColor[i][1],  m_DataColor[i][2],  m_DataColor[i][3]);  
			if(i<2){
			  x = m_GraphOffsetX-(i+0.9f)*m_GraphLabelWidth;
			  z =0.0f;	 
			}
			else{
			  x = m_GraphOffsetX+ m_GraphWidth*m_Scale+((i-2)+0.2f)*m_GraphLabelWidth;
			  z = 0.0f;	
			}
			
			for(j =0;j<m_DivNumY+1;j++){		 
				if(i<2){
					 y =m_GraphOffsetY+m_GraphUnitHeight*(j+0.0f);

				 }
				 else{
					
					 y = m_GraphOffsetY+m_GraphUnitHeight*(j+0.0f);
	 
				 }			        
			     s = format.format((float)(m_OriginValueY[i]+(j)*m_DivValueY[i])); //转换成字符串
			     m_FontGraph.draw( s , x+m_OffSetX,y+m_OffSetY, z); 
			}	
			m_FontGraph.RenderFont();
		}
		m_FontGraph.SetColor( 0.0f, 1.0f, 1.0f, 1.0f );  
		for(j=0; j<=m_DivNumX; j++){			
	         //glWindowPos2i(m_OffSetX+m_GraphOffsetX+m_GraphUnitWidth*j-m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-m_GraphOffsetY-m_GraphHeight-m_GraphLabelHeight-m_OffSetY);
		      s = format.format((float)(m_OriginValueX+(j)*m_DivValueX)); //转换成字符串
		      m_FontGraph.draw( s  , m_GraphOffsetX+m_GraphUnitWidth*j-m_GraphLabelWidth*0.5f+m_OffSetX,m_GraphOffsetY-18.0f+m_OffSetY, 0); 
			  
       }
		m_FontGraph.RenderFont();
		GLES20.glDisable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
 }
/***********************************************************************************
子函数描述：DrawData(), 绘制示波器背景（说明文字、单位以及绘制网格刻度）
************************************************************************************/

 void DrawData(float[] modelMatrix){

  int i,j;
   float[] DataBoundary ={(2*m_OffSetX+2*m_GraphWidth+2*m_GraphOffsetX)/mWindowWidth, (2*m_OffSetX+2*m_GraphOffsetX)/mWindowWidth ,(2*m_OffSetY+2*m_GraphOffsetY+2*m_GraphHeight)/mWindowHeight, (2*m_OffSetY+2*m_GraphOffsetY)/mWindowHeight };
 //Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
   
 if (vbo_data_realtime[0] > 0 && ibo_data_realtime[0] > 0) {		
 	//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
 	GLES20.glUseProgram(program[0]);
 	ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);
 	
 	BoundaryHandle          = GLES20.glGetUniformLocation(program[0], "u_Boundary");		
 	GLES20.glUniform4fv(BoundaryHandle, 1, DataBoundary , 0); 
 	GLES20.glEnableVertexAttribArray(BoundaryHandle);		
 	
 	// Set program handles for cube drawing.


 	m_DataColor[0] = new float[]{1.0f, 0.0f, 0.0f,1.0f};
 	m_DataColor[1] = new float[]{0.0f, 1.0f, 0.0f,1.0f};
 	m_DataColor[2] = new float[]{1.0f, 0.0f, 1.0f,1.0f};
     m_DataColor[3] = new float[]{1.0f, 1.0f, 0.0f,1.0f};

 	for(i =0; i<m_CurveNum;i++){
 		ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);
 		GLES20.glUniform4fv(ColorHandle, 1, m_DataColor[i] , 0); 
 		GLES20.glEnableVertexAttribArray(ColorHandle);		
 		//if(m_RefreshMode == 0)
 		//	GLES20.glDrawElements(GLES20.GL_LINE_STRIP, ((m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*2 -(int)m_EffectDataStartIndex[i]), GLES20.GL_UNSIGNED_SHORT,(int)m_EffectDataStartIndex[i]*2 + (m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*4*i);
 		//else
 		//	GLES20.glDrawElements(GLES20.GL_LINE_STRIP, ((m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)), GLES20.GL_UNSIGNED_SHORT, (m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*2*i);		
 			//GLES20.glDrawElements(GLES20.GL_LINE_STRIP, ((m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*2), GLES20.GL_UNSIGNED_SHORT,(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*2);
		
 		 Matrix.setIdentityM(mMVPMatrix, 0);

 		 Matrix.translateM(mMVPMatrix, 0, m_GraphOffsetX-m_OriginValueX*m_TimeDrawCof, m_GraphOffsetY-m_DataDrawCof[i]*m_OriginValueY[i], 0);	
 		 Matrix.translateM(mMVPMatrix, 0, m_OffSetX, m_OffSetY, 0);	
  		Matrix.scaleM(mMVPMatrix, 0, m_TimeDrawCof, m_DataDrawCof[i], 0);
 		 Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
 		 
 	 	mvpMatrixUniform = GLES20.glGetUniformLocation(program[0], MVP_MATRIX_UNIFORM);
 	 	GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
 		
 		positionAttribute = GLES20.glGetAttribLocation(program[0], POSITION_ATTRIBUTE);	
 		for(j=0;j<=m_RealtimeSectionIndex;j++)
 		{

 		 	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_data[j]); 		 	
 		 	// Bind Attributes
 		 	GLES20.glVertexAttribPointer(positionAttribute, 2, GLES20.GL_FLOAT, false,
 		 			2*4, 0);
 		 	GLES20.glEnableVertexAttribArray(positionAttribute);

 		 	GLES20.glLineWidth(1.0f);
 		 	// Draw
 		 	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_data[j]);
 		 	if(m_SectionNum -1 == m_RealtimeSectionIndex){
 		 		if(m_DynamicSectionIndex == m_SectionNum -1 && j==0)
 		 			GLES20.glDrawElements(GLES20.GL_LINE_STRIP, 2*m_PerSectionDataLength-m_RealTimeDataLength, GLES20.GL_UNSIGNED_SHORT,(i)*4*m_PerSectionDataLength+2*m_RealTimeDataLength);
 		 		else if(m_DynamicSectionIndex != m_SectionNum -1 && j==m_DynamicSectionIndex+1)
 		 			GLES20.glDrawElements(GLES20.GL_LINE_STRIP,  2*m_PerSectionDataLength-m_RealTimeDataLength, GLES20.GL_UNSIGNED_SHORT,(i)*4*m_PerSectionDataLength+2*m_RealTimeDataLength);
 		 		else
 		 			GLES20.glDrawElements(GLES20.GL_LINE_STRIP, 2*m_PerSectionDataLength, GLES20.GL_UNSIGNED_SHORT,i*4*m_PerSectionDataLength );
 		 	} 		 	
 		 	else
 		 		GLES20.glDrawElements(GLES20.GL_LINE_STRIP, 2*m_PerSectionDataLength, GLES20.GL_UNSIGNED_SHORT,i*4*m_PerSectionDataLength );
 		}


 		 	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_data_realtime[0]); 		 	
 		 	// Bind Attributes
 		 	GLES20.glVertexAttribPointer(positionAttribute, 2, GLES20.GL_FLOAT, false,
 		 			2*4, 0);
 		 	GLES20.glEnableVertexAttribArray(positionAttribute);

 		 	GLES20.glLineWidth(1.0f);
 		 	// Draw
 		 	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_data_realtime[0]);
 		 	GLES20.glDrawElements(GLES20.GL_LINE_STRIP, m_RealTimeDataLength, GLES20.GL_UNSIGNED_SHORT,i*4*m_PerSectionDataLength );
 	}	
 	
 	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
 	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);	
 	GLES20.glUseProgram(0);
 }

 }
/***********************************************************************************
子函数描述：RefreshDisplay(), 刷新示波器显示（X,Y轴坐标以及绘制的曲线动态刷新）
************************************************************************************/
public void RefreshDisplay(){
 
    int i;
    float tempresult1,tempresult2;
    float displayDivY;
	if(m_AutomaticDisplay==true){

		// m_DivValueY,m_OriginValueY m_DivValueY m_OriginValueX are changing dynamicly;	
	    for(i=0;i<m_CurveNum;i++){
			if(m_MaxRecievedData[i]!=m_MinRecievedData[i])		    
		        m_DisplayDataRange[i]=(m_MaxRecievedData[i]-m_MinRecievedData[i]);

		    /*m_OriginValueY[i]=m_MinRecievedData[i];
			m_DataDrawCof[i]=m_GraphHeight/m_DisplayDataRange[i];
			m_DivValueY[i]=m_DisplayDataRange[i]/m_DivNumY;	*/
		
			tempresult1 = Math.max(Math.abs(m_MaxRecievedData[i]), Math.abs(m_MinRecievedData[i]))*2.0f/m_DivNumY;// m_DisplayDataRange[i]*2.0f/m_DivNumY;
			
			displayDivY = 100000;
			
			for(int j=0;j<9;j++)
			{
				tempresult2 = (float)Math.pow(10, j);
				
				for(int k=0;k<9;k++)
				{
					/*if(tempresult1>=(k*10000.0f+10000.0f)/tempresult2 && tempresult1<((k+1)*10000.0f+5000.0f)/tempresult2)
					{
						displayDivY = ((k+1)*10000.0f+5000.0f)/tempresult2;
						k=9;
						j=9;
					}
					if(tempresult1>=((k+1)*10000.0f+5000.0f)/tempresult2 && tempresult1<((k+1)*10000.0f+10000.0f)/tempresult2)
					{
						displayDivY = ((k+1)*10000.0f+10000.0f)/tempresult2;
						k=9;
						j=9;
					}*/
					if(tempresult1>=((k+1)*10000.0f+5000.0f)/tempresult2 && tempresult1<((k+1)*10000.0f+10000.0f)/tempresult2)
					{
						displayDivY = ((k+1)*10000.0f+10000.0f)/tempresult2;
						k=9;
						j=9;
					}					
					else if(tempresult1>=(k*10000.0f+10000.0f)/tempresult2 && tempresult1<((k+1)*10000.0f+5000.0f)/tempresult2)
					{
						displayDivY = ((k+1)*10000.0f+5000.0f)/tempresult2;
						k=9;
						j=9;
					}
					else
					{
						displayDivY = ((k+1)*10000.0f)/tempresult2;
					}

				}
	
				
			}

		
			m_OriginValueY[i] = -(displayDivY*m_DivNumY)/2.0f;  
			m_DivValueY[i]    = displayDivY;
			 m_DisplayDataRange[i] = displayDivY*m_DivNumY;
			m_DataDrawCof[i]=m_GraphHeight/m_DisplayDataRange[i];
	    }
	    

	    
	    
	    
        m_DisplayDataEndTimeIndex=m_LatestTimeIndex;

		if(m_RefreshMode==1 ){				
           if(m_MaxRecievedTime!=m_MinRecievedTime)
				m_DisplayDataTimeLength=(m_MaxRecievedTime-m_MinRecievedTime);
           m_OriginValueX =m_MinRecievedTime;	
		}
		else
		   m_OriginValueX=m_RecievedTime[m_DisplayDataEndTimeIndex]-m_DisplayDataTimeLength;

        m_DisplayDataStartTimeIndex=m_DisplayDataEndTimeIndex-(int)(m_DisplayDataTimeLength/m_DataSampleTimeInterval);
		m_TimeDrawCof=m_GraphWidth/m_DisplayDataTimeLength;
		m_DivValueX=m_DisplayDataTimeLength/m_DivNumX;

		if(m_DisplayDataStartTimeIndex<0)
			m_DisplayDataStartTimeIndex=0;

		if(m_DisplayDataEndTimeIndex<0)
			m_DisplayDataEndTimeIndex=0;
	
	}
	else{

	    for(i=0;i<m_CurveNum;i++){ 
		    
		    //m_DisplayDataRange[i] is determined by User 
		    //m_OriginValueY[i] is determined by User 
			m_DataDrawCof[i]=m_GraphHeight/m_DisplayDataRange[i];
			m_DivValueY[i]=m_DisplayDataRange[i]/m_DivNumY;		

		}
		//m_OriginValueX is determined by User 
		//m_DisplayDataTimeLength  is determined by User
		m_DisplayDataEndTimeIndex=m_LatestTimeIndex;//(int)((m_OriginValueX+m_DisplayDataTimeLength)/m_DataSampleTimeInterval);
        m_DisplayDataStartTimeIndex=0;//(int)(m_OriginValueX/m_DataSampleTimeInterval);
		m_TimeDrawCof=m_GraphWidth/m_DisplayDataTimeLength;
		if(m_DisplayDataStartTimeIndex<0)
			m_DisplayDataStartTimeIndex=0;
		if(m_DisplayDataEndTimeIndex<0)
			m_DisplayDataEndTimeIndex=0;	  
		if(m_DisplayDataEndTimeIndex>=m_LatestTimeIndex)
			m_DisplayDataEndTimeIndex=m_LatestTimeIndex;	
		m_DivValueX=m_DisplayDataTimeLength/m_DivNumX;	    
	}
	
}

/***********************************************************************************
子函数描述：ReciedveData(), 接受数据（X,Y轴坐标值）
************************************************************************************/

public void ReciedveData(float Time, float[] Data){

    int i,j;
    
    if(m_CmdRunState ==false)
    {
    	return;
    }
    
	if(m_StartRecieve==false){
		m_LatestTimeIndex=0;
		m_MinRecievedTime=m_MaxRecievedTime=m_RecievedTime[m_LatestTimeIndex]=m_RecievedTimeToRealTimeScale*Time;
		
		m_DispStartTimeOffsetIndex = (int)Time;
		
		for(i=0;i<m_CurveNum;i++){ 				

			m_RecievedSectionData[i*m_PerSectionDataLength*4+4*m_LatestTimeIndex]= m_RecievedTime[m_LatestTimeIndex];
			m_RecievedSectionData[i*m_PerSectionDataLength*4+4*m_LatestTimeIndex+1]= m_RecievedDataToRealDataScale*Data[i];
			m_RecievedSectionData[i*m_PerSectionDataLength*4+4*m_LatestTimeIndex+2]= m_RecievedTime[m_LatestTimeIndex];
			m_RecievedSectionData[i*m_PerSectionDataLength*4+4*m_LatestTimeIndex+3]= m_RecievedDataToRealDataScale*Data[i];
			
			m_MaxRecievedData[i]=m_RecievedDataToRealDataScale*Data[i];// Data[i];
			m_MinRecievedData[i]=m_RecievedDataToRealDataScale*Data[i];//Data[i];
			
			
			m_RecievedData[i][0] =m_RecievedDataToRealDataScale*Data[i];
			/*
			for(j=0;j<m_SectionNum;j++){
			  m_MaxSectionData[j][i]  = m_MaxRecievedData[i];
			  m_MinSectionData[j][i]  = m_MaxRecievedData[i];
			  m_SectionDataStartTime[j]  =m_MinRecievedTime;
			}*/
		}
		m_StartRecieve=true;
	}
	else{

		if(m_LatestTimeIndex<m_PerSectionDataLength-1){
			
		    m_LatestTimeIndex++;
		    
		    m_RealTimeDataLength =2*m_LatestTimeIndex;
		    final FloatBuffer VertexDataBuffer = ByteBuffer
		    			.allocateDirect(m_RecievedSectionData.length * 4).order(ByteOrder.nativeOrder())
		    			.asFloatBuffer();
		    VertexDataBuffer.put(m_RecievedSectionData).position(0);
		    	
		    final ShortBuffer IndexDataBuffer = ByteBuffer
		    			.allocateDirect(m_RecievedSectionDataIndex.length * 2).order(ByteOrder.nativeOrder())
		    			.asShortBuffer();
		    IndexDataBuffer.put(m_RecievedSectionDataIndex).position(0);


		    if (vbo_data_realtime[0] > 0 && ibo_data_realtime[0] > 0) {
		    		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_data_realtime[0]);
		    		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VertexDataBuffer.capacity() * 4,
		    				VertexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		    		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_data_realtime[0]);
		    		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
		    				* 2, IndexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		    		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		    		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);	    
		    
		    }   
		    
		    
		}
		else{
		    m_LatestTimeIndex=0;//m_DisplayDataMaxTimeIndex;
		    //for(i=0;i<m_DisplayDataMaxTimeIndex;i++){ 
			//    for(j=0;j<m_CurveNum;j++)
            //         m_RecievedData[j][i]= m_RecievedData[j][i+1];
			//	m_RecievedTime[i]= m_RecievedTime[i+1];
				
			//}
		    // m_RecievedTime[m_LatestTimeIndex]=m_RecievedTimeToRealTimeScale*Time;
			// m_RecievedData[j][m_LatestTimeIndex]=m_RecievedDataToRealDataScale*Data[i];		
		    
			  //m_PerSectionDataLength;
			  //m_DynamicSectionIndex ; 
			  //
		    

		    final FloatBuffer VertexDataBuffer = ByteBuffer
		    			.allocateDirect(m_RecievedSectionData.length * 4).order(ByteOrder.nativeOrder())
		    			.asFloatBuffer();
		    VertexDataBuffer.put(m_RecievedSectionData).position(0);
		    	
		    final ShortBuffer IndexDataBuffer = ByteBuffer
		    			.allocateDirect(m_RecievedSectionDataIndex.length * 2).order(ByteOrder.nativeOrder())
		    			.asShortBuffer();
		    IndexDataBuffer.put(m_RecievedSectionDataIndex).position(0);

		    if(m_DynamicSectionIndex<m_SectionNum -1)
		    {
		    	m_DynamicSectionIndex++;
			    if(m_RealtimeSectionIndex< m_SectionNum-1)
			    	m_RealtimeSectionIndex ++;
		    }
		    else
		    {
		    	m_DynamicSectionIndex =0;
		    }
    

		    
		    
		    if (vbo_data[m_DynamicSectionIndex] > 0 && ibo_data[m_DynamicSectionIndex] > 0) {
		    		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo_data[m_DynamicSectionIndex]);
		    		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VertexDataBuffer.capacity() * 4,
		    				VertexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		    		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo_data[m_DynamicSectionIndex]);
		    		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
		    				* 2, IndexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		    		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		    		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);	    
		    
		    }
		
		  }
		
        m_RecievedTime[m_LatestTimeIndex]=m_RecievedTimeToRealTimeScale*Time;
       /* if(m_LatestTimeIndex ==0)
        {
        	m_SectionDataStartTime[m_DynamicSectionIndex]  = m_RecievedTime[m_LatestTimeIndex];
        	for(i=0;i<m_CurveNum;i++){
			  m_MaxSectionData[m_DynamicSectionIndex][i]  =  m_RecievedDataToRealDataScale*Data[i];
			  m_MinSectionData[m_DynamicSectionIndex][i]  =  m_RecievedDataToRealDataScale*Data[i];
        	}
        }*/
        

        
	    if(m_MaxRecievedTime<m_RecievedTime[m_LatestTimeIndex])
			 m_MaxRecievedTime=m_RecievedTime[m_LatestTimeIndex];

		if(m_MinRecievedTime>m_RecievedTime[m_LatestTimeIndex])
			 m_MinRecievedTime=m_RecievedTime[m_LatestTimeIndex];

		
		for(i=0;i<m_CurveNum;i++){ 	
	        //m_RecievedData[i][m_LatestTimeIndex]=m_RecievedDataToRealDataScale*Data[i];
	        //m_RecievedSectionData[i*m_PerSectionDataLength*4 +4*m_LatestTimeIndex]= m_RecievedTime[m_LatestTimeIndex];
			if(m_TotoalRecieveTimeIndex<=m_DisplayDataMaxTimeIndex)
				m_RecievedData[i][m_TotoalRecieveTimeIndex] =m_RecievedDataToRealDataScale*Data[i];
			else
			{
				
				for(j=0;j<m_DisplayDataMaxTimeIndex;j++)
					m_RecievedData[i][j]=m_RecievedData[i][j+1];
				
				m_RecievedData[i][m_DisplayDataMaxTimeIndex] =m_RecievedDataToRealDataScale*Data[i];
				
			}
			
			if(m_LatestTimeIndex>=1){
				m_RecievedSectionData[i*m_PerSectionDataLength*4+4*m_LatestTimeIndex+1]= m_RecievedSectionData[i*m_PerSectionDataLength*4+4*(m_LatestTimeIndex-1)+3];
				m_RecievedSectionData[i*m_PerSectionDataLength*4 +4*m_LatestTimeIndex]= m_RecievedTime[m_LatestTimeIndex];
			}
			else{
				m_RecievedSectionData[i*m_PerSectionDataLength*4+4*m_LatestTimeIndex+1]= m_RecievedSectionData[(i+1)*m_PerSectionDataLength*4-1];//m_RecievedDataToRealDataScale*Data[i];
				m_RecievedSectionData[i*m_PerSectionDataLength*4 +4*m_LatestTimeIndex]= m_RecievedTime[m_PerSectionDataLength-1];
			}
			m_RecievedSectionData[i*m_PerSectionDataLength*4+4*m_LatestTimeIndex+2]= m_RecievedTime[m_LatestTimeIndex];
			m_RecievedSectionData[i*m_PerSectionDataLength*4+4*m_LatestTimeIndex+3]= m_RecievedDataToRealDataScale*Data[i];
	     
	         
	         
	         if(m_MaxRecievedData[i]<m_RecievedDataToRealDataScale*Data[i])
				m_MaxRecievedData[i]=m_RecievedDataToRealDataScale*Data[i];

             if(m_MinRecievedData[i]>m_RecievedDataToRealDataScale*Data[i])
				m_MinRecievedData[i]=m_RecievedDataToRealDataScale*Data[i];	
            /* 
	         if(m_MaxSectionData[m_DynamicSectionIndex][i]<m_RecievedDataToRealDataScale*Data[i])
	        	 m_MaxSectionData[m_DynamicSectionIndex][i]=m_RecievedDataToRealDataScale*Data[i];

             if( m_MinSectionData[m_DynamicSectionIndex][i]>m_RecievedDataToRealDataScale*Data[i])
            	 m_MinSectionData[m_DynamicSectionIndex][i]=m_RecievedDataToRealDataScale*Data[i];	            
			*/
             
	    }	
		if(m_TotoalRecieveTimeIndex<=100*m_DisplayDataMaxTimeIndex)
			m_TotoalRecieveTimeIndex++;

	}

}


/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
OscilloScope(Context context){
	
	super(context,BUTTON_SQUARE,0.0f,0.0f,1.0f,4.0f); //m_TextRenderList=DEFAULT_CAPTION_DISPLAYLIST;
	 m_ControlType =CONTROL_UNIT_OSCILLO;
	ShaderRelatedInit(m_Context);
}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
OscilloScope(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float BorderWith){
	 
	  super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);
	  m_Width=ContrlUnitWidth[ControlType]*m_Scale;
	  m_Height=ContrlUnitHeight[ControlType]*m_Scale;
	  m_ControlType =CONTROL_UNIT_OSCILLO;
	  ShaderRelatedInit(m_Context);

}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/

public OscilloScope(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float Width,float Height,float BorderWith,float FontSize)
{
	super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);	
	

	m_Width=Width*m_Scale-2.0f*BorderWith;
	m_Height=Height*m_Scale-2.0f*BorderWith;
	m_ControlType =CONTROL_UNIT_OSCILLO;	
	
	ShaderRelatedInit(m_Context);
	
	m_FontGraph = new MyFont(m_Context,(m_Context.getAssets()));
	GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_CULL_FACE);
	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	// Load the font from file (set size + padding), creates the texture
	// NOTE: after a successful call to this the font is ready for rendering!
	m_FontGraph.load( "Roboto-Regular.ttf", (int)(16*FontSize), 0, 0);  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
	m_FontSizeScaleFactor = FontSize;
}

void ShaderRelatedInit(Context context){
	
	 program =new int[1];
	// Initialize the color and texture handles
	/* String vertexShader = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_border);
	 String fragmentShader = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_border);

	 int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
	 int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
	
	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program[0] = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
			POSITION_ATTRIBUTE, TEXCORD_ATTRIBUTE});		
	*/
	
	String vertexShader1 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_panel);
	String fragmentShader1 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_panel);

	int vertexShaderHandle1 = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader1);
	int fragmentShaderHandle1 = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader1);

	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program[0] = ShaderHelper.createAndLinkProgram(vertexShaderHandle1, fragmentShaderHandle1, new String[] {
			POSITION_ATTRIBUTE,TEXCORD_ATTRIBUTE});	

	
	/*
	String vertexShader2 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_area);
	String fragmentShader2 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_area);

	int vertexShaderHandle2 = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader2);
	int fragmentShaderHandle2 = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader2);

	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program[2] = ShaderHelper.createAndLinkProgram(vertexShaderHandle2, fragmentShaderHandle2, new String[] {
			POSITION_ATTRIBUTE,COLOR_ATTRIBUTE});	
    
	
	// Load the texture
	textureId = TextureHelper.loadTexture(m_Context,R.drawable.orb);		// 
	GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			
	
	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);		
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
	
	//GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);		
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);	*/		
	GLES20.glGenBuffers(3, vbo, 0);
	GLES20.glGenBuffers(3, ibo, 0);	
	
	GLES20.glGenBuffers(5, vbo_data, 0);
	GLES20.glGenBuffers(5, ibo_data, 0);
	
	GLES20.glGenBuffers(1, vbo_data_realtime,0);
	GLES20.glGenBuffers(1, ibo_data_realtime,0);

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
boolean IsOnFocus(){
    return m_IsOnfocus;
}
 /***********************************************************************************
 子函数描述：GetEndIndex(), 最后一个数据的索引
 ************************************************************************************/
float GetEndIndex(){
    return (float)m_DisplayDataEndTimeIndex;//m_RecievedData[m_LatestTimeIndex];
}
 /***********************************************************************************
 子函数描述：SetScopeParameters(), 变量初始化
 ************************************************************************************/
//void SetScopeParameters(float OffsetX, float OffsetY,float Height, float Width, int CurveNum, String[] CurveLabelString, int[] Color,float DataSampleTimeInterval,int DataSize,int DivNumX, int DivNumY, boolean GridOn){
public void SetScopeParameters( float GraphWidth, float GraphHeight, int CurveNum){//, String[] CurveLabelString, int[] Color,float DataSampleTimeInterval,int DataSize,int DivNumX, int DivNumY, boolean GridOn){
		        
	  int i;
	  

	  m_OffSetX = m_OffSetX -(mWindowWidth-m_Width)*0.5f;
	  m_OffSetY = m_OffSetY -(mWindowHeight-m_Height)*0.5f;
	  
	  
	  m_GraphHeight=GraphHeight;
	  m_GraphWidth=GraphWidth;
	  m_CurveNum=CurveNum;
	  
	  m_GraphOffsetX= -(m_GraphWidth)/2.0f;//(mWindowWidth -m_GraphWidth)/2.0f; 
      m_GraphOffsetY= -(m_GraphHeight)/2.0f; 
      
	  //m_GraphLabelHeight=30*0.5f;//m_GraphUnitHeight*0.5;
	  m_GraphLabelWidth=(m_Width -m_GraphWidth-2*m_BorderWidth)/m_CurveNum;	  
	  //m_LabelStringY = CurveLabelString;
	  m_LabelStringX= "";//CurveLabelString[CurveNum];//"t(ms)";

	  m_DataSampleTimeInterval=1;//DataSampleTimeInterval;
      m_DivNumX=8;//DivNumX; 
      m_DivNumY=8;//DivNumY; 

	  m_GridOn=true;//GridOn;

      m_GraphUnitHeight=m_GraphHeight/m_DivNumY; 
      m_GraphUnitWidth=m_GraphWidth/m_DivNumX; 

	  m_AxisLineWidth=2;
	  m_GridLineWidth=1.0f;  
      m_CuveLineWidth=2;

      m_OriginValueY=new float[CurveNum];
      m_OriginValueX=0;

	  m_DivValueY= new float[CurveNum];
	  m_DivValueX=0;

	  m_Color=new int[CurveNum];

	  m_DisplayDataStartTimeIndex=0;
	  m_DisplayDataEndTimeIndex=0;
	  m_DisplayDataMaxTimeIndex=5000-1;
	  m_LatestTimeIndex=0;
	  m_TotoalRecieveTimeIndex=0;
	  m_DisplayDataTimeLength=1500;//ms
      m_MaxRecievedTime=1500;
	  m_MinRecievedTime=0;
	  m_RefreshMode=0;

	  m_RecievedTime=new float[5000];//new float[DataSize];//[];
	  m_RecievedData=new float[CurveNum][];//[DataSize];//[DataSize];
	  //[DataSize];//(float**)new float[CurveNum][DataSize];
	  m_TimeDrawCof=1;
	  m_DataDrawCof=new float[CurveNum];
	  m_DisplayDataRange =new float[CurveNum];
	  m_MaxRecievedData  = new float[CurveNum];
	  m_MinRecievedData  = new float[CurveNum];
	  
	  m_MaxDispData  = new float[CurveNum];
	  m_MinDispData  = new float[CurveNum];
	  //m_UserSelectedStartX;
	  //m_UserSelectedEndX;
	  m_UserSelectedStartY = new float[CurveNum];
	  m_UserSelectedEndY = new float[CurveNum];
	  m_UserSelectedStartY1 = new float[CurveNum];
	  m_UserSelectedEndY1 = new float[CurveNum];
	  
	  for(i=0;i<m_CurveNum;i++){ 	 
	     	//m_RecievedTime[i]=0;
			//m_RecievedData=new float(CurveNum ,DataSize);//[][];
            m_OriginValueY[i]=0;
			m_DataDrawCof[i]=1;
			m_DisplayDataRange[i]=100;
			m_MaxRecievedData[i]=0;
			m_MinRecievedData[i]=0;
			m_UserSelectedStartY[i]=0;
			m_UserSelectedStartY1[i]=0;
			m_RecievedData[i]=new float[5000];//new float[DataSize];
			m_UserSelectedEndY[i]=0; 
			m_UserSelectedEndY1[i]=0; 
			m_DivValueY[i]=0;
			//m_Color[i]=0x
		    // *(m_RecievedData+i*DataSize)=new float(DataSize);//[][];
	 
	  }
	  m_RecievedTimeToRealTimeScale=1;
	  m_RecievedDataToRealDataScale=1;
      m_IsResetAxisX=false;
      m_IsResetAxisY=false;
      m_IsResetAxisXY=true;
	  m_AutomaticDisplay=true; 
	  m_StartRecieve=false;
	  
	  m_EffectDataStartIndex =new float[CurveNum];
	  m_DataColor = new float[CurveNum][4];
	  m_timer =0.0f;

	  
	  m_CmdRunState =true;
	  m_IsOnDrag    =false;
	  
	  m_SectionNum =5;
	  m_PerSectionDataLength =5000/m_SectionNum;
	  m_DynamicSectionIndex =0;  
	  m_RealtimeSectionIndex =0;
	  m_RealTimeDataLength = 0;
	  m_RecievedSectionData=new float[m_PerSectionDataLength*4*m_CurveNum];//[DataSize];//[DataSize];
	  m_RecievedSectionDataIndex =new short[m_PerSectionDataLength*2*m_CurveNum];
	  for(i=0;i<m_PerSectionDataLength*2*m_CurveNum;i++)
	  {
			m_RecievedSectionDataIndex[i] =(short)i;
	  }
	  
	  
	  m_MaxSectionData  = new float[m_SectionNum][CurveNum];
	  m_MinSectionData  = new float[m_SectionNum][CurveNum];
	  //m_MinSectionDataIndex  = new float[m_SectionNum];
	  //m_MaxSectionDataIndex  = new float[m_SectionNum];
	  //m_SectionDataStartTime  = new float[m_SectionNum];
	  
	  m_DispStartTimeOffsetIndex =0; 
	  
	  m_TotalMoveX =0.0f;
	  m_TotalScaleX=m_DisplayDataTimeLength;
	  m_TotalMoveY =0.0f;
	  m_TotalScaleY=1.0f;  
	  m_PreScaleY=1.0f;  
	  m_PreScaleX=1.0f;  
	  InitGLDataForBorder();
	  InitGLDataForArea();
	 InitGLDataForBackPanel();
	 InitGLDataForDataBackground();
	 
}


public void SetDispWiodowSize(int width, int height)
{
	mWindowWidth = width;
	mWindowHeight = height;
}
/***********************************************************************************
 子函数描述：AddCaption(), 主题标签
 ************************************************************************************/
public void AddCaption(String TextString)
{
	 m_TextString  = TextString;

}

public void Stop()
{
	 m_CmdRunState  = false;
	 m_AutomaticDisplay =false;
}

public void Start()
{
	 m_CmdRunState  = true;
	 m_AutomaticDisplay= true;
}
/***********************************************************************************
 子函数描述：DrawScaleRullerX(),绘制框选的X轴区间
 ************************************************************************************/
void DrawScaleRullerX(){
   /*
	 // Draw X axis and Y axis         
	 //if(tempActiveMouseX >m_OffSetX+m_GraphOffsetX+m_GraphWidth)
	    int  tempReleaseMouseX;
        tempReleaseMouseX = Math.min((float)m_ReleaseMouseX,(float)(m_OffSetX+m_GraphOffsetX+m_GraphWidth));
        tempReleaseMouseX =max(tempReleaseMouseX,m_OffSetX+m_GraphOffsetX);
        //tempReleaseMouseY =min(m_ReleaseMouseY,m_OffSetY+m_GraphOffsetY+m_GraphHeight);
        //tempReleaseMouseY=max(tempReleaseMouseY,m_OffSetY+m_GraphOffsetY);
	    glColor4f(1.0,1.0,1.0,1.0);       
	    glLineWidth(m_GridLineWidth);
	    glBegin(GL_LINES);
			  glVertex3f(m_ActiveMouseX-m_OffSetX, m_ActiveMouseY+10-m_OffSetY ,0.0);
			  glVertex3f(m_ActiveMouseX-m_OffSetX,m_ActiveMouseY-10-m_OffSetY ,0.0);	

			  glVertex3f(m_ActiveMouseX-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);
			  glVertex3f(tempReleaseMouseX-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);

			  glVertex3f(tempReleaseMouseX-m_OffSetX,m_ActiveMouseY+10-m_OffSetY  ,0.0);
			  glVertex3f(tempReleaseMouseX-m_OffSetX,m_ActiveMouseY-10-m_OffSetY  ,0.0);          
	    glEnd();*/
}
/***********************************************************************************
 子函数描述：DrawScaleRullerY(),绘制所选的Y轴区间长度
 ************************************************************************************/
void DrawScaleRullerY(){
   /*
	    // Draw X axis and Y axis 
	    //tempReleaseMouseX =min(m_ReleaseMouseX,(m_OffSetX+m_GraphOffsetX+m_GraphWidth));
        //tempReleaseMouseX=max(tempReleaseMouseX,m_OffSetX+m_GraphOffsetX);
	    int  tempReleaseMouseY;
        tempReleaseMouseY =min(m_ReleaseMouseY,m_OffSetY+m_GraphOffsetY+m_GraphHeight);
        tempReleaseMouseY=max(tempReleaseMouseY,m_OffSetY+m_GraphOffsetY);        
	    glColor4f(1.0,1.0,1.0,1.0);       
	    glLineWidth(m_GridLineWidth);
	    glBegin(GL_LINES);
			  glVertex3f(m_ActiveMouseX+10-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);
			  glVertex3f(m_ActiveMouseX-10-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);	

			  glVertex3f(m_ActiveMouseX-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);
			  glVertex3f(m_ActiveMouseX-m_OffSetX, tempReleaseMouseY-m_OffSetY ,0.0);

			  glVertex3f(m_ActiveMouseX+10-m_OffSetX, tempReleaseMouseY-m_OffSetY,0.0);
			  glVertex3f(m_ActiveMouseX-10-m_OffSetX, tempReleaseMouseY-m_OffSetY,0.0);			  
          
	    glEnd();*/
}
/***********************************************************************************
 子函数描述：DrawScaleRullerXY(),绘制框选区间
 ************************************************************************************/
void DrawScaleRullerXY(){
   /*
	   // Draw X axis and Y axis 
	   int  tempReleaseMouseX,tempReleaseMouseY;
	   tempReleaseMouseX =min(m_ReleaseMouseX,(m_OffSetX+m_GraphOffsetX+m_GraphWidth));
       tempReleaseMouseX=max(tempReleaseMouseX,m_OffSetX+m_GraphOffsetX);
       tempReleaseMouseY =min(m_ReleaseMouseY,m_OffSetY+m_GraphOffsetY+m_GraphHeight);
       tempReleaseMouseY=max(tempReleaseMouseY,m_OffSetY+m_GraphOffsetY);        
	   glColor4f(1.0,1.0,1.0,1.0);       
	   glLineWidth(m_GridLineWidth);
	   glEnable(GL_LINE_STIPPLE);
	   glLineStipple(1.0f,0xF0F0);
	   glBegin(GL_LINES);

			  glVertex3f(m_ActiveMouseX-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);
			  glVertex3f(m_ActiveMouseX-m_OffSetX, tempReleaseMouseY-m_OffSetY ,0.0);	

			  glVertex3f(m_ActiveMouseX-m_OffSetX, tempReleaseMouseY-m_OffSetY ,0.0);	
			  glVertex3f(tempReleaseMouseX-m_OffSetX, tempReleaseMouseY-m_OffSetY ,0.0);

			  glVertex3f(tempReleaseMouseX-m_OffSetX, tempReleaseMouseY-m_OffSetY ,0.0);
			  glVertex3f(tempReleaseMouseX-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);

			  glVertex3f(tempReleaseMouseX-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);
			  glVertex3f(m_ActiveMouseX-m_OffSetX, m_ActiveMouseY-m_OffSetY ,0.0);
          
	   glEnd();
	   glDisable(GL_LINE_STIPPLE);*/
}

/***********************************************************************************
 子函数描述：Render(),绘制整个示波器模块
 ************************************************************************************/
public void  Render(float[] modelMatrix,float[] Boundary){    

	/* m_timer+= 1.0f;
	
	 for(int i=0;i<4;i++)
		 m_TestData[i] =2000*(i+1)*(float)Math.cos(m_timer/500.0f)*(float)Math.sin((i+1)*m_timer/40.0f+i*5.0f);
	    			//float yyy=2000*cos(double(iFrames)/500.0)*sin(double(iFrames)/4.0);            
	 //ReciedveData(m_TestData[0],m_TestData);  
	 ReciedveData(m_timer,m_TestData);  */
	mBoundary =Boundary;
	DrawLables(modelMatrix);
	DrawBackPanel(modelMatrix);
	 RefreshDisplay();
	 DrawDataBackground(modelMatrix);
	 DrawData(modelMatrix);	

	//DrawControlBorder(modelMatrix);
	//DrawControlArea(modelMatrix);
	
	
	

 }

public void  RenderFont(float[] modelMatrix,float[] Boundary){    

 	GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	// TEST: render some strings with the font
  	m_Font.SetMvpMatrix(modelMatrix);
	//s = "START";
	m_Font.SetDisplayArea(Boundary);  
  	m_Font.SetColor( 1.0f, 0.0f, 0.0f, 1.0f );  
  	m_Font.draw( m_TextString ,m_OffSetX -(m_BorderWidth)/2.0f  -m_TextString.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor, 0); 
   	
	/*
    DecimalFormat format = new DecimalFormat("#.00");
    String s = format.format((float)(m_ActiveMouseX)); //转换成字符串
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-20, 0); 
    
	s = format.format((float)(m_ReleaseMouseX)); //转换成字符串
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-40, 0); 
    	
  	s = format.format((float)(m_ActiveMouseX1)); //转换成字符串
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-60, 0); 
    
	s = format.format((float)(m_ReleaseMouseX1)); //转换成字符串
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-80, 0); 
    
  	s = format.format((float)(m_ActiveMouseY)); //转换成字符串
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor-300, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-20, 0); 
    
	s = format.format((float)(m_ReleaseMouseY)); //转换成字符串
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor-300, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-40, 0); 
    	
  	s = format.format((float)(m_ActiveMouseY1)); //转换成字符串
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor-300, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-60, 0); 
    
	s = format.format((float)(m_ReleaseMouseY1)); //转换成字符串
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor-300, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-80, 0); 
   */ 	
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
void UserMouseMove(int pointerId,float wParam, float lParam){
   /* m_ReleaseMouseX = ( short )( lParam );
    m_ReleaseMouseY = ( short )( lParam );
	*/
	float tempOriginal,tempLength,tempScale,tempOriginalY,tempMax,tempMin,tempOriginalMax,tempOriginalMin;
	int tempMaxIndex,tempMinIndex,tempDispStartTimeIndex,tempDispEndTimeIndex;
    float OffSetX = m_OffSetX +(mWindowWidth-m_Width)*0.5f + 0.5f*(m_Width-m_GraphWidth);
    float OffSetY = m_OffSetY +(mWindowHeight-m_Height)*0.5f +0.5f*(m_Height-m_GraphHeight);
    
	 if(pointerId ==0){  	
	    
		if(m_IsOnDrag){ 
			m_ReleaseMouseX = ( short )( wParam );
			m_ReleaseMouseY = ( short )( lParam );	
			
			
		}
		else{
			
		    m_ReleaseMouseX = ( short )( wParam );
			m_ReleaseMouseY = ( short )( lParam );		
			
			//if(((float)m_ReleaseMouseX - (float)m_ActiveMouseX) > 2*mWindowWidth)
			//	m_ReleaseMouseX = m_ActiveMouseX + 2*mWindowHeight;
			tempOriginal = m_OriginValueX-(((float)m_ReleaseMouseX - (float)m_ActiveMouseX))/m_TimeDrawCof;
			if(tempOriginal>m_MaxRecievedTime-m_DivValueX)
				m_OriginValueX =m_MaxRecievedTime-m_DivValueX;
			
			else if(tempOriginal+m_DivValueX*(m_DivNumX-1) <m_MinRecievedTime)
				m_OriginValueX =m_MinRecievedTime-m_DivValueX*(m_DivNumX-1);
			else
				m_OriginValueX = tempOriginal;		
			
			
			
			
			/*
			tempMax=m_MaxRecievedData[0];
			tempMin=m_MinRecievedData[0];
			tempMaxIndex=tempMinIndex =0;
			for(int i=0;i<m_CurveNum;i++){
				if((tempMin-m_OriginValueY[tempMinIndex])*m_DataDrawCof[tempMinIndex]>=(m_MinRecievedData[i]-m_OriginValueY[i])*m_DataDrawCof[i]){
					tempMin=m_MinRecievedData[i];
					tempMinIndex =i;
				}
				
				if((tempMax-m_OriginValueY[tempMaxIndex])*m_DataDrawCof[tempMaxIndex]<=(m_MaxRecievedData[i]-m_OriginValueY[i])*m_DataDrawCof[i]){
					tempMax=m_MaxRecievedData[i];
					tempMaxIndex =i;
				}
			}
			*/

			tempDispStartTimeIndex = (int)((m_OriginValueX)/m_RecievedTimeToRealTimeScale) -m_DispStartTimeOffsetIndex;
			if(tempDispStartTimeIndex<=0)
				tempDispStartTimeIndex =0;
			if(tempDispStartTimeIndex>=m_TotoalRecieveTimeIndex)
				tempDispStartTimeIndex =m_TotoalRecieveTimeIndex;			
			
			tempDispEndTimeIndex = (int)((m_OriginValueX +m_DivValueX*m_DivNumX)/m_RecievedTimeToRealTimeScale)-m_DispStartTimeOffsetIndex;		
			if(tempDispEndTimeIndex<=0)
				tempDispEndTimeIndex =0;
			if(tempDispEndTimeIndex>=m_TotoalRecieveTimeIndex)
				tempDispEndTimeIndex =m_TotoalRecieveTimeIndex;	
			
	         if(m_TotoalRecieveTimeIndex > m_DisplayDataMaxTimeIndex)
	         {
	        	 
	        	 if(tempDispEndTimeIndex+m_DispStartTimeOffsetIndex <m_TotoalRecieveTimeIndex)
	        		 tempDispEndTimeIndex = m_DisplayDataMaxTimeIndex -(m_TotoalRecieveTimeIndex-tempDispEndTimeIndex-m_DispStartTimeOffsetIndex);
	        	 else
	        		 tempDispEndTimeIndex = m_DisplayDataMaxTimeIndex;
	        	 
	        	 tempDispStartTimeIndex = tempDispEndTimeIndex -(int)((m_DivValueX*m_DivNumX)/m_RecievedTimeToRealTimeScale);
	 			
	        	 if(tempDispStartTimeIndex<=0)
					tempDispStartTimeIndex =0;
	         }

			for(int i=0;i<m_CurveNum;i++){
				m_MaxDispData[i] =m_RecievedData[i][tempDispStartTimeIndex];
				m_MinDispData[i] =m_RecievedData[i][tempDispStartTimeIndex];
			}
			
			for(int j = tempDispStartTimeIndex;j<=tempDispEndTimeIndex; j++)
			for(int i=0;i<m_CurveNum;i++){
				if(m_MaxDispData[i]<m_RecievedData[i][j])
					m_MaxDispData[i]=m_RecievedData[i][j];

				if(m_MinDispData[i]>m_RecievedData[i][j])
					m_MinDispData[i]=m_RecievedData[i][j];	
			}
			
			
			tempMax=m_MaxDispData[0];//[tempDispStartTimeIndex];
			tempMin=m_MinDispData[0];//[tempDispStartTimeIndex];
			tempMaxIndex=tempMinIndex =0;
			for(int i=0;i<m_CurveNum;i++){
				if((tempMin-m_OriginValueY[tempMinIndex])*m_DataDrawCof[tempMinIndex]>=(m_MinDispData[i]-m_OriginValueY[i])*m_DataDrawCof[i]){
					tempMin=m_MinDispData[i];
					tempMinIndex =i;
				}
						
				if((tempMax-m_OriginValueY[tempMaxIndex])*m_DataDrawCof[tempMaxIndex]<=(m_MaxDispData[i]-m_OriginValueY[i])*m_DataDrawCof[i]){
					tempMax=m_MaxDispData[i];
					tempMaxIndex =i;
				}
			}
			
			tempOriginalMax = m_OriginValueY[tempMaxIndex] - (((float)m_ReleaseMouseY - (float)m_ActiveMouseY))/m_DataDrawCof[tempMaxIndex];
			tempOriginalMin = m_OriginValueY[tempMinIndex] - (((float)m_ReleaseMouseY - (float)m_ActiveMouseY))/m_DataDrawCof[tempMinIndex];
		
			if(tempOriginalMax>=tempMax-m_DivValueY[tempMaxIndex]){
				
				tempOriginalY     =(m_OriginValueY[tempMaxIndex]-(tempMax-m_DivValueY[tempMaxIndex]))*m_DataDrawCof[tempMaxIndex]+m_ActiveMouseY;
				//m_OriginValueY[tempMaxIndex] =tempMax-m_DivValueY[tempMaxIndex];
				
			}				
			else if(tempOriginalMin+m_DivValueY[tempMinIndex]*(m_DivNumY-1) <tempMin){
				tempOriginalY     =(m_OriginValueY[tempMinIndex]-(tempMin-m_DivValueY[tempMinIndex]*(m_DivNumY-1)))*m_DataDrawCof[tempMinIndex]+m_ActiveMouseY;
				//m_OriginValueY[tempMinIndex] =tempMin-m_DivValueY[tempMinIndex]*(m_DivNumY-1);
			}	
			else{
				tempOriginalY  =m_ReleaseMouseY;
			}	 
			for(int i=0;i<m_CurveNum;i++){
				
				//if(((float)m_ReleaseMouseY - (float)m_ActiveMouseY) > 2*mWindowHeight)				
				//	m_ReleaseMouseY = m_ActiveMouseY + 2*mWindowHeight;	

				
				m_OriginValueY[i] = m_OriginValueY[i] - (((float)tempOriginalY - (float)m_ActiveMouseY))/m_DataDrawCof[i];
				/*if(m_MinRecievedData[i]- 0.9f*(m_DisplayDataRange[i])>=m_OriginValueY[i])
					m_OriginValueY[i] = m_MinRecievedData[i]- 0.9f*(m_DisplayDataRange[i]);
				if(m_MaxRecievedData[i]- 0.1f*(m_DisplayDataRange[i])<=m_OriginValueY[i])
					m_OriginValueY[i] = m_MaxRecievedData[i]- 0.1f*(m_DisplayDataRange[i]);	*/
			}				
				
			 m_ActiveMouseX =m_ReleaseMouseX;
		     m_ActiveMouseY = m_ReleaseMouseY ;			
		     //RefreshDisplay();
		}
	 }
	 else{
		 
		if(m_IsOnDrag){ 	 
			m_ReleaseMouseX1 = ( short )( wParam );
			m_ReleaseMouseY1 = ( short )( lParam );		
		}
		else{

		     m_ReleaseMouseX1=m_ActiveMouseX1 = ( short )( wParam );
			 m_ReleaseMouseY1=m_ActiveMouseY1 = ( short )( lParam );		
		}			
	 }
	
	 
		if(m_IsOnDrag){ 
			
			//if((float)m_ReleaseMouseX1 - (float)m_ReleaseMouseX <=10)
			//	m_ReleaseMouseX1 = m_ReleaseMouseX +10;
			
			if((float)m_ActiveMouseX1<=(float)m_ActiveMouseX && (float)m_ReleaseMouseX1 >= (float)m_ReleaseMouseX)
			{
				tempScale =m_PreScaleX;//tempScale =1.0f;
			}
			else if((float)m_ActiveMouseX1>=(float)m_ActiveMouseX && (float)m_ReleaseMouseX1 <= (float)m_ReleaseMouseX)
			{
				tempScale =m_PreScaleX;//tempScale =1.0f;						
			}	
			else
				tempScale = ( (float)m_ReleaseMouseX1 - (float)m_ReleaseMouseX )/( (float)m_ActiveMouseX1-  (float)m_ActiveMouseX+0.00001f) ;
			
			float scaleX =  0.0001f+Math.abs(tempScale);
				
			if((float)m_UserSelectedEndX/scaleX>50000.0f)
				scaleX =m_UserSelectedEndX/50000.0f;
			
			if((float)m_UserSelectedEndX/scaleX<2f)
				scaleX =m_UserSelectedEndX/2;
			
			

				
			tempOriginal = (float)m_UserSelectedStartX1 - (float)m_UserSelectedEndX1/scaleX;
			tempLength	= (float)m_UserSelectedEndX/scaleX;

			m_OriginValueX = tempOriginal;			
			m_DisplayDataTimeLength = tempLength;	
			m_PreScaleX = scaleX;
 
			 
			for(int i=0;i<m_CurveNum;i++){ 

					if((float)m_ActiveMouseY1<=(float)m_ActiveMouseY && (float)m_ReleaseMouseY1 >= (float)m_ReleaseMouseY)
					{
						tempScale =m_PreScaleY;//tempScale =1.0f;
					}
					else if((float)m_ActiveMouseY1>=(float)m_ActiveMouseY && (float)m_ReleaseMouseY1 <= (float)m_ReleaseMouseY)
					{
						tempScale =m_PreScaleY;;//tempScale =1.0f;						
					}	
					else
						
						tempScale = ( (float)m_ReleaseMouseY1 - (float)m_ReleaseMouseY )/( (float)m_ActiveMouseY1-  (float)m_ActiveMouseY+0.00001f) ;
					
					float scaleY =  0.0001f+Math.abs(tempScale );
					
					if((float)m_UserSelectedEndY[0]/scaleY>50000.0f)
						scaleY =m_UserSelectedEndY[0]/50000.0f;
					
					if((float)m_UserSelectedEndY[0]/scaleY<0.5f)
						scaleY =m_UserSelectedEndY[0]/0.5f;
					
					tempOriginal = m_UserSelectedStartY1[i] - m_UserSelectedEndY1[i]/scaleY;
					tempLength	=  m_UserSelectedEndY[i]/scaleY;	
			
					{
						
						m_OriginValueY[i]   =tempOriginal;
						m_DisplayDataRange[i]= tempLength;
						m_PreScaleY = scaleY;

					}

					//m_MinRecievedData[i]=m_OriginValueY[i];
					//m_MaxRecievedData[i]=m_MinRecievedData[i]+m_DisplayDataRange[i];					
				}
			 
				//RefreshDisplay();
			 
		}
	 
/*
    m_ReleaseMouseX = ( short )( lParam );
    m_ReleaseMouseY = ( short )( lParam );
	if(!m_IsActive){      
		if(m_ReleaseMouseX-m_GraphOffsetX-m_OffSetX<m_GraphWidth  && m_ReleaseMouseY-m_GraphOffsetY-m_OffSetY< m_GraphHeight  && m_ReleaseMouseX-m_OffSetX > m_GraphOffsetX && m_ReleaseMouseY-m_OffSetY > m_GraphOffsetY){
             m_IsOnfocus=true;
 		}
		else
			 m_IsOnfocus=false;
	}
	*/
}

/***********************************************************************************
 子函数描述：UserMouseDown(),鼠标点击事件
 ************************************************************************************/
 void UserMouseDown(int pointerId,float wParam, float lParam){

	     float OffSetX = m_OffSetX +(mWindowWidth-m_Width)*0.5f + 0.5f*(m_Width-m_GraphWidth);
	     float OffSetY = m_OffSetY +(mWindowHeight-m_Height)*0.5f +0.5f*(m_Height-m_GraphHeight);
	
	 if(pointerId ==0){    
	     m_ReleaseMouseX=m_ActiveMouseX = ( short )( wParam );
		 m_ReleaseMouseY=m_ActiveMouseY = ( short )( lParam );		 
		 
		 
			if(!m_IsActive){      
				if(m_ReleaseMouseX-(OffSetX)<m_GraphWidth  && m_ReleaseMouseY-(OffSetY)< m_GraphHeight  && m_ReleaseMouseX-(OffSetX)> 0 && m_ReleaseMouseY-(OffSetY)>0){
		             m_IsOnfocus=true;
		             m_IsActive =true;
		 		}
				else{
					 m_IsOnfocus=false;
					 m_IsActive =false;
				}
			}		 
			else{
				
				
	            /*
				m_UserSelectedStartX = (m_ActiveMouseX-(OffSetX))/m_TimeDrawCof+m_OriginValueX;

				for(int i=0;i<m_CurveNum;i++){ 

					m_UserSelectedStartY[i] = (m_ActiveMouseY-(OffSetY))/m_DataDrawCof[i]+m_OriginValueY[i];
				}*/
			}	
	 }
	 else
	 {
			if(m_IsActive){      
				if(m_ReleaseMouseX-(OffSetX)<m_GraphWidth  && m_ReleaseMouseY-(OffSetY)< m_GraphHeight  && m_ReleaseMouseX-(OffSetX)> 0 && m_ReleaseMouseY-(OffSetY)>0){
		             m_IsOnDrag=true;
				     m_ReleaseMouseX1=m_ActiveMouseX1 = ( short )( wParam );
					 m_ReleaseMouseY1=m_ActiveMouseY1 = ( short )( lParam );	
					 
					 m_UserSelectedStartX = m_OriginValueX;
					 m_UserSelectedEndX   = m_DisplayDataTimeLength;
					for(int i=0;i<m_CurveNum;i++){ 
					   m_UserSelectedStartY[i] = m_OriginValueY[i];
					   m_UserSelectedEndY[i]   = m_DisplayDataRange[i];
					   
					   m_UserSelectedStartY1[i] = (0.5f*(m_ActiveMouseY + m_ActiveMouseY1)-(OffSetY))/m_DataDrawCof[i]+m_OriginValueY[i];
					   m_UserSelectedEndY1[i] = (0.5f*(m_ActiveMouseY + m_ActiveMouseY1)-(OffSetY))/m_DataDrawCof[i];
					}
					
					m_UserSelectedStartX1 = (0.5f*(m_ActiveMouseX + m_ActiveMouseX1)-(OffSetX))/m_TimeDrawCof+m_OriginValueX; 
					m_UserSelectedEndX1 = (0.5f*(m_ActiveMouseX + m_ActiveMouseX1)-(OffSetX))/m_TimeDrawCof;
					
					
		 		}
				else{
					m_IsOnDrag=false;

				}

				 
				/*	m_UserSelectedStartX1 = (m_ActiveMouseX1-(OffSetX))/m_TimeDrawCof+m_OriginValueX;

					for(int i=0;i<m_CurveNum;i++){ 

						m_UserSelectedStartY1[i] = (m_ActiveMouseY1-(OffSetY))/m_DataDrawCof[i]+m_OriginValueY[i];
					}	*/ 			
				
			}
		 

	 }
 }

/***********************************************************************************
 子函数描述：UserMouseUp(),鼠标释放事件
 ************************************************************************************/	 
void  UserMouseUp(int pointerId,float wParam, float lParam){
	
	
	 if(pointerId ==0){  
		 m_IsOnfocus=false;
		 m_IsActive =false;
		m_IsOnDrag=false;

	 }
	 else
	 {
		 //m_TotalScaleX =m_TotalScaleX*m_PreScaleX;
		 m_IsOnDrag=false;
	 }

	/*
    float OffSetX = m_OffSetX +(mWindowWidth-m_Width)*0.5f + 0.5f*(m_Width-m_GraphWidth);
    float OffSetY = m_OffSetY +(mWindowHeight-m_Height)*0.5f +0.5f*(m_Height-m_GraphHeight);	
	if(m_IsActive){
	 m_IsOnfocus=false;
	 m_IsActive =false;
     m_ReleaseMouseX = ( short )( wParam );
	 m_ReleaseMouseY = ( short )( lParam );	  
    //tempRescale= abs(m_UserSelectedEndY-m_UserSelectedSatrtY)/m_DisplayDataRange[0];

	if(m_IsResetAxisY || m_IsResetAxisXY){

		 for(int i=0;i<m_CurveNum;i++){ 

			  m_UserSelectedEndY[i] = (m_ReleaseMouseY-OffSetY)/m_DataDrawCof[i]+m_OriginValueY[i];
			  m_DisplayDataRange[i] = Math.abs(m_UserSelectedEndY[i]-m_UserSelectedStartY[i] );

			  if(m_DisplayDataRange[i]< 0.001f)
				   m_DisplayDataRange[i]=0.001f;

			  if(m_UserSelectedEndY[i] > m_UserSelectedStartY[i]){
				    //m_MaxRecievedData[i]=m_UserSelectedEndY[i];
					m_MinRecievedData[i]= m_UserSelectedStartY[i];
					if(m_DisplayDataRange[i]>= 0.001f)
						m_MaxRecievedData[i]=m_UserSelectedEndY[i];

					else
						 m_MaxRecievedData[i]= m_MinRecievedData[i]+0.001f;

			   }
			   else{
                      //m_MaxRecievedData[i]= m_UserSelectedStartY[i];
				    m_MinRecievedData[i]=m_UserSelectedEndY[i];
					if(m_DisplayDataRange[i]>= 0.001)
						m_MaxRecievedData[i]=m_UserSelectedStartY[i];
					else
						m_MaxRecievedData[i]= m_MinRecievedData[i]+0.001f;
			   }
				 m_OriginValueY[i]=m_UserSelectedStartY[i];
		    }          

	   }

       if(m_IsResetAxisX || m_IsResetAxisXY){ 
			 m_UserSelectedEndX = (m_ReleaseMouseX-OffSetX)/m_TimeDrawCof+m_OriginValueX;
             m_DisplayDataTimeLength= (int)(Math.abs(m_UserSelectedEndX-m_UserSelectedStartX));
             m_DisplayDataTimeLength = m_DisplayDataTimeLength>= 2*m_DataSampleTimeInterval?m_DisplayDataTimeLength: 2*m_DataSampleTimeInterval;
			 if(m_DisplayDataTimeLength< m_DataSampleTimeInterval)
				 m_DisplayDataTimeLength=m_DataSampleTimeInterval;

			 if(m_UserSelectedEndX > m_UserSelectedStartX){
				 m_MinRecievedTime= m_UserSelectedStartX;
                 //if(m_DisplayDataTimeLength>= 2*m_DataSampleTimeInterval)
				 //      m_MaxRecievedTime=m_UserSelectedEndX;
				 //else
					   m_MaxRecievedTime=m_MinRecievedTime+m_DisplayDataTimeLength;
			  }
			  else{
				     m_MinRecievedTime=m_UserSelectedStartX;
				     m_DisplayDataTimeLength =2*m_DataSampleTimeInterval;
					 m_MaxRecievedTime=m_MinRecievedTime+2*m_DataSampleTimeInterval;				  
			  }
			  m_OriginValueX = m_MinRecievedTime;
		}
		RefreshDisplay();
        m_IsOnfocus=false;
		m_IsActive=false;
		  
}*/

}

public float getTotalX(){
	return m_TotoalRecieveTimeIndex;
}

};