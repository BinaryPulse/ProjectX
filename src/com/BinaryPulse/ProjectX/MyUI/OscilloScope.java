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
	protected static int m_DisplayDataMaxTimeIndex;
	protected static int m_LatestTimeIndex;
	protected static float m_DisplayDataTimeLength;
	protected static float[] m_RecievedTime;//[];
	protected static float[][] m_RecievedData;//[];//[];
	protected static float[][] m_DataColor;//[];//[];
	protected static float m_TimeDrawCof;
	protected static float[] m_DataDrawCof;//[];
	protected static float[] m_DisplayDataRange;//[];
	protected static float[] m_MaxRecievedData;//[];
	protected static float[] m_MinRecievedData;//[];;

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
	
	protected int[] vbo = new int[2];
	protected int[] ibo = new int[2];
	
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
           
		         ����ģ�鹦�������� OscilloScope��ʾ������

###############################################################################*/

/***********************************************************************************
�Ӻ���������DrawControlBorder(bool AnimationEnabled), ����ʾ�����߿�
************************************************************************************/
/*void  DrawControlBorder(float[] modelMatrix){//boolean AnimationEnabled ){
	
	float[] color = {0.0f,1.0f, 1.0f, 0.0f};
	
	Matrix.setIdentityM(mMVPMatrix, 0);
	Matrix.translateM(mMVPMatrix, 0, -(m_Width)/2.0f- m_BorderWidth +m_OffSetX, -m_Height/2.0f-m_BorderWidth +m_OffSetY, 0);	
	Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
	
	  if(m_IntervalCoordinate<0.48f && m_IntervalCoordinate>=0f)

	       m_IntervalCoordinate+=0.0025f;
     else
          m_IntervalCoordinate=0.0f;  
	  
	if (vbo[0] > 0 && ibo[0] > 0) {		
		
		GLES20.glUseProgram(program[0]);
		ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);
        TextureUniformHandle = GLES20.glGetUniformLocation(program[0], TEXTURE_UNIFORM);
        TextureMoveUniformHandle = GLES20.glGetUniformLocation(program[0], TEXTUREMOV_UNIFORM);
        
		GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
		GLES20.glEnableVertexAttribArray(ColorHandle);
		
		GLES20.glUniform1f(TextureMoveUniformHandle, m_IntervalCoordinate);//(, 1, mcolor , 0); 
		GLES20.glEnableVertexAttribArray(TextureMoveUniformHandle);	
		
 	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // Set the active texture unit to texture unit 0
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
		GLES20.glUniform1i(TextureUniformHandle, 0); 
	
		// Set program handles for cube drawing.
		mvpMatrixUniform = GLES20.glGetUniformLocation(program[0], MVP_MATRIX_UNIFORM);
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
		positionAttribute = GLES20.glGetAttribLocation(program[0], POSITION_ATTRIBUTE);
		texcordAttribute = GLES20.glGetAttribLocation(program[0], TEXCORD_ATTRIBUTE);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

		GLES20.glVertexAttribPointer(texcordAttribute, 2, GLES20.GL_FLOAT, false,5*4, 0);
		GLES20.glEnableVertexAttribArray(texcordAttribute);			
		
		// Bind Attributes
		GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,5*4, 2*4);
		GLES20.glEnableVertexAttribArray(positionAttribute);
	
		// Draw
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 12, GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GLES20.glUseProgram(0);
	}
}*/

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
	/*final FloatBuffer VertexDataBuffer = ByteBuffer
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
	} */
}

/***********************************************************************************
�Ӻ���������DrawControlArea(bool AnimationEnabled), ����ʾ������������
************************************************************************************/
/*void DrawControlArea(float[] modelMatrix){
	
         
	//GLES20.glEnable(GLES20.GL_BLEND);  
	//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);	
		
		//float[] color = {0.0f,0.3f, 0.3f, 0.3f};		
	//GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	//GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	
		//Matrix.setIdentityM(mMVPMatrix, 0);
		//Matrix.translateM(mMVPMatrix, 0, m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);	
		//Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
		
		Matrix.setIdentityM(mMVPMatrix, 0);
		Matrix.translateM(mMVPMatrix, 0, -(m_Width)/2.0f- m_BorderWidth +m_OffSetX, -m_Height/2.0f-m_BorderWidth +m_OffSetY, 0);	
		Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);		
		
		if (vbo[3] > 0 && ibo[3] > 0) {		
			
			GLES20.glUseProgram(program[2]);
			//ColorHandle          = GLES20.glGetUniformLocation(program[1], COLOR_UNIFORM);	        
			//GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
			//GLES20.glEnableVertexAttribArray(ColorHandle);
			 
			// Set program handles for cube drawing.
			mvpMatrixUniform = GLES20.glGetUniformLocation(program[2], MVP_MATRIX_UNIFORM);
			GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
			
			positionAttribute = GLES20.glGetAttribLocation(program[2], POSITION_ATTRIBUTE);
			colorAttribute = GLES20.glGetAttribLocation(program[2], COLOR_ATTRIBUTE);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[3]);
			// Bind Attributes
			GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,7*4, 0);
			GLES20.glEnableVertexAttribArray(positionAttribute);
			
			GLES20.glVertexAttribPointer(colorAttribute, 4, GLES20.GL_FLOAT, false,7*4, 3*4);
			GLES20.glEnableVertexAttribArray(colorAttribute);			
			// Draw
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[3]);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 8, GLES20.GL_UNSIGNED_SHORT, 0);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);			
			GLES20.glUseProgram(0);
		}
		//GLES20.glDisable(GLES20.GL_BLEND);
  
}*/

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
    
  /*  
	final FloatBuffer VertexDataBuffer = ByteBuffer
				.allocateDirect(vertexBuffer.length * 4).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
	VertexDataBuffer.put(vertexBuffer).position(0);
		
	final ShortBuffer IndexDataBuffer = ByteBuffer
				.allocateDirect(indexBuffer.length * 2).order(ByteOrder.nativeOrder())
				.asShortBuffer();
	IndexDataBuffer.put(indexBuffer).position(0);



	if (vbo[3] > 0 && ibo[3] > 0) {
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[3]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VertexDataBuffer.capacity() * 4,
					VertexDataBuffer, GLES20.GL_STATIC_DRAW);

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[3]);
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
					* 2, IndexDataBuffer, GLES20.GL_STATIC_DRAW);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	} */
}

/***********************************************************************************
�Ӻ���������DrawBackPanel(), ����ʾ����������˵�����֡���λ�Լ���������̶ȣ�
************************************************************************************/
 void DrawBackPanel(float[] modelMatrix){//boolean AnimationEnabled ){
		
		float[] color = {0.0f,0.3f, 0.3f, 0.3f};		
		float[] color1 = {1.0f,0.0f, 1.0f, 1.0f};		
		float[] color2 = {0.3f,0.3f, 0.3f, 1.0f};
		
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
			GLES20.glUniform4fv(ColorHandle, 1, color2 , 0); 
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
 �Ӻ���������DrawBackPanel(), ����ʾ����������˵�����֡���λ�Լ���������̶ȣ�
 ************************************************************************************/
  void DrawBandArea(float[] modelMatrix){//boolean AnimationEnabled ){
 		
 		float[] color = {0.0f,0.3f, 0.3f, 0.3f};		
 		
 		Matrix.setIdentityM(mMVPMatrix, 0);
 		Matrix.translateM(mMVPMatrix, 0, m_OffSetX, m_OffSetY, 0);	
 		Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
 		

 		  
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

 			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
 			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);			
 			GLES20.glUseProgram(0);
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
			     s = format.format((float)(m_OriginValueY[i]+(j)*m_DivValueY[i])); //ת�����ַ���
			     m_FontGraph.draw( s , x+m_OffSetX,y+m_OffSetY, z); 
			}	
			m_FontGraph.RenderFont();
		}
		m_FontGraph.SetColor( 0.0f, 1.0f, 1.0f, 1.0f );  
		for(j=0; j<=m_DivNumX; j++){			
	         //glWindowPos2i(m_OffSetX+m_GraphOffsetX+m_GraphUnitWidth*j-m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-m_GraphOffsetY-m_GraphHeight-m_GraphLabelHeight-m_OffSetY);
		      s = format.format((float)(m_OriginValueX+(j)*m_DivValueX)); //ת�����ַ���
		      m_FontGraph.draw( s  , m_GraphOffsetX+m_GraphUnitWidth*j-m_GraphLabelWidth*0.5f+m_OffSetX,m_GraphOffsetY-18.0f+m_OffSetY, 0); 
			  
       }
		m_FontGraph.RenderFont();
		GLES20.glDisable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
 }
/***********************************************************************************
�Ӻ���������DrawData(), ����ʾ����������˵�����֡���λ�Լ���������̶ȣ�
************************************************************************************/
void DrawData(float[] modelMatrix){

	 int i,j,k;
     float tempX,tempY;
     float x,y,z;

 	float vertexBuffer[] = new float[(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*m_CurveNum*6];
 	short indexBuffer[] = new short[(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*m_CurveNum*6];
     
if(m_RefreshMode ==0){
	 for(i=0;i<m_CurveNum;i++){

		 m_EffectDataStartIndex[i] =0;
		  for(j=m_DisplayDataStartTimeIndex;j<=m_DisplayDataEndTimeIndex;j++){	   

			   tempX=m_GraphOffsetX+(m_RecievedTime[j]-m_OriginValueX)*m_TimeDrawCof;
			   tempY=m_GraphOffsetY+(m_RecievedData[i][j]-m_OriginValueY[i])*m_DataDrawCof[i];
			   if(m_RecievedData[i][j]>m_OriginValueY[i]+(m_DivNumY)*m_DivValueY[i])
				   tempY=m_GraphOffsetY+ m_GraphHeight;
			   if(m_RecievedData[i][j]<m_OriginValueY[i])
				   tempY=m_GraphOffsetY;//+m_GraphHeight;
			   if(m_RecievedTime[j]<(m_OriginValueX-m_DivValueX) && tempX < m_GraphOffsetX){
				   
				   tempX =m_GraphOffsetX+(m_RecievedTime[j+1]-m_OriginValueX)*m_TimeDrawCof; 
				   if(tempX < m_GraphOffsetX) {
					   tempX=m_GraphOffsetX;
					   tempY=m_GraphOffsetY;
					   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6]= tempX;
					   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1]= tempY;
					   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2]= 0;
					   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6 );
					   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1);
					   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2);
					   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3]= tempX;
					   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4]= tempY;
					   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+5]= 0;
					   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3 );
					   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4);
					   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+5] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6+(j-m_DisplayDataStartTimeIndex)*6+5);
					   m_EffectDataStartIndex[i] +=2;	      
					   continue;
				   }
				   else{
					   tempX = m_GraphOffsetX;   
					   
				   }
			   }
			   else if(tempX < m_GraphOffsetX && j<m_DisplayDataEndTimeIndex){

					   tempX =m_GraphOffsetX+(m_RecievedTime[j+1]-m_OriginValueX)*m_TimeDrawCof; 
					   if(tempX < m_GraphOffsetX) {
					       tempX=m_GraphOffsetX;
					       tempY=m_GraphOffsetY;
						   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6]= tempX;
						   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1]= tempY;
						   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2]= 0;
						   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6 );
						   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1);
						   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2);
						   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3]= tempX;
						   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4]= tempY;
						   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+5]= 0;
						   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3 );
						   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4);
						   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+5] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6+(j-m_DisplayDataStartTimeIndex)*6+5);
						   m_EffectDataStartIndex[i] += 2;	  
						   continue;
					   }
					   else{
					      tempX = m_GraphOffsetX;
					   }

			   }

			   if(m_RecievedTime[j]> m_OriginValueX+(m_DivNumX)*m_DivValueX)
				   tempX=m_GraphOffsetX+m_GraphWidth;

			   x = tempX;
			   y = tempY;
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6]= x;
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1]= y;
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2]= 0;
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6 );
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+1);
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+2);
			   
			   if(j<m_DisplayDataEndTimeIndex){

				   if ((m_RecievedTime[j]-m_OriginValueX)*m_TimeDrawCof >= 0)
				   {
					   x =  tempX + (m_RecievedTime[j+1] -m_RecievedTime[j])*m_TimeDrawCof;
					   y = tempY;
				   }
			       else
			       {
					   x = tempX + (m_RecievedTime[j+1] -m_OriginValueX)*m_TimeDrawCof;
					   y = tempY;
			       }
			   }
			   
			   if(x>=m_GraphOffsetX+m_GraphWidth)
				   x=m_GraphOffsetX+m_GraphWidth;
			   
			   z = 0;
			   
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3]= x;
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4]= y;
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+5]= z;
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+3 );
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+4);
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6 +(j-m_DisplayDataStartTimeIndex)*6+5] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*6+(j-m_DisplayDataStartTimeIndex)*6+5);
		  }           

	  } 
  }
  else{

	 for(i=0;i<m_CurveNum;i++){
	 
		  for(j=m_DisplayDataStartTimeIndex;j<=m_DisplayDataEndTimeIndex;j++){	   

			   tempX=m_GraphOffsetX+(m_RecievedTime[j]-m_OriginValueX)*m_TimeDrawCof;
			   tempY=m_GraphOffsetY+(m_RecievedData[i][j]-m_OriginValueY[i])*m_DataDrawCof[i];
			   if(m_RecievedData[i][j]>m_OriginValueY[i]+(m_DivNumY)*m_DivValueY[i])
				   tempY=m_GraphOffsetY+m_GraphHeight;
			   if(m_RecievedData[i][j]<m_OriginValueY[i])
				   tempY=m_GraphOffsetY;
			   if(m_RecievedTime[j]<(m_OriginValueX))//-m_DivValueX))//+(m_DivNumY)*m_DivValueY[i])
			   {
				   tempX=m_GraphOffsetX;               
			   }

			   if(m_RecievedTime[j]> m_OriginValueX+(m_DivNumX)*m_DivValueX)
				   tempX=m_GraphOffsetX+m_GraphWidth;
	  
			   x = tempX;
			   y = tempY;
			   z = 0;
				  
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3]= x;
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3+1]= y;
			   vertexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3+2]= z;
			   
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3 );
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3+1] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3+1);
			   indexBuffer[i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3+2] =(short)(i*(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*3 +(j-m_DisplayDataStartTimeIndex)*3+2);			   
		  }           
	  } 

 }
 
	
final FloatBuffer VertexDataBuffer = ByteBuffer
			.allocateDirect(vertexBuffer.length * 4).order(ByteOrder.nativeOrder())
			.asFloatBuffer();
VertexDataBuffer.put(vertexBuffer).position(0);
	
final ShortBuffer IndexDataBuffer = ByteBuffer
			.allocateDirect(indexBuffer.length * 2).order(ByteOrder.nativeOrder())
			.asShortBuffer();
IndexDataBuffer.put(indexBuffer).position(0);



if (vbo[1] > 0 && ibo[1] > 0) {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VertexDataBuffer.capacity() * 4,
				VertexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
				* 2, IndexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
} 

Matrix.setIdentityM(mMVPMatrix, 0);
Matrix.translateM(mMVPMatrix, 0, m_OffSetX, m_OffSetY, 0);	
Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
//Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
  
if (vbo[1] > 0 && ibo[1] > 0) {		
	//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	GLES20.glUseProgram(program[0]);
	ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);
	
	BoundaryHandle          = GLES20.glGetUniformLocation(program[0], "u_Boundary");		
	GLES20.glUniform4fv(BoundaryHandle, 1, mBoundary , 0); 
	GLES20.glEnableVertexAttribArray(BoundaryHandle);		
	
	// Set program handles for cube drawing.
	mvpMatrixUniform = GLES20.glGetUniformLocation(program[0], MVP_MATRIX_UNIFORM);
	GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
	positionAttribute = GLES20.glGetAttribLocation(program[0], POSITION_ATTRIBUTE);	
	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[1]);

	
	// Bind Attributes
	GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,
			3*4, 0);
	GLES20.glEnableVertexAttribArray(positionAttribute);

	GLES20.glLineWidth(1.0f);
	// Draw
	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
	m_DataColor[0] = new float[]{1.0f, 0.0f, 0.0f,1.0f};
	m_DataColor[1] = new float[]{0.0f, 1.0f, 0.0f,1.0f};
	m_DataColor[2] = new float[]{1.0f, 0.0f, 1.0f,1.0f};
    m_DataColor[3] = new float[]{1.0f, 1.0f, 0.0f,1.0f};

	for(i =0; i<m_CurveNum;i++){
		ColorHandle          = GLES20.glGetUniformLocation(program[0], COLOR_UNIFORM);
		GLES20.glUniform4fv(ColorHandle, 1, m_DataColor[i] , 0); 
		GLES20.glEnableVertexAttribArray(ColorHandle);		
		if(m_RefreshMode == 0)
			GLES20.glDrawElements(GLES20.GL_LINE_STRIP, ((m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*2 -(int)m_EffectDataStartIndex[i]), GLES20.GL_UNSIGNED_SHORT,(int)m_EffectDataStartIndex[i]*2 + (m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*4*i);
		else
			GLES20.glDrawElements(GLES20.GL_LINE_STRIP, ((m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)), GLES20.GL_UNSIGNED_SHORT, (m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*2*i);		
			//GLES20.glDrawElements(GLES20.GL_LINE_STRIP, ((m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*2), GLES20.GL_UNSIGNED_SHORT,(m_DisplayDataEndTimeIndex - m_DisplayDataStartTimeIndex +1)*2);
	
	}	
	
	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);	
	GLES20.glUseProgram(0);
}

}

/***********************************************************************************
�Ӻ���������RefreshDisplay(), ˢ��ʾ������ʾ��X,Y�������Լ����Ƶ����߶�̬ˢ�£�
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
�Ӻ���������ReciedveData(), �������ݣ�X,Y������ֵ��
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
		for(i=0;i<m_CurveNum;i++){ 	
	        m_RecievedData[i][m_LatestTimeIndex]=m_RecievedDataToRealDataScale*Data[i];	
			m_MaxRecievedData[i]=m_RecievedData[i][m_LatestTimeIndex];// Data[i];
			m_MinRecievedData[i]= m_RecievedData[i][m_LatestTimeIndex];//Data[i];
		}
		m_StartRecieve=true;
	}
	else{

		if(m_LatestTimeIndex<m_DisplayDataMaxTimeIndex){
		    m_LatestTimeIndex++;	
		}
		else{
		    m_LatestTimeIndex=m_DisplayDataMaxTimeIndex;
		    for(i=0;i<m_DisplayDataMaxTimeIndex;i++){ 
			    for(j=0;j<m_CurveNum;j++)
                     m_RecievedData[j][i]= m_RecievedData[j][i+1];
				m_RecievedTime[i]= m_RecievedTime[i+1];
				
			}
		    // m_RecievedTime[m_LatestTimeIndex]=m_RecievedTimeToRealTimeScale*Time;
			// m_RecievedData[j][m_LatestTimeIndex]=m_RecievedDataToRealDataScale*Data[i];		
		
		}
        m_RecievedTime[m_LatestTimeIndex]=m_RecievedTimeToRealTimeScale*Time;
	    if(m_MaxRecievedTime<m_RecievedTime[m_LatestTimeIndex])
			 m_MaxRecievedTime=m_RecievedTime[m_LatestTimeIndex];

		if(m_MinRecievedTime>m_RecievedTime[m_LatestTimeIndex])
			 m_MinRecievedTime=m_RecievedTime[m_LatestTimeIndex];

		for(i=0;i<m_CurveNum;i++){ 	
	         m_RecievedData[i][m_LatestTimeIndex]=m_RecievedDataToRealDataScale*Data[i];	
	         if(m_MaxRecievedData[i]<m_RecievedData[i][m_LatestTimeIndex])
				m_MaxRecievedData[i]=m_RecievedData[i][m_LatestTimeIndex];

             if(m_MinRecievedData[i]>m_RecievedData[i][m_LatestTimeIndex])
				m_MinRecievedData[i]=m_RecievedData[i][m_LatestTimeIndex];	
	    }

	}

}
/***********************************************************************************
�Ӻ���������UIControlUnit(), ��ʼ��
************************************************************************************/
OscilloScope(Context context){
	
	super(context,BUTTON_SQUARE,0.0f,0.0f,1.0f,4.0f); //m_TextRenderList=DEFAULT_CAPTION_DISPLAYLIST;
	 m_ControlType =CONTROL_UNIT_OSCILLO;
	ShaderRelatedInit(m_Context);
}
/***********************************************************************************
�Ӻ���������UIControlUnit(), ��ʼ��
************************************************************************************/
OscilloScope(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float BorderWith){
	 
	  super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);
	  m_Width=ContrlUnitWidth[ControlType]*m_Scale;
	  m_Height=ContrlUnitHeight[ControlType]*m_Scale;
	  m_ControlType =CONTROL_UNIT_OSCILLO;
	  ShaderRelatedInit(m_Context);

}
/***********************************************************************************
�Ӻ���������UIControlUnit(), ��ʼ��
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
	GLES20.glGenBuffers(2, vbo, 0);
	GLES20.glGenBuffers(2, ibo, 0);	
	
}
/***********************************************************************************
�Ӻ���������~OscilloScope(), ע��
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
�Ӻ���������IsOnFocus(), �Ƿ��ÿ���Ȩ
************************************************************************************/
@Override
boolean IsOnFocus(){
    return m_IsOnfocus;
}
 /***********************************************************************************
 �Ӻ���������GetEndIndex(), ���һ�����ݵ�����
 ************************************************************************************/
float GetEndIndex(){
    return (float)m_DisplayDataEndTimeIndex;//m_RecievedData[m_LatestTimeIndex];
}
 /***********************************************************************************
 �Ӻ���������SetScopeParameters(), ������ʼ��
 ************************************************************************************/
//void SetScopeParameters(float OffsetX, float OffsetY,float Height, float Width, int CurveNum, String[] CurveLabelString, int[] Color,float DataSampleTimeInterval,int DataSize,int DivNumX, int DivNumY, boolean GridOn){
public void SetScopeParameters(float GraphHeight, float GraphWidth, int CurveNum){//, String[] CurveLabelString, int[] Color,float DataSampleTimeInterval,int DataSize,int DivNumX, int DivNumY, boolean GridOn){
		        
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
      m_DivNumX=4;//DivNumX; 
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
	  InitGLDataForBorder();
	  InitGLDataForArea();
	 InitGLDataForBackPanel();
	 
}


public void SetDispWiodowSize(int width, int height)
{
	mWindowWidth = width;
	mWindowHeight = height;
}
/***********************************************************************************
 �Ӻ���������AddCaption(), �����ǩ
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
 �Ӻ���������DrawScaleRullerX(),���ƿ�ѡ��X������
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
 �Ӻ���������DrawScaleRullerY(),������ѡ��Y�����䳤��
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
 �Ӻ���������DrawScaleRullerXY(),���ƿ�ѡ����
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
 �Ӻ���������Render(),��������ʾ����ģ��
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
	 DrawData(modelMatrix);	
	 DrawBandArea(modelMatrix);
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
    String s = format.format((float)(m_ActiveMouseX)); //ת�����ַ���
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-20, 0); 
    
	s = format.format((float)(m_ReleaseMouseX)); //ת�����ַ���
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-40, 0); 
    	
  	s = format.format((float)(m_ActiveMouseX1)); //ת�����ַ���
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-60, 0); 
    
	s = format.format((float)(m_ReleaseMouseX1)); //ת�����ַ���
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-80, 0); 
    
  	s = format.format((float)(m_ActiveMouseY)); //ת�����ַ���
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor-300, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-20, 0); 
    
	s = format.format((float)(m_ReleaseMouseY)); //ת�����ַ���
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor-300, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-40, 0); 
    	
  	s = format.format((float)(m_ActiveMouseY1)); //ת�����ַ���
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor-300, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-60, 0); 
    
	s = format.format((float)(m_ReleaseMouseY1)); //ת�����ַ���
  	m_Font.draw( s ,m_OffSetX -(m_BorderWidth)/2.0f  -s.length()*0.5f*20*m_FontSizeScaleFactor-300, m_OffSetY -(-m_Height*m_Scale-m_BorderWidth)/2.0f -50*m_FontSizeScaleFactor-80, 0); 
   */ 	
  	m_Font.RenderFont();
	GLES20.glDisable(GLES20.GL_BLEND);

 }
/***********************************************************************************
 �Ӻ���������UserKeyInput(),��ȡ�û���������
 ************************************************************************************/
void UserKeyInput(int InputKey){

}

/***********************************************************************************
 �Ӻ���������UserMouseMove(),����ƶ��¼�
 ************************************************************************************/
void UserMouseMove(int pointerId,float wParam, float lParam){
   /* m_ReleaseMouseX = ( short )( lParam );
    m_ReleaseMouseY = ( short )( lParam );
	*/
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
			
			m_OriginValueX = m_OriginValueX-(((float)m_ReleaseMouseX - (float)m_ActiveMouseX))/m_TimeDrawCof;;
		    m_MinRecievedTime=m_OriginValueX;
			m_MaxRecievedTime=m_MinRecievedTime+m_DisplayDataTimeLength;		
			 
			for(int i=0;i<m_CurveNum;i++){ 
	
				m_OriginValueY[i] = m_OriginValueY[i] - (((float)m_ReleaseMouseY - (float)m_ActiveMouseY))/m_DataDrawCof[i];
				
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
		 
			float scaleX =  0.0001f+Math.abs( ( (float)m_ReleaseMouseX1 - (float)m_ReleaseMouseX )/( (float)m_ActiveMouseX1-  (float)m_ActiveMouseX+0.00001f)  );
			
			m_OriginValueX = (float)m_UserSelectedStartX1 - (float)m_UserSelectedEndX1/scaleX;
			m_DisplayDataTimeLength =  (float)m_UserSelectedEndX/scaleX;
		     m_MinRecievedTime=m_OriginValueX;
			 m_MaxRecievedTime=m_MinRecievedTime+m_DisplayDataTimeLength;		
			 
			 
				for(int i=0;i<m_CurveNum;i++){ 
					
					float scaleY =  0.0001f+Math.abs( ( (float)m_ReleaseMouseY1 - (float)m_ReleaseMouseY )/( (float)m_ActiveMouseY1-  (float)m_ActiveMouseY+0.00001f)  );
					
					m_OriginValueY[i] = m_UserSelectedStartY1[i] - m_UserSelectedEndY1[i]/scaleY;
					m_DisplayDataRange[i] =  m_UserSelectedEndY[i]/scaleY;
					m_MinRecievedData[i]=m_OriginValueY[i];
					m_MaxRecievedData[i]=m_MinRecievedData[i]+m_DisplayDataRange[i];					
				}
			 
				RefreshDisplay();
			 
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
 �Ӻ���������UserMouseDown(),������¼�
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
 �Ӻ���������UserMouseUp(),����ͷ��¼�
 ************************************************************************************/	 
void  UserMouseUp(int pointerId,float wParam, float lParam){
	
	
	 if(pointerId ==0){  
		 m_IsOnfocus=false;
		 m_IsActive =false;
		m_IsOnDrag=false;

	 }
	 else
	 {
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



};