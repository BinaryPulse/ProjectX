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

public class DropDownList extends UIControlUnit {


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
	
	protected int[] vbo = new int[1];
	protected int[] ibo = new int[1];
	
	//private float[] mVPMatrix;		
	protected float[] mMVPMatrix = new float[16];	
	protected float[] mcolor;
	
    protected int mWindowWidth;
    protected int mWindowHeight;
    
    
    protected float m_DropDownListOffSetX;
    protected float m_DropDownListOffSetY;
    protected float m_DropDownListWidth;
    protected float m_DropDownListPerHeight;

    protected float m_DragLineOffSetX;
    protected float m_DragLineOffSetY;
    protected float m_DragAreaWidth;
    protected float m_DragLineLength;

    protected float m_MouseOffSetY;
    protected float m_DragBoxLength;
    protected float m_DragBoxHeight;
    protected float m_DragBoxOffSetY ;

    protected int m_FocusItemIncreasedNum;
    protected int m_OnfocusItemNum;
    protected int m_FirstItemNum;
    protected int m_SelectedItemNum;
    protected int m_HideListNum;
    protected int m_DropListTotalNum;
    protected int m_DropListDisplayNum;
    protected int m_DropDownFontList;

    protected boolean  m_IsInSelectArea;
    protected boolean  m_IsInSlipArea;
    protected boolean  m_IsOnDrag;

    protected String m_ListString[];    

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
	Matrix.translateM(modleMatrix1, 0, m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);		
	for(int i =0;i<16;i++)
		mMVPMatrixForArea[i] = modleMatrix1[i];
 
}

/***********************************************************************************
子函数描述：DrawBackPanel(), 绘制示波器背景（说明文字、单位以及绘制网格刻度）
************************************************************************************/
 void DrawBackPanel(float[] modelMatrix){//boolean AnimationEnabled ){
		
		Matrix.setIdentityM(mMVPMatrix, 0);
		Matrix.translateM(mMVPMatrix, 0,  m_OffSetX -mWindowWidth/2.0f, m_OffSetY -mWindowHeight/2.0f, 0);	
		Matrix.multiplyMM(mMVPMatrix, 0,modelMatrix, 0, mMVPMatrix, 0);
		  
		if (vbo[0] > 0 && ibo[0] > 0) {		
			
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

			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
			
			int tempInt=m_OnfocusItemNum-m_FirstItemNum;
			if(tempInt <=m_DropListDisplayNum-1 && tempInt >=0){
				GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 52, GLES20.GL_UNSIGNED_SHORT, 0);
			}
			else
			{
				GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 47, GLES20.GL_UNSIGNED_SHORT, 0);
			}
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);			
			GLES20.glUseProgram(0);
			GLES20.glDisable(GLES20.GL_BLEND);
		}

}
 public void InitGLDataForBackPanel()
 {

	float     tempHeight=(m_DropListDisplayNum)*m_Height;
	int      tempInt=m_OnfocusItemNum-m_FirstItemNum;
	
	float 	vertexBuffer[] = {
			// background for dropdown
			
			  /* m_DropDownListOffSetX+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(0) ,0.0f,
			   0, 0, 0, 1.0f,*/
			   m_DropDownListOffSetX+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(0) ,0.0f,
			   0, 0, 0, 1.0f,
		       m_DropDownListOffSetX+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(m_DropListDisplayNum) ,0.0f,
		       0, 0, 0, 1.0f,
		       m_DropDownListOffSetX+m_Width+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(0) ,0.0f,
		       0, 0, 0, 1.0f,
		       m_DropDownListOffSetX+m_Width+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(m_DropListDisplayNum) ,0.0f,
		       0, 0, 0, 1.0f,			
		       m_DropDownListOffSetX+m_Width+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(m_DropListDisplayNum) ,0.0f,
		       0, 0, 0, 1.0f,		
			 // 分割线 vertical
			 m_DragLineOffSetX-0.25f*m_BorderWidth, m_Height ,0.0f,      
			 0,  1,  0,  0.6f,
			 m_DragLineOffSetX-0.25f*m_BorderWidth, m_Height ,0.0f,      
			 0,  1,  0,  0.6f,
		     m_DragLineOffSetX-0.25f*m_BorderWidth, -tempHeight ,0.0f,  
		     0,  1,  0,  0.6f,
		     m_DragLineOffSetX+0.25f*m_BorderWidth, m_Height ,0.0f,  
		     0,  1,  0,  0.6f,		     
		     m_DragLineOffSetX+0.25f*m_BorderWidth, -tempHeight ,0.0f,	
		     0,  1,  0,  0.6f,
		     m_DragLineOffSetX+0.25f*m_BorderWidth, -tempHeight ,0.0f,	
		     0,  1,  0,  0.6f,
		     
		  // 分割线  horizontal
			  m_BorderWidth, m_BorderWidth ,0.0f, 
			  0,  1,  0,  0.6f,
			  m_BorderWidth, m_BorderWidth ,0.0f, 
			  0,  1,  0,  0.6f,
			  m_BorderWidth, -m_BorderWidth,0.0f,
			  0,  1,  0,  0.6f,
			  m_BorderWidth+m_Width, m_BorderWidth ,0.0f, 
			  0,  1,  0,  0.6f,
			  m_BorderWidth+m_Width, -m_BorderWidth,0.0f, 
			  0,  1,  0,  0.6f,
			  m_BorderWidth+m_Width, -m_BorderWidth,0.0f, 
			  0,  1,  0,  0.6f,

			  // 包络线  horizontal
			  m_BorderWidth, -m_DragLineLength ,0.0f, 
			  0,  1,  0,  0.6f,
			  m_BorderWidth, -m_DragLineLength ,0.0f, 
			  0,  1,  0,  0.6f,
			  m_BorderWidth, -m_BorderWidth-m_DragLineLength,0.0f,
			  0,  1,  0,  0.6f,
			  m_BorderWidth+m_Width, -m_DragLineLength ,0.0f, 
			  0,  1,  0,  0.6f,
			  m_BorderWidth+m_Width, -m_BorderWidth-m_DragLineLength,0.0f, 
			  0,  1,  0,  0.6f,
			  m_BorderWidth+m_Width, -m_BorderWidth-m_DragLineLength,0.0f, 
			  0,  1,  0,  0.6f,
			  
			  // 包络线  V
			  	m_BorderWidth-0.5f*m_BorderWidth, m_Height ,0.0f,      
				 0,  1,  0,  0.6f,
				 m_BorderWidth-0.5f*m_BorderWidth, m_Height ,0.0f,      
				 0,  1,  0,  0.6f,
				 m_BorderWidth-0.5f*m_BorderWidth, -tempHeight-m_BorderWidth ,0.0f,  
			     0,  1,  0,  0.6f,
			     m_BorderWidth, m_Height ,0.0f,  
			     0,  1,  0,  0.6f,		     
			     m_BorderWidth, -tempHeight-m_BorderWidth ,0.0f,	
			     0,  1,  0,  0.6f,
			     m_BorderWidth, -tempHeight-m_BorderWidth ,0.0f,	
			     0,  1,  0,  0.6f,
			  
			  // 包络线  V
			     m_BorderWidth+m_Width, m_Height ,0.0f,      
				 0,  1,  0,  0.6f,
			     m_BorderWidth+m_Width, m_Height ,0.0f,      
				 0,  1,  0,  0.6f,
				 m_BorderWidth+m_Width, -tempHeight-m_BorderWidth ,0.0f,  
			     0,  1,  0,  0.6f,
			     m_BorderWidth+m_Width+0.5f*m_BorderWidth, m_Height ,0.0f,  
			     0,  1,  0,  0.6f,		     
			     m_BorderWidth+m_Width+0.5f*m_BorderWidth, -tempHeight-m_BorderWidth ,0.0f,	
			     0,  1,  0,  0.6f,
			     m_BorderWidth+m_Width+0.5f*m_BorderWidth, -tempHeight-m_BorderWidth ,0.0f,	
			     0,  1,  0,  0.6f,
			  
			  
			//Drag Line
			m_DragLineOffSetX+m_DragAreaWidth*0.45f, m_DragLineOffSetY ,0.0f,   
			0, 1, 0, 0.5f,
			m_DragLineOffSetX+m_DragAreaWidth*0.45f, m_DragLineOffSetY ,0.0f,   
			0, 1, 0, 0.5f,
			m_DragLineOffSetX+m_DragAreaWidth*0.45f, m_DragLineOffSetY-m_DragLineLength ,0.0f,  
			0, 1, 0, 0.5f,
			m_DragLineOffSetX+m_DragAreaWidth*0.55f, m_DragLineOffSetY ,0.0f,    
			0, 1, 0, 0.5f,
			m_DragLineOffSetX+m_DragAreaWidth*0.55f, m_DragLineOffSetY-m_DragLineLength,0.0f,
			0, 1, 0, 0.5f,
			m_DragLineOffSetX+m_DragAreaWidth*0.55f, m_DragLineOffSetY-m_DragLineLength,0.0f,
			0, 1, 0, 0.5f,
			//Drag Box
			m_DragLineOffSetX, m_DragBoxOffSetY ,0.0f,      
			0, 1, 0, 0.5f,
			m_DragLineOffSetX, m_DragBoxOffSetY ,0.0f,      
			0, 1, 0, 0.5f,
			m_DragLineOffSetX, m_DragBoxOffSetY-m_DragBoxHeight ,0.0f,
			0, 1, 0, 0.5f,
			m_DragLineOffSetX+m_DragBoxLength, m_DragBoxOffSetY ,0.0f,
			0, 1, 0, 0.5f,
			m_DragLineOffSetX+m_DragBoxLength, m_DragBoxOffSetY-m_DragBoxHeight,0.0f,
			0, 1, 0, 0.5f,
			m_DragLineOffSetX+m_DragBoxLength, m_DragBoxOffSetY-m_DragBoxHeight,0.0f,
			0, 1, 0, 0.5f,		     
		       
			//Focus or Selected Area     

		   m_DropDownListOffSetX+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(tempInt) ,0.0f,
		   0, 1, 0, 0.5f,
		   m_DropDownListOffSetX+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(tempInt) ,0.0f,
		   0, 1, 0, 0.5f,
	       m_DropDownListOffSetX+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(tempInt+1) ,0.0f,
		   0, 1, 0, 0.5f,
	       m_DropDownListOffSetX+m_DropDownListWidth+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(tempInt) ,0.0f,
		   0, 1, 0, 0.5f,
	       m_DropDownListOffSetX+m_DropDownListWidth+m_BorderWidth, m_DropDownListOffSetY-m_DropDownListPerHeight*(tempInt+1) ,0.0f,
	       0, 1, 0, 0.5f

	};

    //short indexBuffer[]  ={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27};

    short indexBuffer[] = new short[52];
    for(int i =0;i<52;i++)
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
 

public void DrawLables(float[] modelMatrix)
 {      
 	GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	// TEST: render some strings with the font
  	m_Font.SetMvpMatrix(modelMatrix);
	//s = "START";
  	//m_Font.SetColor( 1.0f, 0.0f, 0.0f, 1.0f );  
  	//m_Font.draw( m_TextString ,m_OffSetX -(mWindowWidth-m_Width*m_Scale-m_BorderWidth)/2.0f -m_TextString.length()*0.5f*25, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -20, 0); 
  	//m_Font.draw( m_ListString[m_SelectedItemNum-1] ,m_OffSetX -(mWindowWidth-m_Width*m_Scale-m_BorderWidth)/2.0f -m_ListString[m_SelectedItemNum-1].length()*0.5f*25, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -20, 0);
  	
	  for(int i=0;i<m_DropListDisplayNum;i++){
	    
	       if(m_OnfocusItemNum - m_FirstItemNum != i){
	    	   m_Font.SetColor(0.1f, 0.9f, 0.1f,0.9f);	 
	    	   m_Font.draw( m_ListString[i+m_FirstItemNum-1] ,m_OffSetX -(mWindowWidth-m_Width*m_Scale-m_BorderWidth)/2.0f -9*0.5f*25, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -20 -(i+1)*m_DropDownListPerHeight, 0);
	       }
	  }	  
	  m_Font.RenderFont();
	 
	  for(int i=0;i<m_DropListDisplayNum;i++){
		    
	       if(m_OnfocusItemNum - m_FirstItemNum == i){
			   m_Font.SetColor(0.0f,0.0f,1.0f,1.0f);  
	    	   m_Font.draw( m_ListString[i+m_FirstItemNum-1] ,m_OffSetX -(mWindowWidth-m_Width*m_Scale-m_BorderWidth)/2.0f -9*0.5f*25, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -20 -(i+1)*m_DropDownListPerHeight, 0);
	       }
	  }	  
	  m_Font.RenderFont();
	  
 	
	GLES20.glDisable(GLES20.GL_BLEND);
 }



/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
DropDownList(Context context){
	
	super(context,BUTTON_SQUARE,0.0f,0.0f,1.0f,4.0f); //m_TextRenderList=DEFAULT_CAPTION_DISPLAYLIST;
	 m_ControlType =CONTROL_UNIT_OSCILLO;
	 
	ShaderRelatedInit(m_Context);
}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/
DropDownList(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float BorderWith){
	 
	  super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);
	  m_Width=ContrlUnitWidth[ControlType]*m_Scale;
	  m_Height=ContrlUnitHeight[ControlType]*m_Scale;
	  m_ControlType =CONTROL_UNIT_OSCILLO;
	  
	  ShaderRelatedInit(m_Context);

}
/***********************************************************************************
子函数描述：UIControlUnit(), 初始化
************************************************************************************/

 public DropDownList(Context context,int ControlType,float OffSetX,float OffSetY,float Scale,float Width,float Height,float BorderWith)
{
	super(context,ControlType, OffSetX, OffSetY, Scale, BorderWith);	  
	m_Width=Width*m_Scale-2.0f*BorderWith;
	m_Height=Height*m_Scale-2.0f*BorderWith;
	m_ControlType =CONTROL_UNIT_OSCILLO;	
	m_HideListNum=0;
	m_DropListDisplayNum=1;
    m_DropListTotalNum=1;
	m_SelectedItemNum=1; 
	m_OnfocusItemNum=1;
	m_FirstItemNum=1;
	m_FocusItemIncreasedNum=0;
	m_Width=Width*m_Scale;
	m_Height=Height*m_Scale;
    m_DropDownListOffSetX=0;
    m_DropDownListOffSetY=0;//m_Height+m_BorderWidth;
    m_DropDownListWidth=0.85f*m_Width;
	m_DropDownListPerHeight=m_Height;
    //m_DropDownListPerHeight=
    m_DragLineOffSetX=m_BorderWidth+0.85f*m_Width;
    m_DragLineOffSetY=m_DropDownListOffSetY;
    m_DragAreaWidth=m_Width*0.15f;
    //m_DragLineLength
    m_MouseOffSetY=0;
    m_DragBoxLength=m_DragAreaWidth;
	m_DragBoxHeight=m_DragAreaWidth;
    m_DragBoxOffSetY=m_DragLineOffSetY;
	//m_IsOnfocus=0;	
	ShaderRelatedInit(m_Context);
	

}


public void SetDisplayList(int DisplayNum,String [] ListString){
	   
		m_DropListDisplayNum=DisplayNum;
		m_DropListTotalNum=2*m_DropListDisplayNum;
		m_ListString = ListString;
		m_HideListNum=m_DropListTotalNum-m_DropListDisplayNum;
		m_DropDownListPerHeight=m_Height;
	    m_DragLineLength=m_DropListDisplayNum*m_Height;

		if(m_DropListTotalNum-m_DropListDisplayNum !=-1)
		   m_DragBoxHeight=m_DragLineLength/(m_DropListTotalNum-m_DropListDisplayNum+1);
		else
	       m_DragBoxHeight=m_DragLineLength;
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

/***********************************************************************************
 子函数描述：Render(),绘制整个示波器模块
 ************************************************************************************/
public void  Render(float[] modelMatrix){    
	if(m_IsActive){ 
	InitGLDataForBackPanel(); // this must be done after WindowWidth  WindowHeight been Set
	DrawBackPanel(modelMatrix);
	DrawLables(modelMatrix);
	}
 }

public void  RenderFont(float[] modelMatrix){    

 	GLES20.glEnable(GLES20.GL_BLEND);
	//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	// TEST: render some strings with the font
  	m_Font.SetMvpMatrix(modelMatrix);
	//s = "START";
  	m_Font.SetColor( 0.0f, 1.0f, 0.0f, 1.0f );  
  	//m_Font.draw( m_TextString ,m_OffSetX -(mWindowWidth-m_Width*m_Scale-m_BorderWidth)/2.0f -m_TextString.length()*0.5f*25, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -20, 0); 
  	m_Font.draw( m_ListString[m_SelectedItemNum-1] ,m_OffSetX -(mWindowWidth-m_Width*m_Scale-m_BorderWidth)/2.0f -m_ListString[m_SelectedItemNum-1].length()*0.5f*25, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -20, 0);
  	m_Font.draw( "v" ,m_OffSetX -(mWindowWidth-m_BorderWidth)/2.0f + 3*m_BorderWidth+m_DragLineOffSetX, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -15 - 0.2f*m_DropDownListPerHeight, 0);
  	m_Font.draw("v",m_OffSetX -(mWindowWidth-m_BorderWidth)/2.0f + 3*m_BorderWidth+m_DragLineOffSetX, m_OffSetY -(mWindowHeight-m_Height*m_Scale-m_BorderWidth)/2.0f -15 +0.1f*m_DropDownListPerHeight, 0);
	  // m_DisplayFontList->print(m_OffSetX+3*m_BorderWidth+m_DragLineOffSetX, GetSystemMetrics(SM_CYSCREEN)-m_OffSetY - 0.2*m_DropDownListPerHeight,"v",1);
	  // m_DisplayFontList->print(m_OffSetX+3*m_BorderWidth+m_DragLineOffSetX, GetSystemMetrics(SM_CYSCREEN)-m_OffSetY + 0.1*m_DropDownListPerHeight,"v",1);

  	
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
public void UserMouseMove(float wParam, float lParam){
	
   	int tempMouseX = ( short )( wParam );
	int tempMouseY = ( short )( lParam ); 
    int tempInt;
    /*if(m_IsActive==false){

  		if(tempMouseX-m_OffSetX<m_Width && tempMouseY-m_OffSetY<m_Height && tempMouseX>m_OffSetX && tempMouseY>m_OffSetY){
   			 m_IsOnfocus=true; 
		}
		else
   			 m_IsOnfocus=false;                   
        
     }
	 else*/{
		tempMouseX=tempMouseX-(int)m_OffSetX;
		tempMouseY=-tempMouseY+(int)m_OffSetY;
		/*if(tempMouseX-m_DropDownListOffSetX<m_DropDownListWidth && tempMouseY<m_DropDownListPerHeight*m_DropListDisplayNum && tempMouseX>m_DropDownListOffSetX && tempMouseY> -m_Height){
   			m_IsInSelectArea=true;
 			m_IsInSlipArea=false;
			if(m_DropDownListPerHeight!=0)
 			     m_FocusItemIncreasedNum=(int)(tempMouseY)/((int)m_DropDownListPerHeight);
			m_OnfocusItemNum=m_FocusItemIncreasedNum+m_FirstItemNum;     					   
         }
		 else */if (tempMouseY>= -m_Height && tempMouseY<=m_DragLineLength  && tempMouseX-m_DragLineOffSetX<m_DragAreaWidth  && tempMouseX>m_DragLineOffSetX){
			m_IsInSlipArea=true;
     		m_IsInSelectArea=false;
     		if(m_IsOnDrag==true){	     			   	
     			 m_DragBoxOffSetY =-(tempMouseY-m_MouseOffSetY+0.5f*m_DragBoxHeight);	     			   	           
     			 if(m_DragBoxOffSetY>m_DragLineOffSetY)	     			   	     
     			     m_DragBoxOffSetY=m_DragLineOffSetY	;     

   	             if(m_DragBoxOffSetY<-m_DragLineLength+m_DragLineOffSetY+m_DragBoxHeight)
   	                 m_DragBoxOffSetY=-m_DragLineLength+m_DragLineOffSetY+m_DragBoxHeight;

     			 if(m_DragLineLength != m_DragBoxHeight) 
		             tempInt=(int)((-m_DragBoxOffSetY+m_DragLineOffSetY)/(m_DragLineLength-m_DragBoxHeight)*(m_HideListNum+1))+1;
				 else
                     tempInt=1;

     		     if(tempInt < m_DropListTotalNum-m_DropListDisplayNum+1)	        
	                 m_FirstItemNum=tempInt;
			     else
	                 m_FirstItemNum=m_DropListTotalNum-m_DropListDisplayNum+1;			   	
     		 }
     			   	
		 }	   	
     	/* else{
    		  m_IsInSlipArea=false;
     		  m_IsInSelectArea=false;	     			   	
   		 }*/
   } 	 
}

/***********************************************************************************
 子函数描述：UserMouseDown(),鼠标点击事件
 ************************************************************************************/
public void UserMouseDown(float wParam, float lParam){

		int tempMouseX = ( short )( wParam );
	   	int tempMouseY = ( short )( lParam ); 
		int tempInt;

		if(m_IsActive==false){

	  		if(tempMouseX-m_OffSetX<m_Width && tempMouseY-m_OffSetY<m_Height && tempMouseX>m_OffSetX && tempMouseY>m_OffSetY){
	   			 m_IsOnfocus=true; 
	   			 m_IsActive =true;
				 if(m_DropListTotalNum-m_SelectedItemNum>=m_DropListDisplayNum){
				     m_FirstItemNum=m_SelectedItemNum;			 
				 }
				 else{
		             m_FirstItemNum= m_DropListTotalNum-m_DropListDisplayNum+1;
				 }
				 m_OnfocusItemNum=m_SelectedItemNum;
				 if(m_HideListNum!=0)
				     m_DragBoxOffSetY = m_DragLineOffSetY  - (m_FirstItemNum-1)*((m_DragLineLength-m_DragBoxHeight) / m_HideListNum);
	   			 
			}
               
	        
	     }
		else{
			
			{
				tempMouseX=tempMouseX-(int)m_OffSetX;
				tempMouseY=-tempMouseY+(int)m_OffSetY;
				if(tempMouseX-m_DropDownListOffSetX<m_DropDownListWidth && tempMouseY<m_DropDownListPerHeight*m_DropListDisplayNum && tempMouseX>m_DropDownListOffSetX && tempMouseY> 0){
		   			m_IsInSelectArea=true;
		 			m_IsInSlipArea=false;
					if(m_DropDownListPerHeight!=0)
		 			     m_FocusItemIncreasedNum=(int)(tempMouseY)/((int)m_DropDownListPerHeight);
					m_OnfocusItemNum=m_FocusItemIncreasedNum+m_FirstItemNum;     					   
		         }
				 else if (tempMouseY>= 0 && tempMouseY<=m_DragLineLength && tempMouseX-m_DragLineOffSetX<m_DragAreaWidth  && tempMouseX>m_DragLineOffSetX){
					m_IsInSlipArea=true;
		     		m_IsInSelectArea=false;
		     		/*if(m_IsOnDrag==true){	     			   	
		     			 m_DragBoxOffSetY =-(tempMouseY+m_MouseOffSetY-0.5f*m_DragBoxHeight);	     			   	           
		     			 if(m_DragBoxOffSetY>m_DragLineOffSetY)	     			   	     
		     			     m_DragBoxOffSetY=m_DragLineOffSetY	;     

		   	             if(m_DragBoxOffSetY<-m_DragLineLength+m_DragLineOffSetY+m_DragBoxHeight)
		   	                 m_DragBoxOffSetY=-m_DragLineLength+m_DragLineOffSetY+m_DragBoxHeight;

		     			 if(m_DragLineLength != m_DragBoxHeight) 
				             tempInt=(int)((-m_DragBoxOffSetY+m_DragLineOffSetY)/(m_DragLineLength-m_DragBoxHeight)*(m_HideListNum+1))+1;
						 else
		                     tempInt=1;

		     		     if(tempInt < m_DropListTotalNum-m_DropListDisplayNum+1)	        
			                 m_FirstItemNum=tempInt;
					     else
			                 m_FirstItemNum=m_DropListTotalNum-m_DropListDisplayNum+1;			   	
		     		 }*/
		     			   	
				 }	   	
		     	 else{
		    		  m_IsInSlipArea=false;
		     		  m_IsInSelectArea=false;	     			   	
		   		 }
		   } 
			
	    /*if(m_IsActive==false){
	         m_IsActive=true;
			 if(m_DropListTotalNum-m_SelectedItemNum>=m_DropListDisplayNum){
			     m_FirstItemNum=m_SelectedItemNum;			 
			 }
			 else{
	             m_FirstItemNum= m_DropListTotalNum-m_DropListDisplayNum+1;
			 }
			 m_OnfocusItemNum=m_SelectedItemNum;
			 if(m_HideListNum!=0)
			     m_DragBoxOffSetY = m_DragLineOffSetY  + (m_FirstItemNum-1)*((m_DragLineLength-m_DragBoxHeight) / m_HideListNum);

		 }
		 else*/ 
		 if(m_IsInSelectArea==true){
		 
		     m_SelectedItemNum=m_OnfocusItemNum;
			 m_IsOnfocus=false;
		     m_IsActive=false;
		 
		 }
		 else if(m_IsInSlipArea==true){

			 //tempMouseX=tempMouseX-(int)m_OffSetX;
			 tempMouseY = ( short )( lParam )-(int)m_OffSetY;
		 
		     if (tempMouseY<m_DragBoxOffSetY && tempMouseY>m_DragBoxOffSetY-m_DragBoxHeight){

	              m_MouseOffSetY= 0.5f*m_DragBoxHeight - m_DragBoxOffSetY + tempMouseY;
			      m_IsOnDrag=true;
			 }
			 else{
			 
			      m_DragBoxOffSetY= tempMouseY;
				  if(m_DragBoxOffSetY  > m_DragLineOffSetY)	     			   	     
		     		  m_DragBoxOffSetY=m_DragLineOffSetY;     

		   	      if(m_DragBoxOffSetY<-m_DragLineLength+m_DragLineOffSetY+m_DragBoxHeight)
		   	          m_DragBoxOffSetY=-m_DragLineLength +m_DragLineOffSetY+m_DragBoxHeight;
		     			   	    
		     	  if(m_DragLineLength != m_DragBoxHeight) 
				      tempInt=(int)((-m_DragBoxOffSetY+m_DragLineOffSetY)/(m_DragLineLength-m_DragBoxHeight)*(m_HideListNum+1))+1;
				  else
	                  tempInt=1;

		     	  if(tempInt < m_DropListTotalNum-m_DropListDisplayNum+1)	        
			           m_FirstItemNum=tempInt;
				  else
			           m_FirstItemNum=m_DropListTotalNum-m_DropListDisplayNum+1;
			 }

		 }
		 else{	 
			 
			 if(tempMouseY<= -m_Height || tempMouseY>0|| tempMouseX<0  || tempMouseX>m_Width){
		    m_IsActive=false;
			m_IsOnDrag=false;
			m_IsOnfocus=false; 
		    } 
		 }

		 
	}
 }

/***********************************************************************************
 子函数描述：UserMouseUp(),鼠标释放事件
 ************************************************************************************/	 
 public void  UserMouseUp(float wParam, float lParam){

	m_IsOnDrag=false;
}

};