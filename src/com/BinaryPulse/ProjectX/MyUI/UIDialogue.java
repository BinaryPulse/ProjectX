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

public class UIDialogue extends UIControlUnit {

	
	final static int CONTROL_UNIT_TORTAL_TYPE = 6;     // total numbers of types of control unit	
	/*final static int CONTROL_UNIT_BUTTON   = 0;        // enum of control unit: button
	final static int CONTROL_UNIT_MESSAGE  = 1;        // enum of control unit: message box
	final static int CONTROL_UNIT_SLIDEBAR = 2;        // enum of control unit: slide bar
	final static int CONTROL_UNIT_DROPLIST = 3;        // enum of control unit: drop down list
	final static int CONTROL_UNIT_SELBOX   = 4;        // enum of control unit: select box
	final static int CONTROL_UNIT_OSCILLO  = 5;        // enum of control unit: OSCILLOSCOPE
	*/
	final static int MAX_CONTROL_UNIT_NUM  = 20;        // max control unit numbers in one UI dialogue 	
	
	final static int IndexSizeForBorder[] = {16,12};
	final static int IndexSizeForArea[]   = {8,8};
	
	final static int VERTEX_SIZE_FOR_BORDER  = 6;   
	final static int VERTEX_SIZE_FOR_AREA  = 8;   
	
	/** This is a handle to our cube shading program. */
	protected int program[];						   // OpenGL Program object
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
	protected int matrixIndexAttribute;

	/** Identifiers for our uniforms and attributes inside the shaders. */	
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
	
	private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";
	
	private static final String MATRIX_INDEX_ATTRIBUTE = "a_MVPMatrixIndex";
	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String COLOR_ATTRIBUTE = "a_Color";
	private static final String NORMAL_ATTRIBUTE = "a_Normal";
	private static final String TEXCORD_ATTRIBUTE = "a_TexCoordinate";
	private static final String TEXTURE_UNIFORM = "u_Texture";
	private static final String COLOR_UNIFORM = "u_Color";
	private static final String TEXTUREMOV_UNIFORM = "u_Texmove";
	
	protected int[] vbo = new int[CONTROL_UNIT_TORTAL_TYPE];
	protected int[] ibo = new int[CONTROL_UNIT_TORTAL_TYPE];
	protected int[] m_UIControlUnitNum = new int[CONTROL_UNIT_TORTAL_TYPE];
	
    protected float vertexBufferForBorder[] = new float[MAX_CONTROL_UNIT_NUM*16*VERTEX_SIZE_FOR_BORDER];
    protected int   vertexLengthForBorder; 
    protected short indexBufferForBorder[] =new short[MAX_CONTROL_UNIT_NUM*16];
    protected int   indexLengthForBorder; 
    
    protected int matrixIndexForBorder;
    
    protected float vertexBufferForArea[] = new float[MAX_CONTROL_UNIT_NUM*8*VERTEX_SIZE_FOR_AREA];
    protected int   vertexLengthForArea; 
    protected short indexBufferForArea[] =new short[MAX_CONTROL_UNIT_NUM*8];
    protected int   indexLengthForArea; 
    protected int  matrixIndexForArea;
	
	protected float[] uMVPMatricesForBorder = new float[MAX_CONTROL_UNIT_NUM*16]; 
	protected float[] uMVPMatricesForArea = new float[MAX_CONTROL_UNIT_NUM*16]; 
	protected float[] mMVPMatrix = new float[16];	

    protected int mWindowWidth;
    protected int mWindowHeight;
    
    protected static float m_timer;
    
    protected int m_ChildUnitLength;
    protected UIControlUnit m_ChildUnitArrayRoot;
    protected UIControlUnit m_ChildUnitArrayTemp;


/*##############################################################################
           
		         对象模块功能描述： UIDialogue（人机交互）

###############################################################################*/
    


public void AddCtrlUnit(UIControlUnit UIControlUnit){
	int i,m,j;

	 if(m_ChildUnitLength == 0){
	      m_ChildUnitArrayTemp = m_ChildUnitArrayRoot =UIControlUnit;
		    vertexLengthForBorder=indexLengthForBorder=vertexLengthForArea=indexLengthForArea =0;
		    matrixIndexForBorder = matrixIndexForArea =0; 
	 }
	 else{
          m_ChildUnitArrayTemp.SetNext(UIControlUnit);
	      m_ChildUnitArrayTemp=UIControlUnit;
	 
	 }
	 m_ChildUnitLength++;	 
	 //m_UIControlUnitNum[m_ChildUnitArrayTemp.m_ControlType]++;
	   m_ChildUnitArrayTemp.SetFont(m_Font);
		for( i =indexLengthForBorder; i< indexLengthForBorder +m_ChildUnitArrayTemp.indexBufferForBorder.length ;i++)
		{
			indexBufferForBorder[i] = (short)i;//(indexLengthForBorder+ m_ChildUnitArrayTemp.indexBufferForBorder[i -indexLengthForBorder]);           // Calculate Index 0
	    }
		indexLengthForBorder += m_ChildUnitArrayTemp.indexBufferForBorder.length;		 
	 
		for( i =0 ; i<m_ChildUnitArrayTemp.indexBufferForBorder.length ;i++)
		{
			for(j=0;j<VERTEX_SIZE_FOR_BORDER;j++){
			/*vertexBufferForBorder[vertexLengthForBorder+j] = m_ChildUnitArrayTemp.vertexBufferForBorder[5*i+j];           // Calculate Index 0
			vertexBufferForBorder[vertexLengthForBorder +1] = m_ChildUnitArrayTemp.vertexBufferForBorder[5*i +1];  	    
			vertexBufferForBorder[vertexLengthForBorder +2] = m_ChildUnitArrayTemp.vertexBufferForBorder[5*i +2];  
			vertexBufferForBorder[vertexLengthForBorder +3] = m_ChildUnitArrayTemp.vertexBufferForBorder[5*i +3];  
			vertexBufferForBorder[vertexLengthForBorder +4] = m_ChildUnitArrayTemp.vertexBufferForBorder[5*i +4];*/
				if(j == VERTEX_SIZE_FOR_BORDER -1){
					vertexBufferForBorder[vertexLengthForBorder +j] = matrixIndexForBorder;
				}
				else
					vertexBufferForBorder[vertexLengthForBorder+j] = m_ChildUnitArrayTemp.vertexBufferForBorder[(VERTEX_SIZE_FOR_BORDER -1)*i+j]; 
			}
			vertexLengthForBorder += VERTEX_SIZE_FOR_BORDER;
		}	 

		//TODO: make sure numSprites < 24
		for ( m = 0; m < 16; m++) {
			uMVPMatricesForBorder[matrixIndexForBorder*16+m] = m_ChildUnitArrayTemp.mMVPMatrixForBorder[m];
		}		
		matrixIndexForBorder++;
		
		
		for( i =indexLengthForArea; i< indexLengthForArea +m_ChildUnitArrayTemp.indexBufferForArea.length ;i++)
		{
			indexBufferForArea[i] =  (short)i;//m_ChildUnitArrayTemp.indexBufferForArea[i-indexLengthForArea];           // Calculate Index 0
	    }
		indexLengthForArea += m_ChildUnitArrayTemp.indexBufferForArea.length;		 
	 
		for( i =0 ; i< m_ChildUnitArrayTemp.indexBufferForArea.length ;i++)
		{
			/*vertexBufferForArea[vertexLengthForArea+ VERTEX_SIZE_FOR_AREA*0] = m_ChildUnitArrayTemp.vertexBufferForArea[7*i];           // Calculate Index 0
			vertexBufferForArea[vertexLengthForArea+ VERTEX_SIZE_FOR_AREA*0 +1] = m_ChildUnitArrayTemp.vertexBufferForArea[7*i +1];  	    
			vertexBufferForArea[vertexLengthForArea+ VERTEX_SIZE_FOR_AREA*0 +2] = m_ChildUnitArrayTemp.vertexBufferForArea[7*i +2];  
			vertexBufferForArea[vertexLengthForArea+ VERTEX_SIZE_FOR_AREA*0 +3] = m_ChildUnitArrayTemp.vertexBufferForArea[7*i +3];  
			vertexBufferForArea[vertexLengthForArea+ VERTEX_SIZE_FOR_AREA*0 +4] = m_ChildUnitArrayTemp.vertexBufferForArea[7*i +4];
			vertexBufferForArea[vertexLengthForArea+ VERTEX_SIZE_FOR_AREA*0 +5] = m_ChildUnitArrayTemp.vertexBufferForArea[7*i +5];
			vertexBufferForArea[vertexLengthForArea+ VERTEX_SIZE_FOR_AREA*0 +6] = m_ChildUnitArrayTemp.vertexBufferForArea[7*i +6];
			vertexBufferForArea[vertexLengthForArea+ VERTEX_SIZE_FOR_AREA*0 +7] = matrixIndexForArea;
			vertexLengthForArea += VERTEX_SIZE_FOR_AREA;*/
			for(j=0;j<VERTEX_SIZE_FOR_AREA;j++){
				if(j == VERTEX_SIZE_FOR_AREA -1)
					vertexBufferForArea[vertexLengthForArea +j] = matrixIndexForArea;				
				else
					vertexBufferForArea[vertexLengthForArea+j] = m_ChildUnitArrayTemp.vertexBufferForArea[(VERTEX_SIZE_FOR_AREA -1)*i+j]; 
			}	
			vertexLengthForArea += VERTEX_SIZE_FOR_AREA;
			
			
		}	 

		//TODO: make sure numSprites < 24
		for ( m = 0; m < 16; m++) {
			uMVPMatricesForArea[matrixIndexForArea*16+m] = m_ChildUnitArrayTemp.mMVPMatrixForArea[m];
		}			
		matrixIndexForArea++;

	 
}


public void EndConstruction(){	      

    
	final FloatBuffer VertexDataBuffer = ByteBuffer
				.allocateDirect(vertexBufferForBorder.length * 4).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
	VertexDataBuffer.put(vertexBufferForBorder).position(0);
		
	final ShortBuffer IndexDataBuffer = ByteBuffer
				.allocateDirect(indexBufferForBorder.length * 2).order(ByteOrder.nativeOrder())
				.asShortBuffer();
	IndexDataBuffer.put(indexBufferForBorder).position(0);



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
	
	final FloatBuffer VertexDataBuffer1 = ByteBuffer
			.allocateDirect(vertexBufferForArea.length * 4).order(ByteOrder.nativeOrder())
			.asFloatBuffer();
	VertexDataBuffer1.put(vertexBufferForArea).position(0);
	
	final ShortBuffer IndexDataBuffer1 = ByteBuffer
			.allocateDirect(indexBufferForArea.length * 2).order(ByteOrder.nativeOrder())
			.asShortBuffer();
	IndexDataBuffer1.put(indexBufferForArea).position(0);



	if (vbo[1] > 0 && ibo[1] > 0) {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VertexDataBuffer1.capacity() * 4,
				VertexDataBuffer1, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer1.capacity()
				* 2, IndexDataBuffer1, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	} 	
}  


void ShaderRelatedInit(Context context){
	
	
	// Initialize the color and texture handles
	 String vertexShader = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_border_ui);
	 String fragmentShader = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_border);

	 int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
	 int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
	 program =new int[2];
	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program[0] = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
			POSITION_ATTRIBUTE, TEXCORD_ATTRIBUTE,MATRIX_INDEX_ATTRIBUTE});		

	
	String vertexShader1 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_vertex_shader_for_area_ui);
	String fragmentShader1 = RawResourceReader.readTextFileFromRawResource(context,
			R.raw.per_pixel_fragment_shader_for_area);

	int vertexShaderHandle1 = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader1);
	int fragmentShaderHandle1 = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader1);

	//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
	//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
	program[1] = ShaderHelper.createAndLinkProgram(vertexShaderHandle1, fragmentShaderHandle1, new String[] {
			POSITION_ATTRIBUTE,COLOR_ATTRIBUTE,MATRIX_INDEX_ATTRIBUTE});	

	// Load the texture
	textureId = TextureHelper.loadTexture(m_Context,R.drawable.orb);		// 
	GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			
	
	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);		
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
	
	//GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);		
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);			
	GLES20.glGenBuffers(3, vbo, 0);
	GLES20.glGenBuffers(3, ibo, 0);	
	
}




public void Render(float[] modelMatrix){	   
	
	

	
	   m_ChildUnitArrayTemp=m_ChildUnitArrayRoot;
	   for(int i=0; i<m_ChildUnitLength; i++){
			   m_ChildUnitArrayTemp.RenderFont(modelMatrix);
            m_ChildUnitArrayTemp=m_ChildUnitArrayTemp.GetNext();
	   }	
		
		 DrawControlBorder(modelMatrix);
		 DrawControlArea(modelMatrix);	   
	   
}   

public void  RenderFont(float[] modelMatrix){    

	
	//DrawControlBorder(modelMatrix);
	//DrawControlArea(modelMatrix);
	//DrawCaption(modelMatrix);

 }
void  DrawControlBorder(float[] modelMatrix){//boolean AnimationEnabled ){
	
	int realtimeIndex;
	float[] color = {0.0f,1.0f, 0.0f, 0.0f};
	
	
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
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, modelMatrix, 0);
		
		mvMatrixUniform = GLES20.glGetUniformLocation(program[0], MV_MATRIX_UNIFORM);
		GLES20.glUniformMatrix4fv(mvMatrixUniform, matrixIndexForBorder, false, uMVPMatricesForBorder, 0);
		
		positionAttribute = GLES20.glGetAttribLocation(program[0], POSITION_ATTRIBUTE);
		texcordAttribute = GLES20.glGetAttribLocation(program[0], TEXCORD_ATTRIBUTE);
		matrixIndexAttribute= GLES20.glGetAttribLocation(program[0], MATRIX_INDEX_ATTRIBUTE);	
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

		GLES20.glVertexAttribPointer(texcordAttribute, 2, GLES20.GL_FLOAT, false,6*4, 0);
		GLES20.glEnableVertexAttribArray(texcordAttribute);			
		
		// Bind Attributes
		GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,6*4, 2*4);
		GLES20.glEnableVertexAttribArray(positionAttribute);
		
		GLES20.glVertexAttribPointer(matrixIndexAttribute, 1, GLES20.GL_FLOAT, false, 6*4, 5*4);
		GLES20.glEnableVertexAttribArray(matrixIndexAttribute);	
		// Draw
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		
		realtimeIndex =0;
		m_ChildUnitArrayTemp=m_ChildUnitArrayRoot;
		for(int i=0; i<m_ChildUnitLength; i++){
			
			//m_ChildUnitArrayTemp.Render(modelMatrix);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, m_ChildUnitArrayTemp.indexBufferForBorder.length, GLES20.GL_UNSIGNED_SHORT, 2*realtimeIndex);
			realtimeIndex += m_ChildUnitArrayTemp.indexBufferForBorder.length;
	        m_ChildUnitArrayTemp = m_ChildUnitArrayTemp.GetNext();
		}
		/*
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 16, GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 16, GLES20.GL_UNSIGNED_SHORT, 16*2);	
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 16, GLES20.GL_UNSIGNED_SHORT, 32*2);	
		*/
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GLES20.glUseProgram(0);
	}
}



/***********************************************************************************
子函数描述：DrawControlArea(bool AnimationEnabled), 绘制示波器控制区域
************************************************************************************/
void DrawControlArea(float[] modelMatrix){
	
	int realtimeIndex;
	//float[] color = {0.0f,0.3f, 0.3f, 0.3f};		
GLES20.glEnable(GLES20.GL_BLEND);
//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);


	
	if (vbo[1] > 0 && ibo[1] > 0) {		
		
		GLES20.glUseProgram(program[1]);
		/*ColorHandle          = GLES20.glGetUniformLocation(program[1], COLOR_UNIFORM);	        
		GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
		GLES20.glEnableVertexAttribArray(ColorHandle);
		 */
		// Set program handles for cube drawing.
		mvpMatrixUniform = GLES20.glGetUniformLocation(program[1], MVP_MATRIX_UNIFORM);
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, modelMatrix, 0);
		
		
		mvMatrixUniform = GLES20.glGetUniformLocation(program[1], MV_MATRIX_UNIFORM);
		GLES20.glUniformMatrix4fv(mvMatrixUniform, matrixIndexForArea, false, uMVPMatricesForArea, 0);
		
		positionAttribute = GLES20.glGetAttribLocation(program[1], POSITION_ATTRIBUTE);
		colorAttribute = GLES20.glGetAttribLocation(program[1], COLOR_ATTRIBUTE);
		matrixIndexAttribute= GLES20.glGetAttribLocation(program[1], MATRIX_INDEX_ATTRIBUTE);	
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[1]);
		// Bind Attributes
		GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,8*4, 0);
		GLES20.glEnableVertexAttribArray(positionAttribute);
		
		GLES20.glVertexAttribPointer(colorAttribute, 4, GLES20.GL_FLOAT, false,8*4, 3*4);
		GLES20.glEnableVertexAttribArray(colorAttribute);			
		
		GLES20.glVertexAttribPointer(matrixIndexAttribute, 1, GLES20.GL_FLOAT, false, 8*4,7*4);
		GLES20.glEnableVertexAttribArray(matrixIndexAttribute);	
		// Draw
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
		realtimeIndex =0;
		m_ChildUnitArrayTemp=m_ChildUnitArrayRoot;
		for(int i=0; i<m_ChildUnitLength; i++){
			
			//m_ChildUnitArrayTemp.Render(modelMatrix);
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, m_ChildUnitArrayTemp.indexBufferForArea.length, GLES20.GL_UNSIGNED_SHORT, 2*realtimeIndex);
			realtimeIndex += m_ChildUnitArrayTemp.indexBufferForArea.length;
	        m_ChildUnitArrayTemp = m_ChildUnitArrayTemp.GetNext();
		}
		/*
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 8, GLES20.GL_UNSIGNED_SHORT, 0);
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 8, GLES20.GL_UNSIGNED_SHORT, 16);
		
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[1]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 8, GLES20.GL_UNSIGNED_SHORT, 32);
		*/
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);			
		GLES20.glUseProgram(0);
		GLES20.glDisable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	}
}


 /*public void DrawLables(float[] modelMatrix)
 {      
	  	int i,j;
	  	float x,y,z;
	  	 String s;
		GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		// TEST: render some strings with the font
		m_Font.SetMvpMatrix(modelMatrix);
		m_Font.draw("example",0,0, 0); 
		m_Font.RenderFont();
		
 }*/

 
 public UIDialogue(Context context){    	
	    super(context,BUTTON_SQUARE,0.0f,0.0f,1.0f,4.0f); //m_TextRenderList=DEFAULT_CAPTION_DISPLAYLIST;	
	    m_ChildUnitLength =0;
	    for(int i =0;i<CONTROL_UNIT_TORTAL_TYPE;i++)
	    {
	    	m_UIControlUnitNum[i] =0;
	    }
	    
	    vertexLengthForBorder=indexLengthForBorder=vertexLengthForArea=indexLengthForArea =0;
	    matrixIndexForBorder = matrixIndexForArea =0; 
	    
		  ShaderRelatedInit(m_Context);
		  
			
			m_Font = new MyFont(m_Context,(m_Context.getAssets()));
			GLES20.glEnable(GLES20.GL_BLEND);
			//GLES20.glDisable(GLES20.GL_CULL_FACE);
			GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			// Load the font from file (set size + padding), creates the texture
			// NOTE: after a successful call to this the font is ready for rendering!
			m_Font.load( "Roboto-Regular.ttf", (int)(32), 0, 0);  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
					  
		  //InitGLDataForBorder();
		  //InitGLDataForArea();
	}    
/***********************************************************************************
 子函数描述：Render(),绘制整个示波器模块
 ************************************************************************************/


/***********************************************************************************
子函数描述：IsOnFocus(), 是否获得控制权
************************************************************************************/
@Override
boolean IsOnFocus(){
   return m_IsOnfocus;
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

}

/***********************************************************************************
 子函数描述：UserMouseDown(),鼠标点击事件
 ************************************************************************************/
 void UserMouseDown(float wParam, float lParam){

 }

/***********************************************************************************
 子函数描述：UserMouseUp(),鼠标释放事件
 ************************************************************************************/	 
void  UserMouseUp(float wParam, float lParam){

 }

};