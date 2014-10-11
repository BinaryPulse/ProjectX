/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.BinaryPulse.ProjectX.MyUI;

import com.BinaryPulse.ProjectX.R;

import com.BinaryPulse.ProjectX.common.RawResourceReader;
import com.BinaryPulse.ProjectX.common.ShaderHelper;
import com.BinaryPulse.ProjectX.common.TextureHelper;
//import com.android.texample2.Vertices;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.Matrix;


public class LedList {
	
	final static int VERTEX_SIZE         = 4;                  // Vertex Size (in Components) ie. (X,Y,U,V,M), M is MVP matrix index
	final static int VERTICES_PER_SPRITE = 32*7;          // Vertices Per Sprite
	final static int INDICES_PER_SPRITE  = 32*7;           // Indices Per Sprite
	final static int LED_BATCH_SIZE      = 10;
	final static int DIGIT_LED_MASK[]    = {0x3F,0x18,0x76,0x7C,0x59,0x6D,0x6F,0x38,0x7F,0x7D,0x00};
	final static int DIGIT_LED_LENGTH[]  = {6,2,5,5,4,5,6,3,7,6,7};
	//--Members--//
	AssetManager assets;                               // Asset Manager
	int textureId;                                     // Font Texture ID [NOTE: Public for Testing Purposes Only!]
	int textureSize;                                   // Texture Size for Font (Square) [NOTE: Public for Testing Purposes Only!]

	/** This is a handle to our cube shading program. */
	private int program;						   // OpenGL Program object
	private int ColorHandle;						   // Shader color handle	
	private int TextureUniformHandle;                 // Shader texture handle

	/** OpenGL handles to our program uniforms. */
	private int mvpMatrixUniform;
	//private int mvMatrixUniform;
	private int lightPosUniform;

	/** OpenGL handles to our program attributes. */
	private int positionAttribute;
	private int texcordAttribute;
	private int matrixIndexAttribute;
	private int colorAttribute;	

	/** Identifiers for our uniforms and attributes inside the shaders. */	
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	//private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
	private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";

	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String NORMAL_ATTRIBUTE = "a_Normal";
	private static final String TEXCORD_ATTRIBUTE = "a_TexCoordinate";
	private static final String MATRIX_INDEX_ATTRIBUTE = "a_MVPMatrixIndex";
	private static final String TEXTURE_UNIFORM = "u_Texture";
	private static final String COLOR_UNIFORM = "u_Color";
	
	protected int[] vbo = new int[1];
	protected int[] ibo = new int[2];
	
	private float[] mVPMatrix= new float[16];		
	private float[] mMVPMatrix = new float[16];	
	
	private float[] vertexBuffer = new float[LED_BATCH_SIZE * VERTICES_PER_SPRITE * VERTEX_SIZE];  // Create Vertex Buffer
	private short[] indexBuffer =  new short[LED_BATCH_SIZE * INDICES_PER_SPRITE]; 
	private short[] indexBuffer0 =  new short[LED_BATCH_SIZE * INDICES_PER_SPRITE]; 
	
	private float[]  spritevertexBuffer;
	private int bufferIndex;   
	private int bufferIndex0;        
	// Vertex Buffer Start Index
	private int numSprites;
	private int matrixIndex;// Number of Sprites Currently in Buffer	
	private float[] uMVPMatrices = new float[12*16]; 

	private float m_LedUnitWidth;
	private float m_LedUnitHeight ;    
	private float m_DepthZ;	
	private float m_LedUnitGap;
	private float m_rot;
	private float m_Zpos;
	//--Constructor--//
	// D: save program + asset manager, create arrays, and initialize the members
	public LedList(Context context,AssetManager assets) {

		this.assets = assets;                           // Save the Asset Manager Instance
	// Initialize the color and texture handles
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(context,
				R.raw.per_pixel_vertex_shader_for_led);
		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context,
				R.raw.per_pixel_fragment_shader_for_led);

		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
		//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
		program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
				POSITION_ATTRIBUTE, MATRIX_INDEX_ATTRIBUTE});		
		
		GLES20.glGenBuffers(1, vbo, 0);
		GLES20.glGenBuffers(2, ibo, 0);
		
		bufferIndex =0;
		bufferIndex0 =0;
		matrixIndex =0;
		numSprites  =0;
		m_rot = 0.0f;
		m_Zpos=0.0f;
	}
	

	//--Load Font--//
	// description
	//    this will load the specified font file, create a texture for the defined
	//    character range, and setup all required values used to render with it.
	// arguments:
	//    file - Filename of the font (.ttf, .otf) to use. In 'Assets' folder.
	//    size - Requested pixel size of font (height)
	//    padX, padY - Extra padding per character (X+Y Axis); to prevent overlapping characters.
	public boolean load(String file, int size, int padX, int padY) {

	// return success
		return true;                                    // Return Success
	}

	//--Begin/End Text Drawing--//
	// D: call these methods before/after (respectively all draw() calls using a text instance
	//    NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha only!!!
	// A: red, green, blue - RGB values for font (default = 1.0)
	//    alpha - optional alpha value for font (default = 1.0)
	// 	  vpMatrix - View and projection matrix to use
	// R: [none]

	public void SetColor(float red, float green, float blue, float alpha) {
		GLES20.glUseProgram(program); // specify the program to use
		// set color TODO: only alpha component works, text is always black #BUG
		float[] color = {red, green, blue, alpha}; 
		
		ColorHandle          = GLES20.glGetUniformLocation(program, COLOR_UNIFORM);
        //TextureUniformHandle = GLES20.glGetUniformLocation(program, TEXTURE_UNIFORM);
        //mMVPMatricesHandle  = GLES20.glGetUniformLocation(program,MVP_MATRIX_UNIFORM);
		
		GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
		GLES20.glEnableVertexAttribArray(ColorHandle);
		
		//GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // Set the active texture unit to texture unit 0
		//GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
		//Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
		//GLES20.glUniform1i(TextureUniformHandle, 0); 
		//mVPMatrix = pMatrix;		
	}
	
    public void SetMvpMatrix(float[] mvpMatrix)
    {
    	
    	mVPMatrix = mvpMatrix;
    }
    

	
public void RenderLedList(){
	
	float[] color = {0.2f, 0.2f, 0.2f, 1}; 
	// add the sprite mvp matrix to uMVPMatrices array	
	final FloatBuffer VertexDataBuffer = ByteBuffer
			.allocateDirect(vertexBuffer.length * 4).order(ByteOrder.nativeOrder())
			.asFloatBuffer();
	VertexDataBuffer.put(vertexBuffer).position(0);
	
	final ShortBuffer IndexDataBuffer = ByteBuffer
			.allocateDirect(indexBuffer.length * 2).order(ByteOrder.nativeOrder())
			.asShortBuffer();
	IndexDataBuffer.put(indexBuffer).position(0);
	
	final ShortBuffer IndexDataBuffer0 = ByteBuffer
			.allocateDirect(indexBuffer0.length * 2).order(ByteOrder.nativeOrder())
			.asShortBuffer();
	IndexDataBuffer0.put(indexBuffer0).position(0);	

	if (vbo[0] > 0 && ibo[0] > 0 && ibo[1] > 0) {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VertexDataBuffer.capacity() * 4,
				VertexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
				* 2, IndexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer0.capacity()
				* 2, IndexDataBuffer0, GLES20.GL_DYNAMIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	} 
	
	if (vbo[0] > 0 && ibo[0] > 0 && ibo[1] > 0) {				
		GLES20.glUseProgram(program);
		// Set program handles for cube drawing.
		mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, matrixIndex, false, uMVPMatrices, 0);
		//lightPosUniform = GLES20.glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);
		positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
		//normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
		//texcordAttribute = GLES20.glGetAttribLocation(program, TEXCORD_ATTRIBUTE);
		matrixIndexAttribute= GLES20.glGetAttribLocation(program, MATRIX_INDEX_ATTRIBUTE);		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

		GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false, 4*4, 0);
		GLES20.glEnableVertexAttribArray(positionAttribute);
		GLES20.glVertexAttribPointer(matrixIndexAttribute, 1, GLES20.GL_FLOAT, false, 4*4, 3*4);
		GLES20.glEnableVertexAttribArray(matrixIndexAttribute);		
		
		// Draw
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, bufferIndex, GLES20.GL_UNSIGNED_SHORT, 0);
		
		
		ColorHandle          = GLES20.glGetUniformLocation(program, COLOR_UNIFORM);
		GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
		GLES20.glEnableVertexAttribArray(ColorHandle);
		// Draw
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, bufferIndex0, GLES20.GL_UNSIGNED_SHORT, 0);
			
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glUseProgram(0);
		
	}		
	
	bufferIndex =0;
	bufferIndex0 =0;
	matrixIndex =0;
	numSprites  =0;
}

public void draw(int Digit[], float x, float y, float z, float angleDegX, float angleDegY, float angleDegZ)  {

		float letterX; 
		float letterY; 
		int   indextLength;
		int   mDrawMask;
		letterX = 0;
		letterY = y;
		
		int digitsLength = Digit.length;
		m_Zpos =z;
		
		for (int i = 0; i < digitsLength; i++)  {              // FOR Each Character in String
	
			indextLength = DIGIT_LED_LENGTH[i];
			
		     if(Digit[i] >9 || Digit[i] <0)
		         mDrawMask = DIGIT_LED_MASK[10];
			 else
				 mDrawMask= DIGIT_LED_MASK[Digit[i]];
			 
			 for(int j =0;j<7;j++){
				
				if( ((1<<(j)) &  mDrawMask ) != 0){
					
					for(int m =bufferIndex;m< bufferIndex + 32;m++)
					{
						indexBuffer[m] = (short)( j*32 + (m- bufferIndex)+i * VERTICES_PER_SPRITE );           // Calculate Index 0
				    }
					bufferIndex += 32;
				}
				else
				{
					for(int m =bufferIndex0;m< bufferIndex0 + 32;m++)
					{
						indexBuffer0[m] = (short)( j*32 + (m- bufferIndex0)+i * VERTICES_PER_SPRITE );           // Calculate Index 0
				    }
					bufferIndex0 += 32;	
				}
			 }	  
			  
			DrawSprite(letterX, letterY, m_LedUnitWidth, m_LedUnitHeight,  i);				
			letterX += (float)50;				
			matrixIndex++;
     	}	
		//RenderLedList();
}
	
	
public void DrawSprite(float x, float y, float width, float height,  int j){//float[] modelMatrix)  {

		float halfWidth = width ;/// 2.0f;                 // Calculate Half Width
		float halfHeight = height;// / 2.0f;               // Calculate Half Height
		float x1 = 0;//x ;//- halfWidth;                       // Calculate Left X
		float y1 = y;//y ;//- halfHeight;                      // Calculate Bottom Y

		if(m_rot>360.0f+j*30)
			m_rot = -360.0f;
		else	
			m_rot+=3.0f;
		m_rot =0.0f;
		// create a model matrix based on x, y and angleDeg
		float[] tempModelMatrix = new float[16];
		//float[] tempModelMatrix1 = new float[16];
		Matrix.setIdentityM(tempModelMatrix, 0);
		Matrix.translateM(tempModelMatrix, 0, x,  (y1 + 2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth)/2.0f,m_Zpos);		
        if(m_rot>0.0f)
        	Matrix.rotateM(tempModelMatrix, 0, m_rot+j*30.0f, 0, 1, 0);
        else
        	Matrix.rotateM(tempModelMatrix, 0,0, 0, 1, 0);

		Matrix.translateM(tempModelMatrix, 0, -(m_LedUnitWidth +m_LedUnitHeight)/2.0f,  -(y1 + 2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth)/2.0f,-m_Zpos);		
		
		for(int i =0;i< VERTICES_PER_SPRITE;i++){
			vertexBuffer[numSprites*VERTEX_SIZE*VERTICES_PER_SPRITE +  VERTEX_SIZE*i+0] = x1 + spritevertexBuffer[3*i+0];//   (short)( j*30 + m +i * VERTICES_PER_SPRITE * VERTEX_SIZE);           // Calculate Index 0
			vertexBuffer[numSprites*VERTEX_SIZE*VERTICES_PER_SPRITE +  VERTEX_SIZE*i+1] = y1 + 2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth-spritevertexBuffer[3*i+1];
			vertexBuffer[numSprites*VERTEX_SIZE*VERTICES_PER_SPRITE +  VERTEX_SIZE*i+2] = spritevertexBuffer[3*i+2] + m_Zpos;
			vertexBuffer[numSprites*VERTEX_SIZE*VERTICES_PER_SPRITE +  VERTEX_SIZE*i+3] =  matrixIndex;			
		}
		
		// add the sprite mvp matrix to uMVPMatrices array		
		Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix , 0, tempModelMatrix, 0);
		//TODO: make sure numSprites < 24
		for (int m = 0; m < 16; m++) {
			uMVPMatrices[matrixIndex*16+m] = mMVPMatrix[m];
		}		
		numSprites++; // Increment Sprite Count	
}

public void SetDigitalLedPara( float LedUnitWidth, float LedUnitHeight, float DepthZ,float LedUnitGap ){

	     int mDrawMask,i;		 
         float OffSetX =0;
         float OffSetY =0;     
	      m_LedUnitWidth = LedUnitWidth;//2.0f;
	      m_LedUnitHeight = LedUnitHeight;//2.0f;	    
	      m_DepthZ = DepthZ;//2.0f;	  	
	      m_LedUnitGap = LedUnitGap;//2.0f;	  	
	 	
	     spritevertexBuffer= new float[]{
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,0.0f,					  
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,0.0f,		      
	 		  OffSetX , OffSetY + 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ m_LedUnitWidth , OffSetY + 0.5f* m_LedUnitWidth ,0.0f,		     
	 		  OffSetX , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ m_LedUnitWidth  , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+m_LedUnitHeight ,0.0f,
	 		  OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+m_LedUnitHeight ,0.0f,			
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,m_DepthZ,		      
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,m_DepthZ,	
	 		  OffSetX+ m_LedUnitWidth , OffSetY + 0.5f* m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX , OffSetY + 0.5f* m_LedUnitWidth ,m_DepthZ,			      
	 		  OffSetX+ m_LedUnitWidth  , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,m_DepthZ,
	          OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+m_LedUnitHeight ,m_DepthZ,
	 		  OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+m_LedUnitHeight ,m_DepthZ,
	          OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,0.0f,          
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,0.0f,          
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,m_DepthZ,			      
	 		  OffSetX , OffSetY + 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX , OffSetY + 0.5f* m_LedUnitWidth ,m_DepthZ, 			     
	 		  OffSetX , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,m_DepthZ,
	          OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+m_LedUnitHeight ,0.0f,
	          OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+m_LedUnitHeight ,m_DepthZ,
	 		  OffSetX+ m_LedUnitWidth  , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ m_LedUnitWidth  , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX+ m_LedUnitWidth , OffSetY + 0.5f* m_LedUnitWidth ,0.0f,
	 	      OffSetX+ m_LedUnitWidth , OffSetY + 0.5f* m_LedUnitWidth ,m_DepthZ,
	          OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,0.0f,          
	          OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,m_DepthZ,
	          OffSetX+ 0.5f* m_LedUnitWidth, OffSetY ,m_DepthZ,
	          //------------------------------------------------------------------------------------------------------------------
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX , OffSetY + 0.5f*m_LedUnitWidth +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ m_LedUnitWidth , OffSetY + 0.5f* m_LedUnitWidth+  m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX , OffSetY +2*m_LedUnitHeight +m_LedUnitGap- 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ m_LedUnitWidth  , OffSetY+2* m_LedUnitHeight +m_LedUnitGap - 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,0.0f,	  
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,	      
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ m_LedUnitWidth , OffSetY + 0.5f* m_LedUnitWidth+  m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX , OffSetY + 0.5f*m_LedUnitWidth +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,  			     
	 		  OffSetX+ m_LedUnitWidth  , OffSetY+2* m_LedUnitHeight +m_LedUnitGap - 0.5f* m_LedUnitWidth ,m_DepthZ,
	          OffSetX , OffSetY +2*m_LedUnitHeight +m_LedUnitGap- 0.5f* m_LedUnitWidth ,m_DepthZ,
	          OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,	          		      
	 		  OffSetX , OffSetY + 0.5f*m_LedUnitWidth +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX , OffSetY + 0.5f*m_LedUnitWidth +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,			     
	 		  OffSetX , OffSetY +2*m_LedUnitHeight +m_LedUnitGap- 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX , OffSetY +2*m_LedUnitHeight +m_LedUnitGap- 0.5f* m_LedUnitWidth ,m_DepthZ,				   
	 		  OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f*m_LedUnitWidth  , OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ m_LedUnitWidth  , OffSetY+2* m_LedUnitHeight +m_LedUnitGap - 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ m_LedUnitWidth  , OffSetY+2* m_LedUnitHeight +m_LedUnitGap - 0.5f* m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX+ m_LedUnitWidth , OffSetY + 0.5f* m_LedUnitWidth+  m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ m_LedUnitWidth , OffSetY + 0.5f* m_LedUnitWidth+  m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,			
	 		  OffSetX+ 0.5f* m_LedUnitWidth, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		//------------------------------------------------------------------------------------------------------------------	  
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth ,0.0f,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap-0.5f*m_LedUnitWidth ,0.0f,	          
	 		  OffSetX + m_LedUnitHeight -m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth ,0.0f,
	 		  OffSetX  + m_LedUnitHeight - m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap-0.5f*m_LedUnitWidth ,0.0f,
	 		  OffSetX + m_LedUnitHeight + 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitHeight + 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap ,0.0f,			     
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap-0.5f*m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth ,m_DepthZ,	          
	 		  OffSetX  + m_LedUnitHeight - m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap-0.5f*m_LedUnitWidth ,m_DepthZ,				   
	 		  OffSetX + m_LedUnitHeight -m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight + 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight + 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap ,m_DepthZ,		     
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth ,0.0f,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight -m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth ,0.0f,
	 		  OffSetX + m_LedUnitHeight -m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap+0.5f*m_LedUnitWidth ,m_DepthZ,
	 	      OffSetX + m_LedUnitHeight + 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitHeight + 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX  + m_LedUnitHeight - m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap-0.5f*m_LedUnitWidth ,0.0f,
	 		  OffSetX  + m_LedUnitHeight - m_LedUnitGap, OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap-0.5f*m_LedUnitWidth ,m_DepthZ,
	          OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap-0.5f*m_LedUnitWidth ,0.0f,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+2*m_LedUnitHeight +1.5f*m_LedUnitGap-0.5f*m_LedUnitWidth ,m_DepthZ,	           
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+2*m_LedUnitHeight+1.5f*m_LedUnitGap ,m_DepthZ,
	 		//------------------------------------------------------------------------------------------------------------------				  
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,	
	          OffSetX +m_LedUnitHeight, OffSetY +0.5f*m_LedUnitWidth +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	          OffSetX+ m_LedUnitWidth +m_LedUnitHeight, OffSetY + 0.5f* m_LedUnitWidth+  m_LedUnitHeight +m_LedUnitGap ,0.0f,	          
	          OffSetX +m_LedUnitHeight , OffSetY +2*m_LedUnitHeight +m_LedUnitGap- 0.5f* m_LedUnitWidth ,0.0f,	          
	          OffSetX+ m_LedUnitWidth +m_LedUnitHeight  , OffSetY+2* m_LedUnitHeight + m_LedUnitGap - 0.5f* m_LedUnitWidth ,0.0f,
	          OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight, OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight, OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,0.0f,	    
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,	 		   
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight, OffSetY + 0.5f* m_LedUnitWidth+  m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX +m_LedUnitHeight, OffSetY +0.5f*m_LedUnitWidth +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,	          
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight  , OffSetY+2* m_LedUnitHeight + m_LedUnitGap - 0.5f* m_LedUnitWidth ,m_DepthZ,				    
	 		  OffSetX +m_LedUnitHeight , OffSetY +2*m_LedUnitHeight +m_LedUnitGap- 0.5f* m_LedUnitWidth ,m_DepthZ,
	          OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight, OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight, OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	          OffSetX +m_LedUnitHeight, OffSetY +0.5f*m_LedUnitWidth +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	          OffSetX +m_LedUnitHeight, OffSetY +0.5f*m_LedUnitWidth +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,	          
	 		  OffSetX +m_LedUnitHeight , OffSetY +2*m_LedUnitHeight +m_LedUnitGap- 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX +m_LedUnitHeight , OffSetY +2*m_LedUnitHeight +m_LedUnitGap- 0.5f* m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight, OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,0.0f,				
	 		  OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight, OffSetY+2*m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,			  
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight  , OffSetY+2* m_LedUnitHeight + m_LedUnitGap - 0.5f* m_LedUnitWidth ,0.0f,				  
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight  , OffSetY+2* m_LedUnitHeight + m_LedUnitGap - 0.5f* m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight, OffSetY + 0.5f* m_LedUnitWidth+  m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight, OffSetY + 0.5f* m_LedUnitWidth+  m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,	   	
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		  OffSetX+ 0.5f* m_LedUnitWidth + m_LedUnitHeight, OffSetY +m_LedUnitHeight +m_LedUnitGap ,m_DepthZ,
	 		//------------------------------------------------------------------------------------------------------------------				  
	 		  OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,0.0f,  
	 		   OffSetX +m_LedUnitHeight , OffSetY + 0.5f* m_LedUnitWidth ,0.0f,
	 		   OffSetX+ m_LedUnitWidth +m_LedUnitHeight , OffSetY + 0.5f* m_LedUnitWidth ,0.0f,          			     
	 		   OffSetX +m_LedUnitHeight , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,0.0f,          			      
	 		   OffSetX+ m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,0.0f,
	 		   OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight ,0.0f,
	 		   OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight ,0.0f,
	 		   OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,m_DepthZ,
	 		   OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,m_DepthZ,	             
	 		   OffSetX+ m_LedUnitWidth +m_LedUnitHeight , OffSetY + 0.5f* m_LedUnitWidth ,m_DepthZ,	          
	 		  OffSetX +m_LedUnitHeight , OffSetY + 0.5f* m_LedUnitWidth ,m_DepthZ,		     				    
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,m_DepthZ,
	          OffSetX +m_LedUnitHeight , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,m_DepthZ,
	          OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight ,m_DepthZ,
	          OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight ,m_DepthZ,
	          OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,0.0f,
	          OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,0.0f,
	          OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,m_DepthZ,          			      
	 		  OffSetX +m_LedUnitHeight , OffSetY + 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX +m_LedUnitHeight , OffSetY + 0.5f* m_LedUnitWidth ,m_DepthZ,			 			     
	 		  OffSetX +m_LedUnitHeight , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX +m_LedUnitHeight , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,m_DepthZ,		
	 		  OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight ,0.0f,
	 		  OffSetX+ 0.5f*m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight ,m_DepthZ,
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight  , OffSetY+ m_LedUnitHeight - 0.5f* m_LedUnitWidth ,m_DepthZ,
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight , OffSetY + 0.5f* m_LedUnitWidth ,0.0f,
	 		  OffSetX+ m_LedUnitWidth +m_LedUnitHeight , OffSetY + 0.5f* m_LedUnitWidth ,m_DepthZ,	          
	 		  OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,0.0f,
	 		  OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,m_DepthZ,
	 		  OffSetX+ 0.5f* m_LedUnitWidth +m_LedUnitHeight, OffSetY ,m_DepthZ,
	 		  //------------------------------------------------------------------------------------------------------------------	     
	 			  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,0.0f,
	 			OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap, OffSetY+0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitWidth+m_LedUnitGap , OffSetY-0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,0.0f,	          
	 		  OffSetX  + m_LedUnitHeight-m_LedUnitGap, OffSetY+0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitHeight-m_LedUnitGap, OffSetY-0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitHeight+ 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitHeight+ 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY-0.5f*m_LedUnitGap ,0.0f,				     
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,m_DepthZ,		
	 		  OffSetX + m_LedUnitWidth+m_LedUnitGap , OffSetY-0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap, OffSetY+0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight-m_LedUnitGap, OffSetY-0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX  + m_LedUnitHeight-m_LedUnitGap, OffSetY+0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight+ 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight+ 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap, OffSetY+0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap, OffSetY+0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,m_DepthZ,	          
	 		  OffSetX  + m_LedUnitHeight-m_LedUnitGap, OffSetY+0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX  + m_LedUnitHeight-m_LedUnitGap, OffSetY+0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight+ 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitHeight+ 0.5f*m_LedUnitWidth-m_LedUnitGap, OffSetY-0.5f*m_LedUnitGap ,m_DepthZ,				  
	 		  OffSetX + m_LedUnitHeight-m_LedUnitGap, OffSetY-0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitHeight-m_LedUnitGap, OffSetY-0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth+m_LedUnitGap , OffSetY-0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitWidth+m_LedUnitGap , OffSetY-0.5f*m_LedUnitWidth-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap , OffSetY-0.5f*m_LedUnitGap ,m_DepthZ,	
	          //------------------------------------------------------------------------------------------------------------------	    
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,0.0f,		
	 		  OffSetX + m_LedUnitWidth+m_LedUnitGap , OffSetY+m_LedUnitHeight  +0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,0.0f,			
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+m_LedUnitHeight -0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX  + m_LedUnitHeight - m_LedUnitGap, OffSetY+m_LedUnitHeight +0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,0.0f,			
	 		  OffSetX  + m_LedUnitHeight-m_LedUnitGap, OffSetY+m_LedUnitHeight -0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,0.0f,			
	 		  OffSetX + m_LedUnitHeight +0.5f*m_LedUnitWidth-m_LedUnitGap , OffSetY+m_LedUnitHeight +0.5f*m_LedUnitGap ,0.0f,			
	 		  OffSetX + m_LedUnitHeight +0.5f*m_LedUnitWidth-m_LedUnitGap , OffSetY+m_LedUnitHeight +0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+m_LedUnitHeight -0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth+m_LedUnitGap , OffSetY+m_LedUnitHeight  +0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,m_DepthZ,	          
	 		  OffSetX  + m_LedUnitHeight-m_LedUnitGap, OffSetY+m_LedUnitHeight -0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX  + m_LedUnitHeight - m_LedUnitGap, OffSetY+m_LedUnitHeight +0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight +0.5f*m_LedUnitWidth-m_LedUnitGap , OffSetY+m_LedUnitHeight +0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight +0.5f*m_LedUnitWidth-m_LedUnitGap , OffSetY+m_LedUnitHeight +0.5f*m_LedUnitGap ,m_DepthZ,			   
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth+m_LedUnitGap , OffSetY+m_LedUnitHeight  +0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitWidth+m_LedUnitGap , OffSetY+m_LedUnitHeight  +0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,m_DepthZ,	           
	 		  OffSetX  + m_LedUnitHeight - m_LedUnitGap, OffSetY+m_LedUnitHeight +0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX  + m_LedUnitHeight - m_LedUnitGap, OffSetY+m_LedUnitHeight +0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitHeight +0.5f*m_LedUnitWidth-m_LedUnitGap , OffSetY+m_LedUnitHeight +0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitHeight +0.5f*m_LedUnitWidth-m_LedUnitGap , OffSetY+m_LedUnitHeight +0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX  + m_LedUnitHeight-m_LedUnitGap, OffSetY+m_LedUnitHeight -0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX  + m_LedUnitHeight-m_LedUnitGap, OffSetY+m_LedUnitHeight -0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+m_LedUnitHeight -0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,0.0f,
	 		  OffSetX + m_LedUnitWidth +m_LedUnitGap , OffSetY+m_LedUnitHeight -0.5f*m_LedUnitWidth+0.5f*m_LedUnitGap ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,0.0f,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,m_DepthZ,
	 		  OffSetX + 0.5f* m_LedUnitWidth +m_LedUnitGap, OffSetY+m_LedUnitHeight+0.5f*m_LedUnitGap  ,m_DepthZ,
	     };     

	}
}
