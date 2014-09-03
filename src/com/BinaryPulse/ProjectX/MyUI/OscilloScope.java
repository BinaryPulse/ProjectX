package com.BinaryPulse.ProjectX.MyUI;
import java.lang.Math;
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
  /*
  public:

	OscilloScope();
	OscilloScope(int ControlType,float OffSetX,float OffSetY,float Scale, float BorderWidth);
	OscilloScope(int ControlType,float OffSetX,float OffSetY,float Scale,float Width, float Heigh, float BorderWidth);	
	virtual ~OscilloScope();//{	//delete m_TextRenderList};
	void AddCaption(char* TextString,int TextLength,float FontSize,GLint FontList);//, float OffSetX,float OffSetY,int FontSize);
	virtual void  Render();
	void DrawControlBorder(bool AnimationEnabled);
    void DrawControlArea(bool AnimationEnabled);

	void SetUpTextureObject(GLuint TextureObject1,GLuint TextureObject2){m_TextureObject1=TextureObject1;m_TextureObject2=TextureObject2;}
    
	void OnEventProcess(){};
    virtual void  UserKeyInput(int InputKey);
    virtual void  UserMouseMove(WPARAM wParam, LPARAM lParam);
    virtual void  UserMouseDown(WPARAM wParam, LPARAM lParam);	 
    virtual void  UserMouseUp(WPARAM wParam, LPARAM lParam);
	virtual bool IsOnFocus();

	void RefreshDisplay(void);
	void DrawData(void);
	void DrawBackPanel(void);

	void ReciedveData(float Time, float *Data);
	void SetScopeParameters(float OffsetX, float OffsetY,float Height, float Width, int CurveNum, char** CurveLabelString,int* Color,float DataSampleTimeInterval, MyFont * ScopeLabelFontList,int DataSize=10000,int DivNumX=10, int DivNumY=10,  bool GridOn=true);

	void DrawScaleRullerX(void);
	void DrawScaleRullerXY(void);
	void DrawScaleRullerY(void);
	float GetEndIndex(void);
	
	 
  protected:*/
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

	protected int m_DisplayDataStartTimeIndex;
	protected int m_DisplayDataEndTimeIndex;
	protected int m_DisplayDataMaxTimeIndex;
	protected int m_LatestTimeIndex;
	protected float m_DisplayDataTimeLength;
	protected float[] m_RecievedTime;//[];
	protected float[][] m_RecievedData;//[];//[];
	protected float m_TimeDrawCof;
	protected float[] m_DataDrawCof;//[];
	protected float[] m_DisplayDataRange;//[];
	protected float[] m_MaxRecievedData;//[];
	protected float[] m_MinRecievedData;//[];;

	protected int m_ActiveMouseX;
	protected int m_ActiveMouseY;

	protected int m_ReleaseMouseX;
	protected int m_ReleaseMouseY;

	protected float m_UserSelectedStartX;
	protected float m_UserSelectedEndX;
	protected float[] m_UserSelectedStartY;//[];
	protected float[] m_UserSelectedEndY;//[];

	protected float m_DataSampleTimeInterval;

	protected boolean m_IsResetAxisX;
	protected boolean m_IsResetAxisY;
	protected boolean m_IsResetAxisXY;

	protected boolean m_AutomaticDisplay; 
	protected boolean m_StartRecieve;

	protected float m_RecievedTimeToRealTimeScale;
	protected float m_RecievedDataToRealDataScale;

	protected float m_MaxRecievedTime;
	protected float m_MinRecievedTime;
	protected boolean m_RefreshMode;


	/** This is a handle to our cube shading program. */
	private int program;						   // OpenGL Program object
	private int ColorHandle;						   // Shader color handle	
	private int TextureUniformHandle;                 // Shader texture handle
	private int TextureMoveUniformHandle;
	private int textureId; 
	/** OpenGL handles to our program uniforms. */
	private int mvpMatrixUniform;
	private int mvMatrixUniform;
	private int lightPosUniform;

	
	/** OpenGL handles to our program attributes. */
	private int positionAttribute;
	private int texcordAttribute;
	private int colorAttribute;	

	/** Identifiers for our uniforms and attributes inside the shaders. */	
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
	private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";

	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String NORMAL_ATTRIBUTE = "a_Normal";
	private static final String TEXCORD_ATTRIBUTE = "a_TexCoordinate";
	private static final String TEXTURE_UNIFORM = "u_Texture";
	private static final String COLOR_UNIFORM = "u_Color";
	private static final String TEXTUREMOV_UNIFORM = "u_Texmove";
	
	final int[] vbo = new int[2];
	final int[] ibo = new int[1];
	
	//private float[] mVPMatrix;		
	private float[] mMVPMatrix = new float[16];	
	private float[] mcolor;
	

    protected int mWindowWidth;
    protected int mWindowHeight;

/*##############################################################################
           
		         对象模块功能描述： OscilloScope（示波器）

###############################################################################*/

/***********************************************************************************
子函数描述：DrawControlBorder(bool AnimationEnabled), 绘制示波器边框
************************************************************************************/
void  DrawControlBorder(float[] modelMatrix){//boolean AnimationEnabled ){
	
	float[] color = {0.0f,1.0f, 1.0f, 1.0f};
	
	Matrix.setIdentityM(mMVPMatrix, 0);
	Matrix.translateM(mMVPMatrix, 0, -mWindowWidth/2.0f, -mWindowHeight/2.0f, 0);	
	Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
	
	  if(m_IntervalCoordinate<0.48f && m_IntervalCoordinate>=0f)

	       m_IntervalCoordinate+=0.0025f;
     else
          m_IntervalCoordinate=0.0f;  
	  
	if (vbo[0] > 0 && ibo[0] > 0) {		
		
		GLES20.glUseProgram(program);
		ColorHandle          = GLES20.glGetUniformLocation(program, COLOR_UNIFORM);
        TextureUniformHandle = GLES20.glGetUniformLocation(program, TEXTURE_UNIFORM);
        TextureMoveUniformHandle = GLES20.glGetUniformLocation(program, TEXTUREMOV_UNIFORM);
        
		GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
		GLES20.glEnableVertexAttribArray(ColorHandle);
		
		GLES20.glUniform1f(TextureMoveUniformHandle, m_IntervalCoordinate);//(, 1, mcolor , 0); 
		GLES20.glEnableVertexAttribArray(TextureMoveUniformHandle);	
		
 	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // Set the active texture unit to texture unit 0
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
		GLES20.glUniform1i(TextureUniformHandle, 0); 
	
		// Set program handles for cube drawing.
		mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
		//mvMatrixUniform = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
		//lightPosUniform = GLES20.glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);
		positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
		//normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
		texcordAttribute = GLES20.glGetAttribLocation(program, TEXCORD_ATTRIBUTE);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);


		//GLES20.glVertexAttribPointer(TextureUniformHandle, 2, GLES20.GL_FLOAT, false,
		//		4*4, 2*4);
		//GLES20.glEnableVertexAttribArray(TextureUniformHandle);
		GLES20.glVertexAttribPointer(texcordAttribute, 2, GLES20.GL_FLOAT, false,
				5*4, 0);
		GLES20.glEnableVertexAttribArray(texcordAttribute);			
		
		// Bind Attributes
		GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,
				5*4, 2*4);
		GLES20.glEnableVertexAttribArray(positionAttribute);
	
		// Draw
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 12, GLES20.GL_UNSIGNED_SHORT, 0);
		//GLES20.glDrawElements(GLES20.GL_LINES, 4, GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GLES20.glUseProgram(0);
	}
}

public void InitGLDataForBorder()
{
	  
    // initiate vertex buffer for border 	
	float zaxis = 0.0f;
	float vertexBuffer[] = {
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
	};

    short indexBuffer[]  ={0,1,2,3,4,5,6,7,8,9,10,11};	
		
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
子函数描述：DrawControlArea(bool AnimationEnabled), 绘制示波器控制区域
************************************************************************************/
void DrawControlArea(boolean AnimationEnabled){
	
   /*      
      glEnable(GL_BLEND);  
	  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);			 
	  glColor4f(0.0,0.0,0.5,0.8);	

	  if(m_PositionCoordinate>0.03)
	       m_PositionCoordinate-=0.003;//MoveStep;
	  else
           m_PositionCoordinate=0.86;	    
		
	  glBegin(GL_TRIANGLE_STRIP);
		  //glTexCoord2f(0.5-m_PositionCoordinate*0.5,0.5-0.5*m_PositionCoordinate);
	       glVertex3f(m_BorderWidth, m_BorderWidth ,0.0);          
		  //glTexCoord2f(0.5-m_PositionCoordinate*0.5,0.5+0.5*m_PositionCoordinate);
		  glVertex3f(m_BorderWidth, m_BorderWidth+m_Height*m_Scale ,0.0);          
		  //glTexCoord2f(0.5+m_PositionCoordinate*0.5,0.5-0.5*m_PositionCoordinate);
		  glVertex3f(m_Width*m_Scale+m_BorderWidth, m_BorderWidth ,0.0);          
		  //glTexCoord2f(0.5+0.5*m_PositionCoordinate,0.5+0.5*m_PositionCoordinate);
		  glVertex3f(m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth ,0.0);
	  glEnd();
      glDisable(GL_BLEND);
  */
}

/***********************************************************************************
子函数描述：DrawBackPanel(), 绘制示波器背景（说明文字、单位以及绘制网格刻度）
************************************************************************************/
 void DrawBackPanel(float[] modelMatrix){//boolean AnimationEnabled ){
		
		float[] color = {0.0f,1.0f, 1.0f, 1.0f};
		
		float[] color1 = {1.0f,0.0f, 1.0f, 1.0f};
		
		float[] color2 = {0.3f,0.3f, 0.3f, 1.0f};
		
		Matrix.setIdentityM(mMVPMatrix, 0);
		Matrix.translateM(mMVPMatrix, 0, m_OffSetX/2.0f, m_OffSetY/2.0f, 0);	
		Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
		
		  if(m_IntervalCoordinate<0.48f && m_IntervalCoordinate>=0f)

		       m_IntervalCoordinate+=0.0025f;
	     else
	          m_IntervalCoordinate=0.0f;  
		  
		if (vbo[0] > 0 && ibo[0] > 0) {		
			
			GLES20.glUseProgram(program);
			ColorHandle          = GLES20.glGetUniformLocation(program, COLOR_UNIFORM);
	        //TextureUniformHandle = GLES20.glGetUniformLocation(program, TEXTURE_UNIFORM);
	        //TextureMoveUniformHandle = GLES20.glGetUniformLocation(program, TEXTUREMOV_UNIFORM);
	        
			GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
			GLES20.glEnableVertexAttribArray(ColorHandle);
			
			//GLES20.glUniform1f(TextureMoveUniformHandle, m_IntervalCoordinate);//(, 1, mcolor , 0); 
			//GLES20.glEnableVertexAttribArray(TextureMoveUniformHandle);	
			
	 	    //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // Set the active texture unit to texture unit 0
			//GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
			// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
			//GLES20.glUniform1i(TextureUniformHandle, 0); 
		
			// Set program handles for cube drawing.
			mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
			GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
			//mvMatrixUniform = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
			//lightPosUniform = GLES20.glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);
			positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
			//normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
			//texcordAttribute = GLES20.glGetAttribLocation(program, TEXCORD_ATTRIBUTE);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);


			//GLES20.glVertexAttribPointer(texcordAttribute, 2, GLES20.GL_FLOAT, false,
			//		5*4, 0);
			//GLES20.glEnableVertexAttribArray(texcordAttribute);			
			
			// Bind Attributes
			GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,
					3*4, 0);
			GLES20.glEnableVertexAttribArray(positionAttribute);
		
			GLES20.glLineWidth(3.0f);
			// Draw
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
			GLES20.glDrawElements(GLES20.GL_LINE_LOOP, (5), GLES20.GL_UNSIGNED_SHORT, 0);
			GLES20.glDrawElements(GLES20.GL_LINES,  (m_DivNumY)*2, GLES20.GL_UNSIGNED_SHORT, 5*2);	
			GLES20.glDrawElements(GLES20.GL_LINES,  (m_DivNumX)*2, GLES20.GL_UNSIGNED_SHORT, 5*2 + (m_DivNumY)*4);
			
			ColorHandle          = GLES20.glGetUniformLocation(program, COLOR_UNIFORM);
			GLES20.glUniform4fv(ColorHandle, 1, color2 , 0); 
			GLES20.glLineWidth(1.0f);
			GLES20.glDrawElements(GLES20.GL_LINES,  (m_DivNumY)*2, GLES20.GL_UNSIGNED_SHORT, 5*2 + (m_DivNumY+m_DivNumX)*4);
			GLES20.glDrawElements(GLES20.GL_LINES,  (m_DivNumX)*2, GLES20.GL_UNSIGNED_SHORT, 5*2 + (2*m_DivNumY+m_DivNumX)*4);
			
			ColorHandle          = GLES20.glGetUniformLocation(program, COLOR_UNIFORM);
			GLES20.glUniform4fv(ColorHandle, 1, color1 , 0); 
			for(int i =0;i<m_CurveNum;i++)
				GLES20.glDrawElements(GLES20.GL_LINES, (4+m_DivNumY*2), GLES20.GL_UNSIGNED_SHORT, ((m_DivNumY+m_DivNumX)*4 +5 +(4*i+m_DivNumY*2*i))*2);
			//GLES20.glDrawElements(GLES20.GL_LINES, (4+m_DivNumY*2), GLES20.GL_UNSIGNED_SHORT, ((m_DivNumY+m_DivNumX)*4 +5 )*2);
			//GLES20.glDrawElements(GLES20.GL_LINES, (4+m_DivNumY*2), GLES20.GL_UNSIGNED_SHORT, ((m_DivNumY+m_DivNumX)*4 +5 + (4+m_DivNumY*2))*2);
			//GLES20.glDrawElements(GLES20.GL_LINES, (4+m_DivNumY*2), GLES20.GL_UNSIGNED_SHORT, ((m_DivNumY+m_DivNumX)*4 +5 + (8+m_DivNumY*4))*2);
			//GLES20.glDrawElements(GLES20.GL_LINES, (4+m_DivNumY*2), GLES20.GL_UNSIGNED_SHORT, ((m_DivNumY+m_DivNumX)*4 +5 + (12+m_DivNumY*6))*2);
			//GLES20.glDrawElements(GLES20.GL_LINES, ( m_CurveNum*(m_DivNumY*2)), GLES20.GL_UNSIGNED_SHORT, ((m_DivNumY+m_DivNumX)*4 +5+(m_CurveNum*2) )*3);
			//GLES20.glDrawElements(GLES20.GL_LINES, (5 + (m_DivNumY+m_DivNumX)*4 + m_CurveNum*(2+m_DivNumY*2)), GLES20.GL_UNSIGNED_SHORT, 0);
			//GLES20.glDrawElements(GLES20.GL_LINES, 4, GLES20.GL_UNSIGNED_SHORT, 0);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			GLES20.glUseProgram(0);
		}
   /*
	  //1.Draw X axis and Y axis 
	   int i;
   	   glColor4f(0.0,0.3,0.3,0.2);	
	   glLineWidth(m_AxisLineWidth);
	   glBegin(GL_LINE_STRIP);			  
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY ,0.0);
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY+m_GraphHeight ,0.0);
			  glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY+m_GraphHeight ,0.0);
			  glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY ,0.0);
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY ,0.0);          
	   glEnd();

	   glBegin(GL_LINES);			  
		   for( i =0;i<m_DivNumY;i++){			
			   glVertex3f(m_GraphOffsetX, m_GraphOffsetY+m_GraphUnitHeight*i,0.0);
			   glVertex3f(m_GraphOffsetX+15, m_GraphOffsetY+m_GraphUnitHeight*i ,0.0);
		
		    }
		    for( i =0;i<m_DivNumX;i++){
			   glVertex3f(m_GraphOffsetX+m_GraphUnitWidth*(i+1), m_GraphOffsetY+m_GraphHeight,0.0);
			   glVertex3f(m_GraphOffsetX+m_GraphUnitWidth*(i+1), m_GraphOffsetY+m_GraphHeight-15 ,0.0);
		
		    }	
	   glEnd();

       //2. Draw Grid
	   if(m_GridOn){

			glLineWidth(m_GridLineWidth);
			glEnable(GL_LINE_STIPPLE);
			glLineStipple(1.0f,0xFF00);
			glBegin(GL_LINES);			  
		        for( i =0;i<m_DivNumY;i++){			
					glVertex3f(m_GraphOffsetX, m_GraphOffsetY+m_GraphUnitHeight*i,0.0);
					glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY+m_GraphUnitHeight*i ,0.0);		
		        }
				for( i =0;i<m_DivNumX;i++){
					glVertex3f(m_GraphOffsetX+m_GraphUnitWidth*(i+1), m_GraphOffsetY+m_GraphHeight,0.0);
					glVertex3f(m_GraphOffsetX+m_GraphUnitWidth*(i+1), m_GraphOffsetY ,0.0);		
				}		
	        glEnd();
     		glDisable(GL_LINE_STIPPLE);

		}

		//3.Draw Y axis Labels
		for(i=0;i<m_CurveNum;i++){
			glColor4f(float((m_Color[i] & 0x0F00)>>8)/15,float((m_Color[i] & 0x00F0)>>4)/15,float((m_Color[i] & 0x00F))/15,1.0);
			glListBase(m_ScopeLabelFontList);
			//Draw Label Name  
			/*
			if(i<2)
				glWindowPos2i(m_OffSetX+m_GraphOffsetX-(6*i+5.5)*m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-m_GraphOffsetY+1.2*m_GraphLabelHeight-m_OffSetY);//m_OffSetX+m_GraphOffsetX-(4*i+3.5)*m_GraphLabelWidth, 
			else
				glWindowPos2i(m_OffSetX+ m_Width*m_Scale-(6*(i-2)+5.5)*m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-m_GraphOffsetY+1.2*m_GraphLabelHeight-m_OffSetY);//m_OffSetX+m_GraphOffsetX-(4*i+3.5)*m_GraphLabelWidth, 
			glCallLists (strlen(m_LabelStringY[i]), GL_UNSIGNED_BYTE, m_LabelStringY[i]);
            */
	/*		if(i<2)
			    m_DisplayFontList->print(m_OffSetX+m_GraphOffsetX-(6*i+6)*m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-m_GraphOffsetY+2*m_GraphLabelHeight-m_OffSetY,m_LabelStringY[i],strlen( m_LabelStringY[i]));
			else
				m_DisplayFontList->print(m_OffSetX+ m_Width*m_Scale-(6*(i-2)+6)*m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-m_GraphOffsetY+2*m_GraphLabelHeight-m_OffSetY,m_LabelStringY[i],strlen( m_LabelStringY[i]));


			glLineWidth(m_AxisLineWidth*0.5);
		    glColor4f(float((m_Color[i] & 0x0F00)>>8)/15,float((m_Color[i] & 0x00F0)>>4)/15,float((m_Color[i] & 0x00F))/15,1.0);
		    
			
			glBegin(GL_LINES);			  
			if(i<2){
			       glVertex3f(m_GraphOffsetX-(6*i+6)*m_GraphLabelWidth, m_GraphOffsetY ,0.0);
			       glVertex3f(m_GraphOffsetX-(6*i+6)*m_GraphLabelWidth, m_GraphOffsetY+m_GraphHeight ,0.0);
			}
			else{
			       glVertex3f(m_Width*m_Scale-(6*(i-2)+6)*m_GraphLabelWidth, m_GraphOffsetY ,0.0);
			       glVertex3f(m_Width*m_Scale-(6*(i-2)+6)*m_GraphLabelWidth, m_GraphOffsetY+m_GraphHeight ,0.0);
			}

			for( int j=0; j<=m_DivNumY; j++){

			    if(i<2){
			          glVertex3f(m_GraphOffsetX-(6*i+6)*m_GraphLabelWidth, m_GraphOffsetY+m_GraphUnitHeight*j,0.0);
			          glVertex3f(m_GraphOffsetX-(6*i+6)*m_GraphLabelWidth+10, m_GraphOffsetY+m_GraphUnitHeight*j ,0.0);			
				}
				else{
			           
			          glVertex3f(m_Width*m_Scale-(6*(i-2)+6)*m_GraphLabelWidth, m_GraphOffsetY+m_GraphUnitHeight*j,0.0);
			          glVertex3f(m_Width*m_Scale-(6*(i-2)+6)*m_GraphLabelWidth+10, m_GraphOffsetY+m_GraphUnitHeight*j ,0.0);		
			    }		
			}		
	        glEnd();

			//Draw Value String
			 for( int j=0; j<=m_DivNumY; j++){

				  //if(i<2)
			      //     glWindowPos2i(m_OffSetX+m_GraphOffsetX-(6*i+5.5)*m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-(m_GraphOffsetY+m_GraphUnitHeight*j+m_OffSetY));
				  //else
                  //     glWindowPos2i(m_OffSetX+m_Width*m_Scale-(6*(i-2)+5.5)*m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-(m_GraphOffsetY+m_GraphUnitHeight*j+m_OffSetY));

				  if(m_DivValueY[i]<=0.01)
				    sprintf(m_tempLabelString," %7.4f",m_OriginValueY[i]+(m_DivNumY-j)*m_DivValueY[i]);
				  else if(m_DivValueY[i]<=100)
                    sprintf(m_tempLabelString," %7.2f",m_OriginValueY[i]+(m_DivNumY-j)*m_DivValueY[i]);
				  else
                    sprintf(m_tempLabelString," %7.1f",m_OriginValueY[i]+(m_DivNumY-j)*m_DivValueY[i]);

				  //sprintf(tempChar,"%4.1f",m_OriginValueY[i]+(m_DivNumY-j)*m_DivValueY[i]);
		          //glCallLists (strlen( m_tempLabelString), GL_UNSIGNED_BYTE, m_tempLabelString);

				  if(i<2)
			          m_DisplayFontList->print(m_OffSetX+m_GraphOffsetX-(6*i+6)*m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-(m_GraphOffsetY+m_GraphUnitHeight*(j)-m_GraphLabelHeight+m_OffSetY),m_tempLabelString,strlen( m_tempLabelString));
				  else
				      m_DisplayFontList->print(m_OffSetX+m_Width*m_Scale-(6*(i-2)+6)*m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-(m_GraphOffsetY+m_GraphUnitHeight*(j)-m_GraphLabelHeight+m_OffSetY),m_tempLabelString,strlen( m_tempLabelString));


				  
						
			 }
		}
		// Draw X Label Name
		glColor4f(0.0,0.8,0.8,1.0);
		//glWindowPos2i(m_OffSetX+m_GraphOffsetX+0.5*m_GraphUnitWidth*m_DivNumX-0.5*m_GraphLabelWidth,  GetSystemMetrics(SM_CYSCREEN)-(m_GraphOffsetY+m_GraphHeight+2*m_GraphLabelHeight+m_OffSetY));
	    //glCallLists (strlen(m_LabelStringX), GL_UNSIGNED_BYTE, m_LabelStringX); 	
		m_DisplayFontList->print(m_OffSetX+m_GraphOffsetX+0.5*m_GraphUnitWidth*m_DivNumX-0.5*m_GraphLabelWidth,  GetSystemMetrics(SM_CYSCREEN)-(m_GraphOffsetY+m_GraphHeight+1*m_GraphLabelHeight+m_OffSetY),m_LabelStringX,strlen(m_LabelStringX));
		for(int j=0; j<=m_DivNumX; j++){			
	         //glWindowPos2i(m_OffSetX+m_GraphOffsetX+m_GraphUnitWidth*j-m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-m_GraphOffsetY-m_GraphHeight-m_GraphLabelHeight-m_OffSetY);
			 if(m_DivValueX<=0.01)
				  sprintf(m_tempLabelString,"%.4f",m_OriginValueX+(j)*m_DivValueX);
			 else if(m_DivValueX<=100)
                  sprintf(m_tempLabelString,"%.2f",m_OriginValueX+(j)*m_DivValueX);
			 else
                  sprintf(m_tempLabelString,"%.1f",m_OriginValueX+(j)*m_DivValueX);

		     //glCallLists ( strlen( m_tempLabelString)-1, GL_UNSIGNED_BYTE, m_tempLabelString); 							
			 m_DisplayFontList->print(m_OffSetX+m_GraphOffsetX+m_GraphUnitWidth*j-m_GraphLabelWidth, GetSystemMetrics(SM_CYSCREEN)-m_GraphOffsetY-m_GraphHeight-m_OffSetY,m_tempLabelString,strlen( m_tempLabelString)-1);
	    }
*/
}
 public void InitGLDataForBackPanel()
 {
 	  
     // initiate vertex buffer for border 	
	int i,j,k;
 	//float vertexBuffer[] =new float[(5 + (m_DivNumY+m_DivNumX)*4 + m_CurveNum*(2+m_DivNumY*2))*3];
	//Maximum Curve Num is 4 
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
		GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		// TEST: render some strings with the font
		m_Font.SetMvpMatrix(modelMatrix);
		m_Font.SetColor( 1.0f, 0.0f, 0.0f, 1.0f );         // Begin Text Rendering (Set Color WHITE)
		
		//m_Font.drawC("Jason Mraz!", 350.0f, 100.0f, 0.0f, 0, 0, 0);
		m_Font.SetColor( 1.0f, 0.0f, 1.0f, 1.0f );  
		
		
		
		for(i =0;i<m_CurveNum;i++){
			if(i<2){
			  x = m_GraphOffsetX-(i+0.9f)*m_GraphLabelWidth;
			  z =0.0f;	 
			}
			else{
			  x = m_GraphOffsetX+ m_GraphWidth*m_Scale+((i-2)+0.2f)*m_GraphLabelWidth;
			  z = 0.0f;	
			}
			
			for(j =0;j<m_DivNumY+1;j++){		 
				 
				//for(k =0;i<4;i++){
				 if(i<2){
					 y =m_GraphOffsetY+m_GraphUnitHeight*(j-0.1f);

				 }
				 else{
					
					 y = m_GraphOffsetY+m_GraphUnitHeight*(j-0.1f);
	 
				 }
				 
				 m_Font.draw( Integer.toString(i*m_CurveNum+j), x,y, z); 
				//}
			}	
			
		}
		
 }
/***********************************************************************************
子函数描述：DrawData(), 绘制示波器背景（说明文字、单位以及绘制网格刻度）
************************************************************************************/
void DrawData(){

/*	 int i,j,k;
     float tempX,tempY;
     glLineWidth(m_CuveLineWidth);
if(m_RefreshMode ==0){
	 for(i=0;i<m_CurveNum;i++){
	      //glColor4f((i==1?1.0:0.0),((i==0|| i==3)?1.0:0.0),((i==2|| i==3)?1.0:0.0),1.0);//m_Color[i]);	
          glColor4f(float((m_Color[i] & 0x0F00)>>8)/15,float((m_Color[i] & 0x00F0)>>4)/15,float((m_Color[i] & 0x00F))/15,1.0);
		  glBegin(GL_LINE_STRIP);		 
		  for(j=m_DisplayDataStartTimeIndex;j<=m_DisplayDataEndTimeIndex;j++){	   

			   tempX=m_GraphOffsetX+(m_RecievedTime[j]-m_OriginValueX)*m_TimeDrawCof;
			   tempY=m_GraphOffsetY+m_GraphHeight-(m_RecievedData[i][j]-m_OriginValueY[i])*m_DataDrawCof[i];
			   if(m_RecievedData[i][j]>m_OriginValueY[i]+(m_DivNumY)*m_DivValueY[i])
				   tempY=m_GraphOffsetY;
			   if(m_RecievedData[i][j]<m_OriginValueY[i])
				   tempY=m_GraphOffsetY+m_GraphHeight;
			   //if(m_RecievedTime[j]<(m_OriginValueX-m_DivValueX))//+(m_DivNumY)*m_DivValueY[i])
			   //{
				   //tempX=m_GraphOffsetX;               
				//   continue;
			   //}
			   if(m_RecievedTime[j]<(m_OriginValueX-m_DivValueX) && tempX < m_GraphOffsetX){

			      //tempX=m_GraphOffsetX;
				   continue;

			   }
			   else if(tempX < m_GraphOffsetX && j<m_DisplayDataEndTimeIndex){
			   
				   //for(k=j;k<=m_DisplayDataEndTimeIndex;k++){
                      //k = j; 
				   	   //tempX =m_GraphOffsetX+(m_RecievedTime[j]-m_OriginValueX)*m_TimeDrawCof;  
					   tempX =m_GraphOffsetX+(m_RecievedTime[j+1]-m_OriginValueX)*m_TimeDrawCof; 
					   if(tempX < m_GraphOffsetX) {
					   
					      continue;
					   }
					   else{
					      tempX = m_GraphOffsetX;
					   }

			   }

			   if(m_RecievedTime[j]> m_OriginValueX+(m_DivNumX)*m_DivValueX)
				   tempX=m_GraphOffsetX+m_GraphWidth;

		       glVertex3f(tempX,tempY,0.0);

			   if(j<m_DisplayDataEndTimeIndex){

				   if ((m_RecievedTime[j]-m_OriginValueX)*m_TimeDrawCof >= 0)
			          glVertex3f( tempX + (m_RecievedTime[j+1] -m_RecievedTime[j])*m_TimeDrawCof,tempY,0.0);
			       else 
			          glVertex3f( tempX + (m_RecievedTime[j+1] -m_OriginValueX)*m_TimeDrawCof,tempY,0.0);   
			   }
		  }           
	      glEnd();	
	  } 
  }
  else{

	 for(i=0;i<m_CurveNum;i++){
	      //glColor4f((i==1?1.0:0.0),((i==0|| i==3)?1.0:0.0),((i==2|| i==3)?1.0:0.0),1.0);//m_Color[i]);	
          glColor4f(float((m_Color[i] & 0x0F00)>>8)/15,float((m_Color[i] & 0x00F0)>>4)/15,float((m_Color[i] & 0x00F))/15,1.0);
		  glBegin(GL_LINE_STRIP);		 
		  for(j=m_DisplayDataStartTimeIndex;j<=m_DisplayDataEndTimeIndex;j++){	   

			   tempX=m_GraphOffsetX+(m_RecievedTime[j]-m_OriginValueX)*m_TimeDrawCof;
			   tempY=m_GraphOffsetY+m_GraphHeight-(m_RecievedData[i][j]-m_OriginValueY[i])*m_DataDrawCof[i];
			   if(m_RecievedData[i][j]>m_OriginValueY[i]+(m_DivNumY)*m_DivValueY[i])
				   tempY=m_GraphOffsetY;
			   if(m_RecievedData[i][j]<m_OriginValueY[i])
				   tempY=m_GraphOffsetY+m_GraphHeight;
			   if(m_RecievedTime[j]<(m_OriginValueX))//-m_DivValueX))//+(m_DivNumY)*m_DivValueY[i])
			   {
				   tempX=m_GraphOffsetX;               
				   //continue;
			   }

			   if(m_RecievedTime[j]> m_OriginValueX+(m_DivNumX)*m_DivValueX)
				   tempX=m_GraphOffsetX+m_GraphWidth;

		       glVertex3f(tempX,tempY,0.0);
                /*
			   if(j<m_DisplayDataEndTimeIndex){

				   if ((m_RecievedTime[j]-m_OriginValueX)*m_TimeDrawCof >= 0)
			          glVertex3f( tempX + (m_RecievedTime[j+1] -m_RecievedTime[j])*m_TimeDrawCof,tempY,0.0);
			       else 
			          glVertex3f( tempX + (m_RecievedTime[j+1] -m_OriginValueX)*m_TimeDrawCof,tempY,0.0);   
			   }*/
/*		  }           
	      glEnd();	
	  } 

 }
 */

}

/***********************************************************************************
子函数描述：RefreshDisplay(), 刷新示波器显示（X,Y轴坐标以及绘制的曲线动态刷新）
************************************************************************************/
void RefreshDisplay(){
 /* 
    int i;
	if(m_AutomaticDisplay==true){

		// m_DivValueY,m_OriginValueY m_DivValueY m_OriginValueX are changing dynamicly;	
	    for(i=0;i<m_CurveNum;i++){
			if(m_MaxRecievedData[i]!=m_MinRecievedData[i])		    
		        m_DisplayDataRange[i]=(m_MaxRecievedData[i]-m_MinRecievedData[i]);

		    m_OriginValueY[i]=m_MinRecievedData[i];
			m_DataDrawCof[i]=m_GraphHeight/m_DisplayDataRange[i];
			m_DivValueY[i]=m_DisplayDataRange[i]/m_DivNumY;	

		}
        m_DisplayDataEndTimeIndex=m_LatestTimeIndex;

		if(m_RefreshMode==1){				
           if(m_MaxRecievedTime!=m_MinRecievedTime)
				m_DisplayDataTimeLength=(m_MaxRecievedTime-m_MinRecievedTime);
           m_OriginValueX =m_MinRecievedTime;	
		}
		else
		   m_OriginValueX=m_RecievedTime[m_DisplayDataEndTimeIndex]-m_DisplayDataTimeLength;

        m_DisplayDataStartTimeIndex=m_DisplayDataEndTimeIndex-int(m_DisplayDataTimeLength/m_DataSampleTimeInterval);
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
		m_DisplayDataEndTimeIndex=int((m_OriginValueX+m_DisplayDataTimeLength)/m_DataSampleTimeInterval);
        m_DisplayDataStartTimeIndex=int(m_OriginValueX/m_DataSampleTimeInterval);
		m_TimeDrawCof=m_GraphWidth/m_DisplayDataTimeLength;
		if(m_DisplayDataStartTimeIndex<0)
			m_DisplayDataStartTimeIndex=0;
		if(m_DisplayDataEndTimeIndex<0)
			m_DisplayDataEndTimeIndex=0;	  
		m_DivValueX=m_DisplayDataTimeLength/m_DivNumX;	    
	}
	*/
}

/***********************************************************************************
子函数描述：ReciedveData(), 接受数据（X,Y轴坐标值）
************************************************************************************/
void ReciedveData(float Time, float[] Data){

  /*  int i,j;
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

	}*/

}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
OscilloScope(Context context){
	
	super(context,BUTTON_SQUARE,0.0f,0.0f,1.0f,4.0f); //m_TextRenderList=DEFAULT_CAPTION_DISPLAYLIST;
	ShaderRelatedInit(m_Context);
}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
OscilloScope(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float BorderWith){
	 
	  super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);
	  m_Width=ContrlUnitWidth[ControlType]*m_Scale;
	  m_Height=ContrlUnitHeight[ControlType]*m_Scale;
	  ShaderRelatedInit(m_Context);

}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/

public OscilloScope(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float Width,float Height,float BorderWith)
{
	super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);	  
	m_Width=Width*m_Scale-2.0f*BorderWith;
	m_Height=Height*m_Scale-2.0f*BorderWith;	
	ShaderRelatedInit(m_Context);
	
	m_Font = new MyFont(m_Context,(m_Context.getAssets()));
	GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_CULL_FACE);
	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	// Load the font from file (set size + padding), creates the texture
	// NOTE: after a successful call to this the font is ready for rendering!
	m_Font.load( "Roboto-Regular.ttf", 18, 0, 0);  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
	
}

void ShaderRelatedInit(Context context){
	
	
	// Initialize the color and texture handles
	final String vertexShader = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_border);
	final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_border);

	final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
	final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
			POSITION_ATTRIBUTE, TEXCORD_ATTRIBUTE});		
	// Load the texture
	textureId = TextureHelper.loadTexture(m_Context, R.drawable.orb);		
	GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			
	
	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);		
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
	
	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);		
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);		
 		
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
public void SetScopeParameters(float Height, float Width, int CurveNum){//, String[] CurveLabelString, int[] Color,float DataSampleTimeInterval,int DataSize,int DivNumX, int DivNumY, boolean GridOn){
		        
	  int i;
  


	  m_GraphHeight=Height;
	  m_GraphWidth=Width;


	  m_CurveNum=CurveNum;
	  
	  m_GraphOffsetX= -(m_GraphWidth)/2.0f;//(mWindowWidth -m_GraphWidth)/2.0f; 
      m_GraphOffsetY= -(m_GraphHeight)/2.0f; 
      
	  //m_GraphLabelHeight=30*0.5f;//m_GraphUnitHeight*0.5;
	  m_GraphLabelWidth=(m_Width -m_GraphWidth)/m_CurveNum;
	  
	  //m_LabelStringY = CurveLabelString;
	  m_LabelStringX= "";//CurveLabelString[CurveNum];//"t(ms)";


	  m_DataSampleTimeInterval=1;//DataSampleTimeInterval;

      m_DivNumX=10;//DivNumX; 
      m_DivNumY=10;//DivNumY; 

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

	 //m_Color=Color;

	  m_DisplayDataStartTimeIndex=0;
	  m_DisplayDataEndTimeIndex=0;
	  //m_DisplayDataMaxTimeIndex=DataSize-1;
	  m_LatestTimeIndex=0;
	  m_DisplayDataTimeLength=500;//ms
      m_MaxRecievedTime=500;
	  m_MinRecievedTime=0;
	  m_RefreshMode=false;

	  m_RecievedTime=new float[1000];//new float[DataSize];//[];
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

	  for(i=0;i<m_CurveNum;i++){ 	 
	     	//m_RecievedTime[i]=0;
			//m_RecievedData=new float(CurveNum ,DataSize);//[][];
            m_OriginValueY[i]=0;
			m_DataDrawCof[i]=1;
			m_DisplayDataRange[i]=100;
			m_MaxRecievedData[i]=0;
			m_MinRecievedData[i]=0;
			m_UserSelectedStartY[i]=0;
			m_RecievedData[i]=new float[1000];//new float[DataSize];
			m_UserSelectedEndY[i]=0; 
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

	  //InitGLDataForBorder();
	  InitGLDataForBackPanel();
}


public void SetDispWiodowSize(int width, int height)
{
	mWindowWidth = width;
	mWindowHeight = height;
}
/***********************************************************************************
 子函数描述：AddCaption(), 主题标签
 ************************************************************************************/
void AddCaption(String TextString,int TextLength,float FontSize,int FontList)
{//, float OffSetX,float OffSetY,int FontSize){
     // m_TextRenderList = Creat New DispalyList;
	 m_TextString  = TextString;

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
public void  Render(float[] modelMatrix){    
	DrawBackPanel(modelMatrix);
	DrawLables(modelMatrix);
	//DrawControlBorder(modelMatrix);
/*	
	Matrix.setIdentityM(mMVPMatrix, 0);
	Matrix.translateM(mMVPMatrix, 0, -mWindowWidth/2.0f, -mWindowHeight/2.0f, 0);	
	Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
	
	  if(m_IntervalCoordinate<0.48f && m_IntervalCoordinate>=0f)

	       m_IntervalCoordinate+=0.0025f;
     else
          m_IntervalCoordinate=0.0f;  
	  
	if (vbo[0] > 0 && ibo[0] > 0) {		
		
		GLES20.glUseProgram(program);
		ColorHandle          = GLES20.glGetUniformLocation(program, COLOR_UNIFORM);
        TextureUniformHandle = GLES20.glGetUniformLocation(program, TEXTURE_UNIFORM);
        TextureMoveUniformHandle = GLES20.glGetUniformLocation(program, TEXTUREMOV_UNIFORM);
        
		GLES20.glUniform4fv(ColorHandle, 1, mcolor , 0); 
		GLES20.glEnableVertexAttribArray(ColorHandle);
		
		GLES20.glUniform1f(TextureMoveUniformHandle, m_IntervalCoordinate);//(, 1, mcolor , 0); 
		GLES20.glEnableVertexAttribArray(TextureMoveUniformHandle);	
		
 	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // Set the active texture unit to texture unit 0
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
		GLES20.glUniform1i(TextureUniformHandle, 0); 
	
		// Set program handles for cube drawing.
		mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
		//mvMatrixUniform = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
		//lightPosUniform = GLES20.glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);
		positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
		//normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
		texcordAttribute = GLES20.glGetAttribLocation(program, TEXCORD_ATTRIBUTE);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);


		//GLES20.glVertexAttribPointer(TextureUniformHandle, 2, GLES20.GL_FLOAT, false,
		//		4*4, 2*4);
		//GLES20.glEnableVertexAttribArray(TextureUniformHandle);
		GLES20.glVertexAttribPointer(texcordAttribute, 2, GLES20.GL_FLOAT, false,
				5*4, 0);
		GLES20.glEnableVertexAttribArray(texcordAttribute);			
		
		// Bind Attributes
		GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,
				5*4, 2*4);
		GLES20.glEnableVertexAttribArray(positionAttribute);
	
		// Draw
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 12, GLES20.GL_UNSIGNED_SHORT, 0);
		//GLES20.glDrawElements(GLES20.GL_LINES, 4, GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GLES20.glUseProgram(0);
		
	}*/
	
/*
	    static float tempAngle=0;
		tempAngle+=1;
		glDisable(GL_DEPTH_TEST);      	
		glPushMatrix();
		//glTranslatef(m_Width*m_Scale*0.5, 0, 0.0f);		
		//glRotatef(-tempAngle,0,1,0);
		//glTranslatef(-m_Width*m_Scale*0.5, 0, 0.0f);
		glTranslatef(m_OffSetX, m_OffSetY, 0.0f);         
		glEnable(GL_TEXTURE_2D);		 
		DrawControlBorder(1);             
		DrawControlArea(1);		         
		//glDisable(GL_TEXTURE_2D);
		//wglUseFontBitmaps(hDC, 0,128, AscCodeList1);
		DrawBackPanel();
     	DrawData();
		if(m_IsActive){		 
		      if(m_IsResetAxisX)
				  DrawScaleRullerX();

		      if(m_IsResetAxisY)
				  DrawScaleRullerY();
		 
		      if(m_IsResetAxisXY)
				  DrawScaleRullerXY();				 
		 }        
		//DrawGraphBorder
   	    glColor4f(0.0,0.4,0.4,0.2);
	    glLineWidth(m_AxisLineWidth);
	    glBegin(GL_LINES);			  
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY ,0.0);
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY+m_GraphHeight ,0.0);
			  glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY+m_GraphHeight ,0.0);
			  glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY ,0.0);
			  //glVertex3f(m_GraphOffsetX, m_GraphOffsetY ,0.0);          
	    glEnd();    
        glEnable(GL_BLEND); 
	    glBlendFunc(GL_SRC_ALPHA,GL_ONE);

	    glBegin(GL_TRIANGLE_STRIP);
			  glColor4f(1.0,1.0,1.0,0.4);	
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY ,0.0);
			  glColor4f(0.0,0.0,0.0,0.3);	
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY+0.5*m_GraphHeight ,0.0);
			  glColor4f(1.0,1.0,1.0,0.4);	
	          glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY ,0.0);
			  glColor4f(0.0,0.0,0.0,0.3);	
			  glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY+0.5*m_GraphHeight ,0.0);		
          
	    glEnd();

	    glBegin(GL_TRIANGLE_STRIP);
			  glColor4f(0.0,0.0,0.0,0.7);
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY+0.5*m_GraphHeight ,0.0);
			  glColor4f(0.0,0.0,0.0,0.5);
			  glVertex3f(m_GraphOffsetX, m_GraphOffsetY+m_GraphHeight ,0.0);
			  glColor4f(0.0,0.0,0.0,0.7);
	          glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY+0.5*m_GraphHeight ,0.0);
			  glColor4f(0.0,0.0,0.0,0.5);
			  glVertex3f(m_GraphOffsetX+m_GraphWidth, m_GraphOffsetY+m_GraphHeight ,0.0);		
          
	    glEnd();  

		glDisable(GL_BLEND);  
		glPopMatrix();*/
 }


/***********************************************************************************
 子函数描述：UserKeyInput(),读取用户键盘输入
 ************************************************************************************/
void UserKeyInput(int InputKey){

}

/***********************************************************************************
 子函数描述：UserMouseMove(),鼠标移动事件
 ************************************************************************************/
void UserMouseMove(float wParam, float lParam){
/*
    m_ReleaseMouseX = ( short )LOWORD( lParam );
    m_ReleaseMouseY = ( short )HIWORD( lParam );
	if(!m_IsActive){      
		if(m_ReleaseMouseX-m_GraphOffsetX-m_OffSetX<m_GraphWidth  && m_ReleaseMouseY-m_GraphOffsetY-m_OffSetY< m_GraphHeight  && m_ReleaseMouseX-m_OffSetX > m_GraphOffsetX && m_ReleaseMouseY-m_OffSetY > m_GraphOffsetY){
             m_IsOnfocus=1;
 		}
		else
			 m_IsOnfocus=0;
	}*/
}

/***********************************************************************************
 子函数描述：UserMouseDown(),鼠标点击事件
 ************************************************************************************/
 void UserMouseDown(float wParam, float lParam){

/*	     m_ReleaseMouseX=m_ActiveMouseX = ( short )( lParam );
		 m_ReleaseMouseY=m_ActiveMouseY = ( short )( lParam );
         m_IsActive=true;		   
		 m_UserSelectedStartX = (m_ActiveMouseX-m_GraphOffsetX-m_OffSetX)/m_TimeDrawCof+m_OriginValueX;

		 for(int i=0;i<m_CurveNum;i++){ 

		      m_UserSelectedStartY[i] = (m_GraphHeight-m_ActiveMouseY+m_GraphOffsetY+m_OffSetY)/m_DataDrawCof[i]+m_OriginValueY[i];
		 }

*/
 }

/***********************************************************************************
 子函数描述：UserMouseUp(),鼠标释放事件
 ************************************************************************************/	 
void  UserMouseUp(float wParam, float lParam){

/*	m_ReleaseMouseX = ( short )LOWORD( lParam );
	m_ReleaseMouseY = ( short )HIWORD( lParam );		  
    //tempRescale= abs(m_UserSelectedEndY-m_UserSelectedSatrtY)/m_DisplayDataRange[0];

	if(m_IsResetAxisY || m_IsResetAxisXY){

		 for(int i=0;i<m_CurveNum;i++){ 

			  m_UserSelectedEndY[i] = (m_GraphHeight-m_ReleaseMouseY+m_GraphOffsetY+m_OffSetY)/m_DataDrawCof[i]+m_OriginValueY[i];
			  m_DisplayDataRange[i] = abs(m_UserSelectedEndY[i]-m_UserSelectedStartY[i] );

			  if(m_DisplayDataRange[i]< 0.001)
				   m_DisplayDataRange[i]=0.001;

			  if(m_UserSelectedEndY[i] > m_UserSelectedStartY[i]){
				    //m_MaxRecievedData[i]=m_UserSelectedEndY[i];
					m_MinRecievedData[i]= m_UserSelectedStartY[i];
					if(m_DisplayDataRange[i]>= 0.001)
						m_MaxRecievedData[i]=m_UserSelectedEndY[i];

					else
						 m_MaxRecievedData[i]= m_MinRecievedData[i]+0.001;

			   }
			   else{
                      //m_MaxRecievedData[i]= m_UserSelectedStartY[i];
				    m_MinRecievedData[i]=m_UserSelectedEndY[i];
					if(m_DisplayDataRange[i]>= 0.001)
						m_MaxRecievedData[i]=m_UserSelectedStartY[i];
					else
						m_MaxRecievedData[i]= m_MinRecievedData[i]+0.001;
			   }
				 m_OriginValueY[i]=m_UserSelectedStartY[i];
		    }          

	   }

       if(m_IsResetAxisX || m_IsResetAxisXY){ 
			 m_UserSelectedEndX = (m_ReleaseMouseX-m_GraphOffsetX-m_OffSetX)/m_TimeDrawCof+m_OriginValueX;
             m_DisplayDataTimeLength= abs(m_UserSelectedEndX-m_UserSelectedStartX);			  
			 if(m_DisplayDataTimeLength< m_DataSampleTimeInterval)
				 m_DisplayDataTimeLength=m_DataSampleTimeInterval;

			 if(m_UserSelectedEndX > m_UserSelectedStartX){
				 m_MinRecievedTime= m_UserSelectedStartX;
                 if(m_DisplayDataTimeLength>= m_DataSampleTimeInterval)
				       m_MaxRecievedTime=m_UserSelectedEndX;
				 else
					   m_MaxRecievedTime=m_MinRecievedTime+m_DisplayDataTimeLength;
			  }
			  else{
                    // m_MaxRecievedTime= m_UserSelectedStartX;
				    m_MinRecievedTime=m_UserSelectedEndX;
					if(m_DisplayDataTimeLength>= m_DataSampleTimeInterval)
				          m_MaxRecievedTime=m_UserSelectedStartX;
					else
						 m_MaxRecievedTime=m_MinRecievedTime+m_DisplayDataTimeLength;
			  }
			  m_OriginValueX = m_MinRecievedTime;
		}
		RefreshDisplay();
        m_IsOnfocus=false;
		m_IsActive=false;
		  */
}

};