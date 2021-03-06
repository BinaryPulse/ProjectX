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

public class Button extends UIControlUnit {


	protected float   m_FontList;
	protected int[]    m_Color;
	protected float    m_AreaColor;
	protected float    m_AreaColor1;
	protected float    m_AreaColor2;

	protected static int m_ActiveMouseX;
	protected static int m_ActiveMouseY;

	protected static int m_ReleaseMouseX;
	protected static int m_ReleaseMouseY;


	/** This is a handle to our cube shading program. */
	/*protected  int program[];						   // OpenGL Program object
	protected int ColorHandle;						   // Shader color handle	
	protected int TextureUniformHandle;                 // Shader texture handle
	protected int TextureMoveUniformHandle;
	protected int textureId; */
	/** OpenGL handles to our program uniforms. */
	/*protected  int mvpMatrixUniform;
	protected  int mvMatrixUniform;
	protected  int lightPosUniform;*/

	
	/** OpenGL handles to our program attributes. */
	/*protected int positionAttribute;
	protected int texcordAttribute;
	protected int colorAttribute;*/	

	/** Identifiers for our uniforms and attributes inside the shaders. */	
	/*
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	//private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
	private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";

	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String COLOR_ATTRIBUTE = "a_Color";
	private static final String TEXCORD_ATTRIBUTE = "a_TexCoordinate";
	private static final String TEXTURE_UNIFORM = "u_Texture";
	private static final String COLOR_UNIFORM = "u_Color";
	private static final String TEXTUREMOV_UNIFORM = "u_Texmove";
	
	protected int[] vbo = new int[3];
	protected int[] ibo = new int[3];
	*/
	//private float[] mVPMatrix;		
	protected float[] mMVPMatrix = new float[16];	
	//protected float[] mcolor;	
    
    protected int mWindowWidth;
    protected int mWindowHeight;
    
    


/*##############################################################################
           
		         对象模块功能描述： OscilloScope（示波器）

###############################################################################*/

/***********************************************************************************
子函数描述：DrawControlBorder(bool AnimationEnabled), 绘制示波器边框
************************************************************************************/
/*void  DrawControlBorder(float[] modelMatrix){//boolean AnimationEnabled ){
	
	float[] color = {0.0f,1.0f, 1.0f, 0.0f};
	
	Matrix.setIdentityM(mMVPMatrix, 0);
	Matrix.translateM(mMVPMatrix, 0,  m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);	
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
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 16, GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GLES20.glUseProgram(0);
	}
}*/

public void InitGLDataForBorder()
{
	/*float vertexBuffer[] = {
			  
			0.48f-m_IntervalCoordinate, 0,
			0.0f,  m_Width*m_CornerProportion*m_Scale+m_BorderWidth, 0.0f,
			0.48f-m_IntervalCoordinate, 0.1f,
			m_BorderWidth, m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth, 0.0f,//

			0.6f-m_IntervalCoordinate, 0,
			m_Width*m_CornerProportion*m_Scale+m_BorderWidth, 0.0f, 0.0f,
			0.6f-m_IntervalCoordinate, 0.1f,
	        m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth, m_BorderWidth, 0.0f,//

			0.9f-m_IntervalCoordinate, 0,
	        m_Width*m_Scale+2*m_BorderWidth, 0.0f, 0.0f,
			0.9f-m_IntervalCoordinate, 0.1f,
	        m_Width*m_Scale+m_BorderWidth, m_BorderWidth, 0.0f,

			1-m_IntervalCoordinate, 0,
			m_Width*m_Scale+2*m_BorderWidth, (m_Height-m_Width*m_CornerProportion)*m_Scale+1.5f*m_BorderWidth, 0.0f,
			1-m_IntervalCoordinate, 0.1f,
	        m_Width*m_Scale+m_BorderWidth, (m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth, 0.0f,//


			0.48f-m_IntervalCoordinate, 0,
			m_Width*m_Scale+2*m_BorderWidth, (m_Height-m_Width*m_CornerProportion)*m_Scale+1.5f*m_BorderWidth, 0.0f,
			0.48f-m_IntervalCoordinate, 0.1f,
	        m_Width*m_Scale+m_BorderWidth, (m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth, 0.0f,//
	        
			0.6f-m_IntervalCoordinate, 0,
	        m_Width*(1-m_CornerProportion)*m_Scale+1.5f*m_BorderWidth, m_Height*m_Scale+2*m_BorderWidth, 0.0f,//
			0.6f-m_IntervalCoordinate, 0.1f,
	        m_Width*(1-m_CornerProportion)*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth, 0.0f,		 
			 
	        0.9f-m_IntervalCoordinate, 0,
	        0.0f, m_Height*m_Scale+2*m_BorderWidth, 0.0f,
			0.9f-m_IntervalCoordinate, 0.1f,
			m_BorderWidth, m_Height*m_Scale+m_BorderWidth, 0.0f,

		    1-m_IntervalCoordinate, 0,
	        0.0f, m_Width*m_CornerProportion*m_Scale+m_BorderWidth, 0.0f,
		    1-m_IntervalCoordinate, 0.1f,
	        m_BorderWidth, m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth, 0.0f   
		  
		};*/ 
    // initiate vertex buffer for border 	
	float vertexBuffer[] = {
	  
		0.48f-m_IntervalCoordinate, 0,
		0.0f,  m_Height*m_Scale+m_BorderWidth-( m_Width*m_CornerProportion*m_Scale+m_BorderWidth), 0.0f,
		0.48f-m_IntervalCoordinate, 0.1f,
		m_BorderWidth, m_Height*m_Scale+m_BorderWidth-(m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth), 0.0f,//

		0.6f-m_IntervalCoordinate, 0,
		m_Width*m_CornerProportion*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth- 0.0f, 0.0f,
		0.6f-m_IntervalCoordinate, 0.1f,
        m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth, m_Height*m_Scale+m_BorderWidth- m_BorderWidth, 0.0f,//

		0.9f-m_IntervalCoordinate, 0,
        m_Width*m_Scale+2*m_BorderWidth, m_Height*m_Scale+m_BorderWidth- 0.0f, 0.0f,
		0.9f-m_IntervalCoordinate, 0.1f,
        m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth- m_BorderWidth, 0.0f,

		1-m_IntervalCoordinate, 0,
		m_Width*m_Scale+2*m_BorderWidth,m_Height*m_Scale+m_BorderWidth-( (m_Height-m_Width*m_CornerProportion)*m_Scale+1.5f*m_BorderWidth), 0.0f,
		1-m_IntervalCoordinate, 0.1f,
        m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth-((m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth), 0.0f,//


		0.48f-m_IntervalCoordinate, 0,
		m_Width*m_Scale+2*m_BorderWidth, m_Height*m_Scale+m_BorderWidth-((m_Height-m_Width*m_CornerProportion)*m_Scale+1.5f*m_BorderWidth), 0.0f,
		0.48f-m_IntervalCoordinate, 0.1f,
        m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth-((m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth), 0.0f,//
        
		0.6f-m_IntervalCoordinate, 0,
        m_Width*(1-m_CornerProportion)*m_Scale+1.5f*m_BorderWidth,m_Height*m_Scale+m_BorderWidth-( m_Height*m_Scale+2*m_BorderWidth), 0.0f,//
		0.6f-m_IntervalCoordinate, 0.1f,
        m_Width*(1-m_CornerProportion)*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth-(m_Height*m_Scale+m_BorderWidth), 0.0f,		 
		 
        0.9f-m_IntervalCoordinate, 0,
        0.0f, m_Height*m_Scale+m_BorderWidth-(m_Height*m_Scale+2*m_BorderWidth), 0.0f,
		0.9f-m_IntervalCoordinate, 0.1f,
		m_BorderWidth, m_Height*m_Scale+m_BorderWidth-(m_Height*m_Scale+m_BorderWidth), 0.0f,

	    1-m_IntervalCoordinate, 0,
        0.0f, m_Height*m_Scale+m_BorderWidth-(m_Width*m_CornerProportion*m_Scale+m_BorderWidth), 0.0f,
	    1-m_IntervalCoordinate, 0.1f,
        m_BorderWidth, m_Height*m_Scale+m_BorderWidth-(m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth), 0.0f,
         
	  
	};

    short indexBuffer[]  ={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};	
    
    
    vertexBufferForBorder = new float[16*5];
    indexBufferForBorder = new short[16];
    
	for(int i =0;i<16;i++)
	{
		indexBufferForBorder[i] = indexBuffer[i];
		vertexBufferForBorder[i*5] = vertexBuffer[i*5];
		vertexBufferForBorder[i*5 +1] = vertexBuffer[i*5 +1];
		vertexBufferForBorder[i*5 +2] = vertexBuffer[i*5 +2];
		vertexBufferForBorder[i*5 +3] = vertexBuffer[i*5 +3];
		vertexBufferForBorder[i*5 +4] = vertexBuffer[i*5 +4];
	}
	//indexBufferForBorder[16] = indexBuffer[16];
    
	Matrix.setIdentityM(mMVPMatrix, 0);
	Matrix.translateM(mMVPMatrix, 0, m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);	
	for(int i =0;i<16;i++)
		mMVPMatrixForBorder[i] = mMVPMatrix[i];
    
   /* 
    
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
	} */
}
/***********************************************************************************
子函数描述：DrawControlArea(bool AnimationEnabled), 绘制示波器控制区域
************************************************************************************/
/*void DrawControlArea(float[] modelMatrix){
	
         
	//GLES20.glEnable(GLES20.GL_BLEND);  
	//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);	
		
		//float[] color = {0.0f,0.3f, 0.3f, 0.3f};		
	GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	
		Matrix.setIdentityM(mMVPMatrix, 0);
		Matrix.translateM(mMVPMatrix, 0, m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);	
		Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
		
		if (vbo[1] > 0 && ibo[1] > 0) {		
			
			GLES20.glUseProgram(program[1]);
			//ColorHandle          = GLES20.glGetUniformLocation(program[1], COLOR_UNIFORM);	        
			//GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
			//GLES20.glEnableVertexAttribArray(ColorHandle);
			 
			// Set program handles for cube drawing.
			mvpMatrixUniform = GLES20.glGetUniformLocation(program[1], MVP_MATRIX_UNIFORM);
			GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mMVPMatrix, 0);
			
			positionAttribute = GLES20.glGetAttribLocation(program[1], POSITION_ATTRIBUTE);
			colorAttribute = GLES20.glGetAttribLocation(program[1], COLOR_ATTRIBUTE);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[1]);
			// Bind Attributes
			GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,7*4, 0);
			GLES20.glEnableVertexAttribArray(positionAttribute);
			
			GLES20.glVertexAttribPointer(colorAttribute, 4, GLES20.GL_FLOAT, false,7*4, 3*4);
			GLES20.glEnableVertexAttribArray(colorAttribute);			
			// Draw
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 8, GLES20.GL_UNSIGNED_SHORT, 0);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);			
			GLES20.glUseProgram(0);
		}
		//GLES20.glDisable(GLES20.GL_BLEND);
  
}*/


public void InitGLDataForArea()
{
	  
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
	m_AreaColor =0.0f;
	m_AreaColor1 =0.1f;
	m_AreaColor2 =0.5f;
	float m_Alph =0.3f;
	float vertexBuffer[] = {

			 //0.5f+0.5f*m_PositionCoordinate,0.5f-0.5f*m_PositionCoordinate,
			 m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth -m_BorderWidth, 0.0f,
			 0.8f,0.8f,0.8f,0.3f,
			 //0.5f-0.45f*m_PositionCoordinate,0.5f-0.5f*m_PositionCoordinate,         
			 m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth, m_Height*m_Scale+m_BorderWidth -m_BorderWidth, 0.0f,
			 0.8f,0.8f,0.8f,0.3f,             
			 // 0.5f+0.5f*m_PositionCoordinate,0.5f-0.45f*m_PositionCoordinate,         
			 m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth -(m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,              
			 //0.5f-0.5f*m_PositionCoordinate,0.5f-0.45f*m_PositionCoordinate,
			 m_BorderWidth, m_Height*m_Scale+m_BorderWidth -(m_Width*m_CornerProportion*m_Scale+1.5f*m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,     
			 //0.5f+0.5f*m_PositionCoordinate,0.5f+0.45f*m_PositionCoordinate,
			 m_Width*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth -((m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,  

			 //0.5f-0.5f*m_PositionCoordinate,0.5f+0.45f*m_PositionCoordinate,
			 m_BorderWidth, m_Height*m_Scale+m_BorderWidth -((m_Height-m_Width*m_CornerProportion)*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,        
			 //0.5f+0.45f*m_PositionCoordinate,0.5f+0.5f*m_PositionCoordinate,  
			 m_Width*(1-m_CornerProportion)*m_Scale+m_BorderWidth, m_Height*m_Scale+m_BorderWidth -(m_Height*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph,       
			 //0.5f-0.5f*m_PositionCoordinate,0.5f+0.5f*m_PositionCoordinate,		 
			 m_BorderWidth, m_Height*m_Scale+m_BorderWidth -(m_Height*m_Scale+m_BorderWidth), 0.0f,
			 m_AreaColor1,m_AreaColor,m_AreaColor2,m_Alph   
	  
	};
    short indexBuffer[]  ={0,1,2,3,4,5,6,7};	
    
    //vertexBufferForArea = vertexBuffer;
    //indexBufferForArea = indexBuffer;   
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
	Matrix.setIdentityM(mMVPMatrix, 0);
	Matrix.translateM(mMVPMatrix, 0, m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);	
	for(int i =0;i<16;i++)
		mMVPMatrixForArea[i] = mMVPMatrix[i];
    
   /* 
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
					VertexDataBuffer, GLES20.GL_STATIC_DRAW);

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
					* 2, IndexDataBuffer, GLES20.GL_STATIC_DRAW);

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}*/
}

 public void DrawCaption(float[] modelMatrix,float[] Boundary)
 {      
	 	GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		// TEST: render some strings with the font
	  	m_Font.SetMvpMatrix(modelMatrix);
		//s = "START";
	  	//if(m_IsOnfocus)
		m_Font.SetDisplayArea(Boundary);  
		  m_Font.SetColor( 0.2f, 1.0f, 0.2f, 1.0f );  

	  	//else
	  	// m_Font.SetColor( 0.2f, 0.2f, 0.2f, 1.0f );  
	  	
	  	if(m_IsActive)
	  		m_Font.draw( m_TextString ,m_OffSetX -(mWindowWidth-m_Width*m_Scale-m_BorderWidth)/2.0f -m_TextString.length()*0.5f*25*m_FontSizeScaleFactor, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -25*m_FontSizeScaleFactor, 0);
	  	else
	  	m_Font.draw( m_TextString ,m_OffSetX -(mWindowWidth-m_Width*m_Scale-m_BorderWidth)/2.0f -m_TextString.length()*0.5f*25*m_FontSizeScaleFactor, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -20*m_FontSizeScaleFactor, 0); 
	  	m_Font.RenderFont();
		GLES20.glDisable(GLES20.GL_BLEND);
 }
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
Button(Context context){
	
	super(context,BUTTON_SQUARE,0.0f,0.0f,1.0f,4.0f); //m_TextRenderList=DEFAULT_CAPTION_DISPLAYLIST;
	//ShaderRelatedInit(m_Context);
	
  
}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
Button(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float BorderWith){
	 
	  super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);
	  m_Width=ContrlUnitWidth[ControlType]*m_Scale;
	  m_Height=ContrlUnitHeight[ControlType]*m_Scale;
	  m_ControlType =CONTROL_UNIT_BUTTON;
	  
	  /*
	  ShaderRelatedInit(m_Context);
	  InitGLDataForBorder();
	  InitGLDataForArea();*/
	  

}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/

public Button(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float Width,float Height,float BorderWith,float FontSize)
{
	super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);	  
	m_Width=Width*m_Scale-2.0f*BorderWith;
	m_Height=Height*m_Scale-2.0f*BorderWith;	
	m_ControlType =CONTROL_UNIT_BUTTON;
	m_FontSizeScaleFactor = FontSize;
	/*ShaderRelatedInit(m_Context);
	InitGLDataForBorder();
	InitGLDataForArea();*/
	
	/*
	m_Font = new MyFont(m_Context,(m_Context.getAssets()));
	GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_CULL_FACE);
	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	// Load the font from file (set size + padding), creates the texture
	// NOTE: after a successful call to this the font is ready for rendering!
	m_Font.load( "Roboto-Regular.ttf", (int)(13*FontSize), 0, 0);  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
	*/
}
/*
void ShaderRelatedInit(Context context){
	
	
	// Initialize the color and texture handles
	 String vertexShader = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_border);
	 String fragmentShader = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_border);

	 int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
	 int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
	 program =new int[2];
	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program[0] = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
			POSITION_ATTRIBUTE, TEXCORD_ATTRIBUTE});		

	
	String vertexShader1 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_area);
	String fragmentShader1 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_area);

	int vertexShaderHandle1 = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader1);
	int fragmentShaderHandle1 = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader1);

	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program[1] = ShaderHelper.createAndLinkProgram(vertexShaderHandle1, fragmentShaderHandle1, new String[] {
			POSITION_ATTRIBUTE,COLOR_ATTRIBUTE});	

	// Load the texture
	textureId = TextureHelper.loadTexture(m_Context,R.drawable.orb);		// 
	GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			
	
	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);		
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
	
	//GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);		
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);			
	GLES20.glGenBuffers(3, vbo, 0);
	GLES20.glGenBuffers(3, ibo, 0);	
	
}*/
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




public void SetDispWiodowSize(int width, int height)
{
	 mWindowWidth  = width;
	 mWindowHeight = height;
	 InitGLDataForBorder();
	 InitGLDataForArea();	
}
/***********************************************************************************
 子函数描述：AddCaption(), 主题标签
 ************************************************************************************/
public void AddCaption(String TextString)//,int TextLength,float FontSize,int FontList)
{
	 m_TextString  = TextString;

}



/***********************************************************************************
 子函数描述：Render(),绘制整个示波器模块
 ************************************************************************************/
public void  Render(float[] modelMatrix,float[] Boundary){    

	
	//DrawControlBorder(modelMatrix);
	//DrawControlArea(modelMatrix);
	//DrawCaption(modelMatrix);

 }

public void  RenderFont(float[] modelMatrix,float[] Boundary){    

	
	//DrawControlBorder(modelMatrix);
	//DrawControlArea(modelMatrix);
	DrawCaption(modelMatrix, Boundary);

 }
/***********************************************************************************
 子函数描述：UserKeyInput(),读取用户键盘输入
 ************************************************************************************/
void UserKeyInput(int InputKey){

}

/***********************************************************************************
 子函数描述：UserMouseMove(),鼠标移动事件
 ************************************************************************************/
public void UserMouseMove(int pointerId, float wParam, float lParam){
	
   	int tempMouseX = (int)( wParam );
	int tempMouseY = (int )( lParam ); 
      
	//m_MouseX=tempMouseX;

	//if(m_IsActive==false){

         if(tempMouseX-m_OffSetX<m_Width*m_Scale && tempMouseY-m_OffSetY<m_Height*m_Scale && tempMouseX>m_OffSetX && tempMouseY>m_OffSetY){     
             // m_IsOnfocus=true;
             // m_IsActive=true;
         }
		 else{
	         m_IsOnfocus=false;
	         m_IsActive=false;
		 }
	//} 
 
}

/***********************************************************************************
 子函数描述：UserMouseDown(),鼠标点击事件
 ************************************************************************************/
public void UserMouseDown(int pointerId,float wParam, float lParam){
	
	
   	int tempMouseX = (int)( wParam );
	int tempMouseY = (int )( lParam ); 
	
	//if(m_IsActive==false){

        if(tempMouseX-m_OffSetX<m_Width*m_Scale && tempMouseY-m_OffSetY<m_Height*m_Scale && tempMouseX>m_OffSetX && tempMouseY>m_OffSetY){     
             m_IsOnfocus=true;  
             m_IsActive=true;
        }
		 else{
	         m_IsOnfocus=false;
	         m_IsActive=false;
		 }
	//}
	

     
     
 }

/***********************************************************************************
 子函数描述：UserMouseUp(),鼠标释放事件
 ************************************************************************************/	 
public void  UserMouseUp(int pointerId,float wParam, float lParam){

	/* if(m_OnEventProcess!=0){
		  m_OnEventProcess();
	 }*/
	if(m_IsActive ==true)
	{
		m_IsClicked =true;
	}
    m_IsOnfocus=false;
    m_IsActive=false;	
}
};