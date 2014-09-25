/**
 * 
 */
/**
 * @author Administrator
 *
 */
package com.BinaryPulse.ProjectX.MyFont;


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


public class MyFont {
	
	class TextureRegion {

		   //--Members--//
		   public float u1, v1;                               // Top/Left U,V Coordinates
		   public float u2, v2;                               // Bottom/Right U,V Coordinates

		   //--Constructor--//
		   // D: calculate U,V coordinates from specified texture coordinates
		   // A: texWidth, texHeight - the width and height of the texture the region is for
		   //    x, y - the top/left (x,y) of the region on the texture (in pixels)
		   //    width, height - the width and height of the region on the texture (in pixels)
		   public TextureRegion(float texWidth, float texHeight, float x, float y, float width, float height)  {
		      this.u1 = x / texWidth;                         // Calculate U1
		      this.v1 = y / texHeight;                        // Calculate V1
		      this.u2 = this.u1 + ( width / texWidth );       // Calculate U2
		      this.v2 = this.v1 + ( height / texHeight );     // Calculate V2
		   }
	};
	//--Constants--//
	public final static int CHAR_START = 32;           // First Character (ASCII Code)
	public final static int CHAR_END = 126;            // Last Character (ASCII Code)
	public final static int CHAR_CNT = ( ( ( CHAR_END - CHAR_START ) + 1 ) + 1 );  // Character Count (Including Character to use for Unknown)

	public final static int CHAR_NONE = 32;            // Character to Use for Unknown (ASCII Code)
	public final static int CHAR_UNKNOWN = ( CHAR_CNT - 1 );  // Index of the Unknown Character

	public final static int FONT_SIZE_MIN = 6;         // Minumum Font Size (Pixels)
	public final static int FONT_SIZE_MAX = 180;       // Maximum Font Size (Pixels)

	public final static int CHAR_BATCH_SIZE = 100;//24;     // Number of Characters to Render Per Batch
													  // must be the same as the size of u_MVPMatrix 
													  // in BatchTextProgram
	
	final static int VERTEX_SIZE = 5;                  // Vertex Size (in Components) ie. (X,Y,U,V,M), M is MVP matrix index
	final static int VERTICES_PER_SPRITE = 4;          // Vertices Per Sprite
	final static int INDICES_PER_SPRITE = 6;           // Indices Per Sprite	
	
	//--Members--//
	AssetManager assets;                               // Asset Manager

	int fontPadX, fontPadY;                            // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)

	float fontHeight;                                  // Font Height (Actual; Pixels)
	float fontAscent;                                  // Font Ascent (Above Baseline; Pixels)
	float fontDescent;                                 // Font Descent (Below Baseline; Pixels)

	int textureId;                                     // Font Texture ID [NOTE: Public for Testing Purposes Only!]
	int textureSize;                                   // Texture Size for Font (Square) [NOTE: Public for Testing Purposes Only!]
	TextureRegion textureRgn;                          // Full Texture Region

	float charWidthMax;                                // Character Width (Maximum; Pixels)
	float charHeight;                                  // Character Height (Maximum; Pixels)
	final float[] charWidths;                          // Width of Each Character (Actual; Pixels)
	TextureRegion[] charRgn;                           // Region of Each Character (Texture Coordinates)
	int cellWidth, cellHeight;                         // Character Cell Width/Height
	int rowCnt, colCnt;                                // Number of Rows/Columns

	float scaleX, scaleY;                              // Font Scale (X,Y Axis)
	float spaceX;                                      // Additional (X,Y Axis) Spacing (Unscaled)
	
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
	protected int[] ibo = new int[1];
	
	private float[] mVPMatrix= new float[16];		
	private float[] mMVPMatrix = new float[16];	
	
	private float[] vertexBuffer = new float[CHAR_BATCH_SIZE * VERTICES_PER_SPRITE * VERTEX_SIZE];  // Create Vertex Buffer
	private short[] indexBuffer = new short[CHAR_BATCH_SIZE * INDICES_PER_SPRITE];  // Create Temp Index Buffer
	private int bufferIndex;                                   // Vertex Buffer Start Index
	private int numSprites;
	private int matrixIndex;// Number of Sprites Currently in Buffer	
	private float[] uMVPMatrices = new float[12*16]; 
	//--Constructor--//
	// D: save program + asset manager, create arrays, and initialize the members
	public MyFont(Context context,AssetManager assets) {

		this.assets = assets;                           // Save the Asset Manager Instance
		charWidths = new float[CHAR_CNT];               // Create the Array of Character Widths
		charRgn = new TextureRegion[CHAR_CNT];          // Create the Array of Character Regions

		// initialize remaining members
		fontPadX = 0;
		fontPadY = 0;

		fontHeight = 0.0f;
		fontAscent = 0.0f;
		fontDescent = 0.0f;

		textureId = -1;
		textureSize = 0;

		charWidthMax = 0;
		charHeight = 0;

		cellWidth = 0;
		cellHeight = 0;
		rowCnt = 0;
		colCnt = 0;

		scaleX = 1.0f;                                  // Default Scale = 1 (Unscaled)
		scaleY = 1.0f;                                  // Default Scale = 1 (Unscaled)
		spaceX = 0.0f;

		// Initialize the color and texture handles
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(context,
				R.raw.per_pixel_vertex_shader_for_font);
		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context,
				R.raw.per_pixel_fragment_shader_for_font);

		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
		//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
		program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
				POSITION_ATTRIBUTE, TEXCORD_ATTRIBUTE,MATRIX_INDEX_ATTRIBUTE});		
		
		GLES20.glGenBuffers(1, vbo, 0);
		GLES20.glGenBuffers(1, ibo, 0);
		
		bufferIndex =0;
		matrixIndex =0;
		numSprites  =0;
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

		// setup requested values
		fontPadX = padX;                                // Set Requested X Axis Padding
		fontPadY = padY;                                // Set Requested Y Axis Padding

		// load the font and setup paint instance for drawing
		Typeface tf = Typeface.createFromAsset( assets, file );  // Create the Typeface from Font File
		Paint paint = new Paint();                      // Create Android Paint Instance
		paint.setAntiAlias( true );                     // Enable Anti Alias
		paint.setTextSize( size );                      // Set Text Size
		paint.setColor( 0xffffff00 );                   // Set ARGB (White, Opaque)
		paint.setTypeface( tf );                        // Set Typeface

		// get font metrics
		Paint.FontMetrics fm = paint.getFontMetrics();  // Get Font Metrics
		fontHeight = (float)Math.ceil( Math.abs( fm.bottom ) + Math.abs( fm.top ) );  // Calculate Font Height
		fontAscent = (float)Math.ceil( Math.abs( fm.ascent ) );  // Save Font Ascent
		fontDescent = (float)Math.ceil( Math.abs( fm.descent ) );  // Save Font Descent

		// determine the width of each character (including unknown character)
		// also determine the maximum character width
		char[] s = new char[2];                         // Create Character Array
		Rect rect1= new Rect();
		charWidthMax = charHeight = 0;                  // Reset Character Width/Height Maximums
		float[] w = new float[2];                       // Working Width Value
		int cnt = 0;                                    // Array Counter
		for ( char c = CHAR_START; c <= CHAR_END; c++ )  {  // FOR Each Character
			s[0] = c;                                    // Set Character
			paint.getTextWidths( s, 0, 1, w );           // Get Character Bounds
			paint.getTextBounds( s, 0, 1,rect1);

			charWidths[cnt] = (float)(rect1.right);//-(float)rect1.left;//+padX;//w[0];                      // Get Width
			//if(c =='i')
				//charWidths[cnt]  =4.0f ;
			if ( charWidths[cnt] > charWidthMax )        // IF Width Larger Than Max Width
				charWidthMax = charWidths[cnt];           // Save New Max Width
			cnt++;                                       // Advance Array Counter
		}
		s[0] = CHAR_NONE;                               // Set Unknown Character
		paint.getTextWidths( s, 0, 1, w );              // Get Character Bounds
		charWidths[cnt] = w[0];                         // Get Width
		if ( charWidths[cnt] > charWidthMax )           // IF Width Larger Than Max Width
			charWidthMax = charWidths[cnt];              // Save New Max Width
		cnt++;                                          // Advance Array Counter
        
		charWidths[0] = charWidthMax/2.0f;  
		// set character height to font height
		charHeight = fontHeight;                        // Set Character Height

		// find the maximum size, validate, and setup cell sizes
		cellWidth = (int)charWidthMax + ( 2 * fontPadX );  // Set Cell Width
		cellHeight = (int)charHeight + ( 2 * fontPadY );  // Set Cell Height
		int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight;  // Save Max Size (Width/Height)
		if ( maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX )  // IF Maximum Size Outside Valid Bounds
			return false;                                // Return Error

		// set texture size based on max font size (width or height)
		// NOTE: these values are fixed, based on the defined characters. when
		// changing start/end characters (CHAR_START/CHAR_END) this will need adjustment too!
		if ( maxSize <= 24 )                            // IF Max Size is 18 or Less
			textureSize = 256;                           // Set 256 Texture Size
		else if ( maxSize <= 40 )                       // ELSE IF Max Size is 40 or Less
			textureSize = 512;                           // Set 512 Texture Size
		else if ( maxSize <= 80 )                       // ELSE IF Max Size is 80 or Less
			textureSize = 1024;                          // Set 1024 Texture Size
		else                                            // ELSE IF Max Size is Larger Than 80 (and Less than FONT_SIZE_MAX)
			textureSize = 2048;                          // Set 2048 Texture Size

		// create an empty bitmap (alpha only)
		Bitmap bitmap = Bitmap.createBitmap( textureSize, textureSize, Bitmap.Config.ALPHA_8 );  // Create Bitmap
		Canvas canvas = new Canvas( bitmap );           // Create Canvas for Rendering to Bitmap
		bitmap.eraseColor( 0x00000000 );                // Set Transparent Background (ARGB)

		// calculate rows/columns
		// NOTE: while not required for anything, these may be useful to have :)
		colCnt = textureSize / cellWidth;               // Calculate Number of Columns
		rowCnt = (int)Math.ceil( (float)CHAR_CNT / (float)colCnt );  // Calculate Number of Rows

		// render each of the characters to the canvas (ie. build the font map)
		float x = 0;//fontPadX;                             // Set Start Position (X)
		float y = ( cellHeight - 1 ) - fontDescent - fontPadY;  // Set Start Position (Y)
		for ( char c = CHAR_START; c <= CHAR_END; c++ )  {  // FOR Each Character
			s[0] = c;                                    // Set Character to Draw
			canvas.drawText( s, 0, 1, x, y, paint );     // Draw Character
			x += cellWidth;                              // Move to Next Character
			if ( ( x + cellWidth - fontPadX ) > textureSize )  {  // IF End of Line Reached
				x = 0;//fontPadX;                             // Set X for New Row
				y += cellHeight;                          // Move Down a Row
			}
		}
		s[0] = CHAR_NONE;                               // Set Character to Use for NONE
		canvas.drawText( s, 0, 1, x, y, paint );        // Draw Character

		// save the bitmap in a texture
		textureId = TextureHelper.loadTexture(bitmap);

		// setup the array of character texture regions
		x = 0;                                          // Initialize X
		y = 0;                                          // Initialize Y
		for ( int c = 0; c < CHAR_CNT; c++ )  {         // FOR Each Character (On Texture)
			charRgn[c] = new TextureRegion( textureSize, textureSize, x-1, y, charWidths[c]+2, cellHeight );  //cellWidth-1, cellHeight-1 );  // Create Region for Character
			x += cellWidth;                              // Move to Next Char (Cell)
			if ( x + cellWidth > textureSize )  {
				x = 0;                                    // Reset X Position to Start
				y += cellHeight;                          // Move to Next Row (Cell)
			}
		}

		// create full texture region
		textureRgn = new TextureRegion( textureSize, textureSize, 0, 0, textureSize, textureSize );  // Create Full Texture Region

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
        TextureUniformHandle = GLES20.glGetUniformLocation(program, TEXTURE_UNIFORM);
        //mMVPMatricesHandle  = GLES20.glGetUniformLocation(program,MVP_MATRIX_UNIFORM);
		
		GLES20.glUniform4fv(ColorHandle, 1, color , 0); 
		GLES20.glEnableVertexAttribArray(ColorHandle);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // Set the active texture unit to texture unit 0
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
		
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
		GLES20.glUniform1i(TextureUniformHandle, 0); 
		

		//mVPMatrix = pMatrix;		
	}
	
    public void SetMvpMatrix(float[] mvpMatrix)
    {
    	
    	mMVPMatrix = mvpMatrix;
    }
    
	public void DrawSprite(float x, float y, float width, float height, TextureRegion region, float[] modelMatrix)  {

		//if(width<20.0f)
		//	width= 20.0f;
		float halfWidth = width ;/// 2.0f;                 // Calculate Half Width
		float halfHeight = height;// / 2.0f;               // Calculate Half Height
		float x1 = x ;//- halfWidth;                       // Calculate Left X
		float y1 = y ;//- halfHeight;                      // Calculate Bottom Y
		float x2 = x + halfWidth;                          // Calculate Right X
		float y2 = y + halfHeight;                         // Calculate Top Y
		//float vertexBuffer[] =new float[5*4];
		//short indexBuffer[] =new short[1*6];
		
		vertexBuffer[bufferIndex++] = x1;               // Add X for Vertex 0
		vertexBuffer[bufferIndex++] = y1;               // Add Y for Vertex 0
		vertexBuffer[bufferIndex++] = region.u1;        // Add U for Vertex 0
		vertexBuffer[bufferIndex++] = region.v2;        // Add V for Vertex 0
		vertexBuffer[bufferIndex++] = matrixIndex;

		vertexBuffer[bufferIndex++] = x2;               // Add X for Vertex 1
		vertexBuffer[bufferIndex++] = y1;               // Add Y for Vertex 1
		vertexBuffer[bufferIndex++] = region.u2;        // Add U for Vertex 1
		vertexBuffer[bufferIndex++] = region.v2;        // Add V for Vertex 1
		vertexBuffer[bufferIndex++] = matrixIndex;

		vertexBuffer[bufferIndex++] = x2;               // Add X for Vertex 2
		vertexBuffer[bufferIndex++] = y2;               // Add Y for Vertex 2
		vertexBuffer[bufferIndex++] = region.u2;        // Add U for Vertex 2
		vertexBuffer[bufferIndex++] = region.v1;        // Add V for Vertex 2
		vertexBuffer[bufferIndex++] = matrixIndex;

		vertexBuffer[bufferIndex++] = x1;               // Add X for Vertex 3
		vertexBuffer[bufferIndex++] = y2;               // Add Y for Vertex 3
		vertexBuffer[bufferIndex++] = region.u1;        // Add U for Vertex 3
		vertexBuffer[bufferIndex++] = region.v1;        // Add V for Vertex 3
		vertexBuffer[bufferIndex++] = matrixIndex;

		// add the sprite mvp matrix to uMVPMatrices array
		
		Matrix.multiplyMM(mVPMatrix, 0, mMVPMatrix , 0, modelMatrix, 0);

		//TODO: make sure numSprites < 24
		for (int m = 0; m < 16; m++) {
			uMVPMatrices[matrixIndex*16+m] = mVPMatrix[m];
		}
		
		numSprites++;                                   // Increment Sprite Count

		
}
	
public void RenderFont(){
	
	
	
	// add the sprite mvp matrix to uMVPMatrices array

	
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
				VertexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexDataBuffer.capacity()
				* 2, IndexDataBuffer, GLES20.GL_DYNAMIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	} 
	
	if (vbo[0] > 0 && ibo[0] > 0) {		
		
		GLES20.glUseProgram(program);

		// Set program handles for cube drawing.
		mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
		//GLES20.glUniformMatrix4fv(mvpMatrixUniform, numSprites, false, modelMatrix, 0);
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, matrixIndex, false, uMVPMatrices, 0);
		//GLES20.glEnableVertexAttribArray(mvpMatrixUniform);
		//mvMatrixUniform = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
		//lightPosUniform = GLES20.glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);
		positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
		//normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
		texcordAttribute = GLES20.glGetAttribLocation(program, TEXCORD_ATTRIBUTE);
		matrixIndexAttribute= GLES20.glGetAttribLocation(program, MATRIX_INDEX_ATTRIBUTE);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);


		GLES20.glVertexAttribPointer(positionAttribute, 2,GLES20.GL_FLOAT, false, 5*4, 0);
		GLES20.glEnableVertexAttribArray(positionAttribute);

		GLES20.glVertexAttribPointer(texcordAttribute, 2, GLES20.GL_FLOAT, false,5*4, 2*4);
		GLES20.glEnableVertexAttribArray(texcordAttribute);		
		
		// bind MVP Matrix index position handle

		GLES20.glVertexAttribPointer(matrixIndexAttribute, 1, GLES20.GL_FLOAT, false, 5*4, 4*4);
		
		GLES20.glEnableVertexAttribArray(matrixIndexAttribute);		
		
		
		
		// Draw
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, numSprites*INDICES_PER_SPRITE, GLES20.GL_UNSIGNED_SHORT, 0);
		//GLES20.glDrawElements(GLES20.GL_TRIANGLES, 12, GLES20.GL_UNSIGNED_SHORT, 0);
		//GLES20.glDrawElements(GLES20.GL_LINES, 4, GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDisableVertexAttribArray(texcordAttribute);	
		GLES20.glUseProgram(0);
		
	}		
	
	bufferIndex =0;
	matrixIndex =0;
	numSprites  =0;
}

	//--Draw Text--//
	// D: draw text at the specified x,y position
	// A: text - the string to draw
	//    x, y, z - the x, y, z position to draw text at (bottom left of text; including descent)
	//    angleDeg - angle to rotate the text
	// R: [none]
	public void draw(String text, float x, float y, float z, float angleDegX, float angleDegY, float angleDegZ)  {
		float chrHeight = cellHeight * scaleY;          // Calculate Scaled Character Height
		float chrWidth = cellWidth * scaleX;            // Calculate Scaled Character Width
		int len = text.length() *INDICES_PER_SPRITE;                        // Get String Length
		int len1 = text.length();
		if(len<0)
			len =0;
		if(len >CHAR_BATCH_SIZE*INDICES_PER_SPRITE)
			len = CHAR_BATCH_SIZE*INDICES_PER_SPRITE;
		//x =0;//+= ( chrWidth / 2.0f ) - ( fontPadX * scaleX );  // Adjust Start X
		//y += ( chrHeight / 2.0f ) - ( fontPadY * scaleY );  // Adjust Start Y
		float letterX, letterY; 
		letterX = letterY = 0;


		short j = (short)(numSprites*VERTICES_PER_SPRITE);                                    // Counter
		for ( int k = numSprites*INDICES_PER_SPRITE; k < len +numSprites*INDICES_PER_SPRITE; k+= INDICES_PER_SPRITE, j += VERTICES_PER_SPRITE)  {  // FOR Each Index Set (Per Sprite)
			indexBuffer[k + 0] = (short)( j + 0 );           // Calculate Index 0
			indexBuffer[k + 1] = (short)( j + 1 );           // Calculate Index 1
			indexBuffer[k + 2] = (short)( j + 2 );           // Calculate Index 2
			indexBuffer[k + 3] = (short)( j + 2 );           // Calculate Index 3
			indexBuffer[k + 4] = (short)( j + 3 );           // Calculate Index 4
			indexBuffer[k + 5] = (short)( j + 0 );           // Calculate Index 5
		}		
		

		// create a model matrix based on x, y and angleDeg
		float[] modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, x, y, z);		
		Matrix.rotateM(modelMatrix, 0, angleDegZ, 0, 0, 1);
		Matrix.rotateM(modelMatrix, 0, angleDegX, 1, 0, 0);
		Matrix.rotateM(modelMatrix, 0, angleDegY, 0, 1, 0);
		
		//Matrix.multiplyMM(mVPMatrix , 0, mMVPMatrix, 0, modelMatrix, 0);
		
		
		for (int i = 0; i < len1; i++)  {              // FOR Each Character in String
			int c = (int)text.charAt(i) - CHAR_START;  // Calculate Character Index (Offset by First Char in Font)
			if (c <= 0 || c >= CHAR_CNT )                // IF Character Not In Font
			{//	c = 95;                         // Set to Unknown Character Index
			  letterX += (float)(charWidths[13] ) * scaleX;
			}//TODO: optimize - applying the same model matrix to all the characters in the string
			else
			{
				

				//Matrix.multiplyMM(modelMatrix, 0, mVPMatrix , 0, mMVPMatrix, 0);
				
				DrawSprite(letterX, letterY, (charWidths[c])*scaleX, chrHeight, charRgn[c], modelMatrix);  // Draw the Character
			
				letterX += (float)(charWidths[c] ) * scaleX;
				
				
			}
			
			//letterX += (float)(charWidths[c]) * scaleX;//cellWidth * scaleX/2.0f;//(charWidths[c]) * scaleX;    // Advance X Position by Scaled Character Width
		}
		matrixIndex++;
		//RenderFont();
		

	}
	public void draw(String text, float x, float y, float z, float angleDegZ) {
		draw(text, x, y, z, 0, 0, angleDegZ);
	}
	public void draw(String text, float x, float y, float angleDeg) {
		draw(text, x, y, 0, angleDeg);
	}
	
	public void draw(String text, float x, float y) {
		draw(text, x, y, 0, 0);
	}

	//--Draw Text Centered--//
	// D: draw text CENTERED at the specified x,y position
	// A: text - the string to draw
	//    x, y, z - the x, y, z position to draw text at (bottom left of text)
	//    angleDeg - angle to rotate the text
	// R: the total width of the text that was drawn
	public float drawC(String text, float x, float y, float z, float angleDegX, float angleDegY, float angleDegZ)  {
		float len = getLength( text );                  // Get Text Length
		//draw( text, x - ( len / 2.0f ), y - ( getCharHeight() / 2.0f ), z, angleDegX, angleDegY, angleDegZ );  // Draw Text Centered
		draw( text, x , y - ( getCharHeight() / 2.0f ), z, angleDegX, angleDegY, angleDegZ );  // Draw Text Centered
		return len;                                     // Return Length
	}
	public float drawC(String text, float x, float y, float z, float angleDegZ) {
		return drawC(text, x, y, z, 0, 0, angleDegZ);
	}
	public float drawC(String text, float x, float y, float angleDeg) {
		return drawC(text, x, y, 0, angleDeg);
	}
	public float drawC(String text, float x, float y) {
		float len = getLength( text );                  // Get Text Length
		return drawC(text, x - (len / 2.0f), y - ( getCharHeight() / 2.0f ), 0);
		
	}
	public float drawCX(String text, float x, float y)  {
		float len = getLength( text );                  // Get Text Length
		draw( text, x - ( len / 2.0f ), y );            // Draw Text Centered (X-Axis Only)
		return len;                                     // Return Length
	}
	public void drawCY(String text, float x, float y)  {
		draw( text, x, y - ( getCharHeight() / 2.0f ) );  // Draw Text Centered (Y-Axis Only)
	}

	//--Set Scale--//
	// D: set the scaling to use for the font
	// A: scale - uniform scale for both x and y axis scaling
	//    sx, sy - separate x and y axis scaling factors
	// R: [none]
	public void setScale(float scale)  {
		scaleX = scaleY = scale;                        // Set Uniform Scale
	}
	public void setScale(float sx, float sy)  {
		scaleX = sx;                                    // Set X Scale
		scaleY = sy;                                    // Set Y Scale
	}

	//--Get Scale--//
	// D: get the current scaling used for the font
	// A: [none]
	// R: the x/y scale currently used for scale
	public float getScaleX()  {
		return scaleX;                                  // Return X Scale
	}
	public float getScaleY()  {
		return scaleY;                                  // Return Y Scale
	}

	//--Set Space--//
	// D: set the spacing (unscaled; ie. pixel size) to use for the font
	// A: space - space for x axis spacing
	// R: [none]
	public void setSpace(float space)  {
		spaceX = space;                                 // Set Space
	}

	//--Get Space--//
	// D: get the current spacing used for the font
	// A: [none]
	// R: the x/y space currently used for scale
	public float getSpace()  {
		return spaceX;                                  // Return X Space
	}

	//--Get Length of a String--//
	// D: return the length of the specified string if rendered using current settings
	// A: text - the string to get length for
	// R: the length of the specified string (pixels)
	public float getLength(String text) {
		float len = 0.0f;                               // Working Length
		int strLen = text.length();                     // Get String Length (Characters)
		for ( int i = 0; i < strLen; i++ )  {           // For Each Character in String (Except Last
			int c = (int)text.charAt( i ) - CHAR_START;  // Calculate Character Index (Offset by First Char in Font)
			len += ( charWidths[c] * scaleX );           // Add Scaled Character Width to Total Length
		}
		len += ( strLen > 1 ? ( ( strLen - 1 ) * spaceX ) * scaleX : 0 );  // Add Space Length
		return len;                                     // Return Total Length
	}

	//--Get Width/Height of Character--//
	// D: return the scaled width/height of a character, or max character width
	//    NOTE: since all characters are the same height, no character index is required!
	//    NOTE: excludes spacing!!
	// A: chr - the character to get width for
	// R: the requested character size (scaled)
	public float getCharWidth(char chr)  {
		int c = chr - CHAR_START;                       // Calculate Character Index (Offset by First Char in Font)
		return ( charWidths[c] * scaleX );              // Return Scaled Character Width
	}
	public float getCharWidthMax()  {
		return ( charWidthMax * scaleX );               // Return Scaled Max Character Width
	}
	public float getCharHeight() {
		return ( charHeight * scaleY );                 // Return Scaled Character Height
	}

	//--Get Font Metrics--//
	// D: return the specified (scaled) font metric
	// A: [none]
	// R: the requested font metric (scaled)
	public float getAscent()  {
		return ( fontAscent * scaleY );                 // Return Font Ascent
	}
	public float getDescent()  {
		return ( fontDescent * scaleY );                // Return Font Descent
	}
	public float getHeight()  {
		return ( fontHeight * scaleY );                 // Return Font Height (Actual)
	}

}
