package com.BinaryPulse.ProjectX.lesson8;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.BinaryPulse.ProjectX.R;
import com.BinaryPulse.ProjectX.common.RawResourceReader;
import com.BinaryPulse.ProjectX.common.ShaderHelper;
import com.BinaryPulse.ProjectX.common.TextureHelper;
import com.BinaryPulse.ProjectX.lesson8.ErrorHandler.ErrorType;

import com.BinaryPulse.ProjectX.MyFont.MyFont;
import com.BinaryPulse.ProjectX.MyUI.OscilloScope;
import android.util.DisplayMetrics;
/**
 * This class implements our custom renderer. Note that the GL10 parameter
 * passed in is unused for OpenGL ES 2.0 renderers -- the static class GLES20 is
 * used instead.
 */
public class LessonEightRenderer implements GLSurfaceView.Renderer {
	/** Used for debug logs. */
	private static final String TAG = "LessonEightRenderer";

	/** References to other main objects. */
	private final LessonEightActivity lessonEightActivity;
	private final ErrorHandler errorHandler;


	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private final float[] modelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	private final float[] viewMatrix = new float[16];
	
	private final float[] viewMatrixFont = new float[16];

	/**
	 * Store the projection matrix. This is used to project the scene onto a 2D
	 * viewport.
	 */
	private final float[] projectionMatrix = new float[16];
	

	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	private final float[] mvpMatrix = new float[16];

	/** Additional matrices. */
	private final float[] accumulatedRotation = new float[16];
	private final float[] currentRotation = new float[16];
	private final float[] lightModelMatrix = new float[16];
	private final float[] temporaryMatrix = new float[16];

	/** OpenGL handles to our program uniforms. */
	private int mvpMatrixUniform;
	private int mvMatrixUniform;
	private int lightPosUniform;

	/** OpenGL handles to our program attributes. */
	private int positionAttribute;
	private int normalAttribute;
	private int colorAttribute;

	/** Identifiers for our uniforms and attributes inside the shaders. */
	private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
	private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
	private static final String LIGHT_POSITION_UNIFORM = "u_LightPos";

	private static final String POSITION_ATTRIBUTE = "a_Position";
	private static final String NORMAL_ATTRIBUTE = "a_Normal";
	private static final String COLOR_ATTRIBUTE = "a_Color";

	/** Additional constants. */
	private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
	private static final int NORMAL_DATA_SIZE_IN_ELEMENTS = 3;
	private static final int COLOR_DATA_SIZE_IN_ELEMENTS = 4;

	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;

	private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS)
			* BYTES_PER_FLOAT;

	/**
	 * Used to hold a light centered on the origin in model space. We need a 4th
	 * coordinate so we can get translations to work when we multiply this by
	 * our transformation matrices.
	 */
	private final float[] lightPosInModelSpace = new float[] { 0.0f, 10.0f, 0.0f, 1.0f };

	/**
	 * Used to hold the current position of the light in world space (after
	 * transformation via model matrix).
	 */
	private final float[] lightPosInWorldSpace = new float[4];

	/**
	 * Used to hold the transformed position of the light in eye space (after
	 * transformation via modelview matrix)
	 */
	private final float[] lightPosInEyeSpace = new float[4];

	/** This is a handle to our cube shading program. */
	private int program;

	/** Retain the most recent delta for touch events. */
	// These still work without volatile, but refreshes are not guaranteed to
	// happen.
	public volatile float deltaX;
	public volatile float deltaY;
	public volatile float deltaZ;
	public volatile float[] rotorSpeed;
	/** The current heightmap object. */
	private HeightMap heightMap,tower,nacelle,porche;
	
	private MyFont glText;                             // A GLText Instance
    private OscilloScope OscilloScope_1;
    
    private DisplayMetrics dm;
    private int windowWidth;
    private int windowHeight;
	/**
	 * Initialize the model data.
	 */
	public LessonEightRenderer(final LessonEightActivity lessonEightActivity, ErrorHandler errorHandler) {
		this.lessonEightActivity = lessonEightActivity;
		this.errorHandler = errorHandler;
		
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		heightMap = new HeightMap();
		tower= new HeightMap();
		nacelle = new HeightMap(); 
		porche = new HeightMap(); 
		// Set the background clear color to black.
		//////GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable depth testing
		//////GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// Position the eye in front of the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = -0.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;
		rotorSpeed = new float[5];
		// Set the view matrix. This matrix can be said to represent the camera
		// position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination
		// of a model and view matrix. In OpenGL 2, we can keep track of these
		// matrices separately if we choose.
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

		final String vertexShader = RawResourceReader.readTextFileFromRawResource(lessonEightActivity,
				R.raw.per_pixel_vertex_shader_no_tex1);
		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(lessonEightActivity,
				R.raw.per_pixel_fragment_shader_no_tex);

		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		//program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
		//		POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE, COLOR_ATTRIBUTE });
		program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
				POSITION_ATTRIBUTE, NORMAL_ATTRIBUTE});
		heightMap.MeshDataReader(lessonEightActivity,
				R.raw.blade);
		tower.MeshDataReader(lessonEightActivity,
				R.raw.tower);	
		nacelle.MeshDataReader(lessonEightActivity,
				R.raw.nacelle);	
	

		dm = new DisplayMetrics();
		lessonEightActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		windowWidth = dm.widthPixels;
		windowHeight = dm.heightPixels;		
		
		
		// Create the GLText
		
		// Set the background frame color
		GLES20.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
		glText = new MyFont(lessonEightActivity,(lessonEightActivity.getAssets()));
		GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		// Load the font from file (set size + padding), creates the texture
		// NOTE: after a successful call to this the font is ready for rendering!
		glText.load( "Roboto-Regular.ttf", 18, 0, 0);  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
		// enable texture + alpha blending

		OscilloScope_1=new OscilloScope(lessonEightActivity,0,100.0f,10.0f,1.0f,(float)windowWidth,(float)windowHeight,2.0f);	
	    OscilloScope_1.SetScopeParameters(0,0, 100,200, 4);//, "123",{1.0f,1.0f,1.0f}, 0.001,  20000,10,5);
	    OscilloScope_1.SetDispWiodowSize(windowWidth,windowHeight);	
		//porche.PorcheDataReader(lessonEightActivity,
		//		R.raw.porsche);	
		// Initialize the accumulated rotation matrix
		Matrix.setIdentityM(accumulatedRotation, 0);
		
		// Load the texture
		final int mAndroidDataHandle = TextureHelper.loadTexture(lessonEightActivity, R.drawable.usb_android);		
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);		
   
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the
		// same while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio*1.0f;
		final float right = ratio*1.0f;
		final float bottom = -1.0f*1.0f;
		final float top = 1.0f*1.0f;
		final float near = 1.0f;
		final float far = 1000.0f;

		Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
		//Matrix.perspectiveM(projectionMatrix,0,  1.0f, ratio, 1.0f, 1000.0f);
			
		/*int useForOrtho = Math.min(width, height);
		
		//TODO: Is this wrong?
		Matrix.orthoM(viewMatrixFont, 0,
				-useForOrtho/2,
				useForOrtho/2,
				-useForOrtho/2,
				useForOrtho/2, 0f, 1f);		*/
		Matrix.orthoM(viewMatrixFont, 0,
				-width/2,
				width/2,
				-height/2,
				height/2, 0f, 1f);		
		
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
	     
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		
		GLES20.glDisable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		// Set our per-vertex lighting program.
		GLES20.glUseProgram(program);

		// Set program handles for cube drawing.
		mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
		mvMatrixUniform = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
		lightPosUniform = GLES20.glGetUniformLocation(program, LIGHT_POSITION_UNIFORM);
		positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
		normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
		//colorAttribute = GLES20.glGetAttribLocation(program, COLOR_ATTRIBUTE);

		// Calculate position of the light. Push into the distance.
		DrawWindTurbine(0.0f,0.0f,70.0f,0.5f,0);
		//DrawWindTurbine(30.0f,0.0f,20.0f,0.2f,1);
		DrawWindTurbine(-60.0f,0.0f,50.0f,1.2f,2);
		DrawWindTurbine(60.0f,0.0f,30.0f,3.2f,3);
		GLES20.glUseProgram(0);
		
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrixFont, 0);
		
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		OscilloScope_1.Render(viewMatrixFont);
		// TEST: render the entire font texture
		//GLES20.glColorMask({0.0, 1.0, 0.0,0.5},0,0)
		//glText.drawTexture( width/2, height/2, mVPMatrix);            // Draw the Entire Texture
		GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		// TEST: render some strings with the font
		glText.SetColor( 1.0f, 0.0f, 0.0f, 1.0f, viewMatrixFont );         // Begin Text Rendering (Set Color WHITE)
		glText.drawC("Jason Mraz!", 80f, 100f, 0.0f, 0, 0, 0);
		glText.SetColor( 1.0f, 0.0f, 1.0f, 1.0f, viewMatrixFont );  
		glText.draw( Integer.toString(windowHeight), -10,-200, 60);
		
		
	/*	
		Matrix.setLookAtM(viewMatrix, 0, (80.0f-deltaY) *(float)java.lang.Math.cos(deltaX*0.015f),0.0f, (80.0f-deltaY)*(float)java.lang.Math.sin(deltaX*0.015f), 0.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f);
		// Draw the heightmap.
		// Translate the heightmap into the screen.
		Matrix.setIdentityM(modelMatrix, 0);
		//Matrix.rotateM(modelMatrix, 0, deltaZ, 0.0f, 1.0f, 0.0f);

		Matrix.translateM(modelMatrix, 0, 0.0f, 30.0f, -00.0f);
		Matrix.rotateM(modelMatrix, 0, deltaZ, 0.0f, 0.0f, 1.0f);

		//Matrix.rotateM(currentRotation, 0, deltaY, 0.0f, 1.0f, 0.0f);
		deltaZ += 1.5;
		// Set a matrix that contains the current rotation.

		Matrix.translateM(modelMatrix, 0, 0.0f, -70.0f, 0.0f);
		
		//Matrix.rotateM(modelMatrix, 0, deltaZ, 0.0f, 1.0f, 0.0f);
		
		//Matrix.translateM(modelMatrix, 0, 0.0f, 00.0f, -0.5f);
		
		Matrix.setIdentityM(currentRotation, 0);
		//Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
		//Matrix.translateM(currentRotation, 0, 0, 0, deltaX);
		//Matrix.translateM(currentRotation, 0, 0, deltaY, 0);
		//deltaX = 0.0f;
		//deltaY = 0.0f;

		// Multiply the current rotation by the accumulated rotation, and then
		// set the accumulated rotation to the result.
		Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0, accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0, 16);

		// Rotate the cube taking the overall rotation into account.
		Matrix.multiplyMM(temporaryMatrix, 0, modelMatrix, 0, accumulatedRotation, 0);
		System.arraycopy(temporaryMatrix, 0, modelMatrix, 0, 16);

		// This multiplies the view matrix by the model matrix, and stores
		// the result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

		
		//Matrix.setIdentityM(modelMatrix, 0);		
		//Matrix.setIdentityM(currentRotation, 0);
		//Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
		//Matrix.translateM(currentRotation, 0, 0, 0, deltaX);
		//Matrix.translateM(currentRotation, 0, 0, deltaY, 0);
		//deltaX = 0.0f;
		//deltaY = 0.0f;
		
		
		// Pass in the modelview matrix.
		GLES20.glUniformMatrix4fv(mvMatrixUniform, 1, false, mvpMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix,
		// and stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(temporaryMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);
		
		Matrix.setIdentityM(lightModelMatrix, 0);
		Matrix.translateM(lightModelMatrix, 0, 0.0f, 20.5f, -8.0f);

		Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
		Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);
		

		// Pass in the light position in eye space.
		GLES20.glUniform3f(lightPosUniform, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

		// Render the heightmap.
		heightMap.render();
		
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, 0.0f, -40.0f, 0.0f);
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(temporaryMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);
		GLES20.glUniform3f(lightPosUniform, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
		// Render the heightmap.
		tower.render();
		nacelle.render();
		//porche.render();
		 
		 */
		
		
		
	}
	
	 public void DrawWindTurbine(float moveX,float moveY,float moveZ,float rotoSpeed,int i)
	 {
		    //static float rotorSpeed =0;
			Matrix.setLookAtM(viewMatrix, 0, (80.0f-deltaY) *(float)java.lang.Math.cos(deltaX*0.015f),0.0f, (80.0f-deltaY)*(float)java.lang.Math.sin(deltaX*0.015f), 0.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f);
			// Draw the heightmap.
			// Translate the heightmap into the screen.
			Matrix.setIdentityM(modelMatrix, 0);
			//Matrix.rotateM(modelMatrix, 0, deltaZ, 0.0f, 1.0f, 0.0f);

			Matrix.translateM(modelMatrix, 0, moveX, 30.0f+moveY, moveZ);
			Matrix.rotateM(modelMatrix, 0, rotorSpeed[i], 0.0f, 0.0f, 1.0f);

			//Matrix.rotateM(currentRotation, 0, deltaY, 0.0f, 1.0f, 0.0f);
			rotorSpeed[i] += rotoSpeed;
			// Set a matrix that contains the current rotation.

			Matrix.translateM(modelMatrix, 0, 0.0f, -70.0f, 0.0f);
			
			//Matrix.rotateM(modelMatrix, 0, deltaZ, 0.0f, 1.0f, 0.0f);
			
			//Matrix.translateM(modelMatrix, 0, 0.0f, 00.0f, -0.5f);
			
			Matrix.setIdentityM(currentRotation, 0);
			//Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
			//Matrix.translateM(currentRotation, 0, 0, 0, deltaX);
			//Matrix.translateM(currentRotation, 0, 0, deltaY, 0);
			//deltaX = 0.0f;
			//deltaY = 0.0f;

			// Multiply the current rotation by the accumulated rotation, and then
			// set the accumulated rotation to the result.
			Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0, accumulatedRotation, 0);
			System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0, 16);

			// Rotate the cube taking the overall rotation into account.
			Matrix.multiplyMM(temporaryMatrix, 0, modelMatrix, 0, accumulatedRotation, 0);
			System.arraycopy(temporaryMatrix, 0, modelMatrix, 0, 16);

			// This multiplies the view matrix by the model matrix, and stores
			// the result in the MVP matrix
			// (which currently contains model * view).
			Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

			
			//Matrix.setIdentityM(modelMatrix, 0);		
			//Matrix.setIdentityM(currentRotation, 0);
			//Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
			//Matrix.translateM(currentRotation, 0, 0, 0, deltaX);
			//Matrix.translateM(currentRotation, 0, 0, deltaY, 0);
			//deltaX = 0.0f;
			//deltaY = 0.0f;
			
			
			// Pass in the modelview matrix.
			GLES20.glUniformMatrix4fv(mvMatrixUniform, 1, false, mvpMatrix, 0);

			// This multiplies the modelview matrix by the projection matrix,
			// and stores the result in the MVP matrix
			// (which now contains model * view * projection).
			Matrix.multiplyMM(temporaryMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
			System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

			// Pass in the combined matrix.
			GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);
			
			Matrix.setIdentityM(lightModelMatrix, 0);
			Matrix.translateM(lightModelMatrix, 0, 0.0f, 20.5f, -8.0f);

			Matrix.multiplyMV(lightPosInWorldSpace, 0, lightModelMatrix, 0, lightPosInModelSpace, 0);
			Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);
			

			// Pass in the light position in eye space.
			GLES20.glUniform3f(lightPosUniform, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

			// Render the heightmap.
			heightMap.render();
			
			Matrix.setIdentityM(modelMatrix, 0);
			Matrix.translateM(modelMatrix, 0, moveX, -40.0f+moveY, moveZ);
			Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(temporaryMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
			System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

			// Pass in the combined matrix.
			GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);
			GLES20.glUniform3f(lightPosUniform, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
			// Render the heightmap.
			tower.render();
			nacelle.render();
			//porche.render();
	

		 
	 }

	class HeightMap {
		static final int SIZE_PER_SIDE = 32;
		static final float MIN_POSITION = -5f;
		static final float POSITION_RANGE = 10f;

		final int[] vbo = new int[2];
		final int[] ibo = new int[1];
/*        final float[] kzVertices = RawResourceReader.MeshDataReader(lessonEightActivity,
				R.raw.blade);*/
		int indexCount;

		HeightMap() {
			try {
				/*final int floatsPerVertex = POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS
						+ COLOR_DATA_SIZE_IN_ELEMENTS;
				final int xLength = SIZE_PER_SIDE;
				final int yLength = SIZE_PER_SIDE;

				final float[] heightMapVertexData = new float[xLength * yLength * floatsPerVertex];

				int offset = 0;

				// First, build the data for the vertex buffer
				for (int y = 0; y < yLength; y++) {
					for (int x = 0; x < xLength; x++) {
						final float xRatio = x / (float) (xLength - 1);
						
						// Build our heightmap from the top down, so that our triangles are counter-clockwise.
						final float yRatio = 1f - (y / (float) (yLength - 1));
						
						final float xPosition = MIN_POSITION + (xRatio * POSITION_RANGE);
						final float yPosition = MIN_POSITION + (yRatio * POSITION_RANGE);

						// Position
						heightMapVertexData[offset++] = xPosition;
						heightMapVertexData[offset++] = yPosition;
						heightMapVertexData[offset++] = ((xPosition * xPosition) + (yPosition * yPosition)) / 10f;					

						// Cheap normal using a derivative of the function.
						// The slope for X will be 2X, for Y will be 2Y.
						// Divide by 10 since the position's Z is also divided by 10.
						final float xSlope = (2 * xPosition) / 10f;
						final float ySlope = (2 * yPosition) / 10f;
						
						// Calculate the normal using the cross product of the slopes.
						final float[] planeVectorX = {1f, 0f, xSlope};
						final float[] planeVectorY = {0f, 1f, ySlope};
						final float[] normalVector = {
								(planeVectorX[1] * planeVectorY[2]) - (planeVectorX[2] * planeVectorY[1]),
								(planeVectorX[2] * planeVectorY[0]) - (planeVectorX[0] * planeVectorY[2]),
								(planeVectorX[0] * planeVectorY[1]) - (planeVectorX[1] * planeVectorY[0])};
						
						// Normalize the normal						
						final float length = Matrix.length(normalVector[0], normalVector[1], normalVector[2]);

						heightMapVertexData[offset++] = normalVector[0] / length;
						heightMapVertexData[offset++] = normalVector[1] / length;
						heightMapVertexData[offset++] = normalVector[2] / length;

						// Add some fancy colors.
						heightMapVertexData[offset++] = xRatio;
						heightMapVertexData[offset++] = yRatio;
						heightMapVertexData[offset++] = 0.5f;
						heightMapVertexData[offset++] = 1f;
					}
				}

				// Now build the index data
				final int numStripsRequired = yLength - 1;
				final int numDegensRequired = 2 * (numStripsRequired - 1);
				final int verticesPerStrip = 2 * xLength;

				final short[] heightMapIndexData = new short[(verticesPerStrip * numStripsRequired) + numDegensRequired];

				offset = 0;

				for (int y = 0; y < yLength - 1; y++) {
					if (y > 0) {
						// Degenerate begin: repeat first vertex
						heightMapIndexData[offset++] = (short) (y * yLength);
					}

					for (int x = 0; x < xLength; x++) {
						// One part of the strip
						heightMapIndexData[offset++] = (short) ((y * yLength) + x);
						heightMapIndexData[offset++] = (short) (((y + 1) * yLength) + x);
					}

					if (y < yLength - 2) {
						// Degenerate end: repeat last vertex
						heightMapIndexData[offset++] = (short) (((y + 1) * yLength) + (xLength - 1));
					}
				}

				indexCount = heightMapIndexData.length;

				final FloatBuffer heightMapVertexDataBuffer = ByteBuffer
						.allocateDirect(heightMapVertexData.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder())
						.asFloatBuffer();
				heightMapVertexDataBuffer.put(heightMapVertexData).position(0);

				final ShortBuffer heightMapIndexDataBuffer = ByteBuffer
						.allocateDirect(heightMapIndexData.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
						.asShortBuffer();
				heightMapIndexDataBuffer.put(heightMapIndexData).position(0);

				GLES20.glGenBuffers(1, vbo, 0);
				GLES20.glGenBuffers(1, ibo, 0);

				if (vbo[0] > 0 && ibo[0] > 0) {
					GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
					GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, heightMapVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
							heightMapVertexDataBuffer, GLES20.GL_STATIC_DRAW);

					GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
					GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, heightMapIndexDataBuffer.capacity()
							* BYTES_PER_SHORT, heightMapIndexDataBuffer, GLES20.GL_STATIC_DRAW);

					GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
					GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
				} else {
					errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR, "glGenBuffers");
				}*/
				
				
				
				
			} catch (Throwable t) {
				Log.w(TAG, t);
				errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR, t.getLocalizedMessage());
			}
		}

		void render() {
			/*if (vbo[0] > 0 && ibo[0] > 0) {				
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

				// Bind Attributes
				GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, 0);
				GLES20.glEnableVertexAttribArray(positionAttribute);

				GLES20.glVertexAttribPointer(normalAttribute, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, POSITION_DATA_SIZE_IN_ELEMENTS * BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(normalAttribute);

				GLES20.glVertexAttribPointer(colorAttribute, COLOR_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						STRIDE, (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT);
				GLES20.glEnableVertexAttribArray(colorAttribute);

				// Draw
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
				
			}*/
			if (vbo[0] > 0 && ibo[0] > 0 && vbo[1] > 0) {				
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

				// Bind Attributes
				GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						0, 0);
				GLES20.glEnableVertexAttribArray(positionAttribute);

				GLES20.glVertexAttribPointer(normalAttribute, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
						0, 0);
				GLES20.glEnableVertexAttribArray(normalAttribute);

				//GLES20.glVertexAttribPointer(colorAttribute, COLOR_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
				//		STRIDE, (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS) * BYTES_PER_FLOAT);
				//GLES20.glEnableVertexAttribArray(colorAttribute);

				// Draw
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
				//GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);
				GLES20.glDrawElements(GLES20.GL_LINES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);

				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
				
			}			
			
		}

		void MeshDataReader(final Context context,
				final int resourceId)
		{
			final InputStream inputStream = context.getResources().openRawResource(
					resourceId);
			final InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			final BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String nextLine,line;
			final StringBuilder body = new StringBuilder();
			final float[] Vertices;// = new float[1307*3];
			final float[] Normals;// = new float[1307*3];
			//final short[] Indexes = new short[2148*3];
			//final float[] Indexes = new float[2148*3];
			final short[] Index;// = new short[2148*3];
			/*
			final float[] Vertices = new float[5*3];
			final float[] Normals = new float[5*3];
			final short[] Indexes = new short[2*3];	*/		
			
			try
			{   
				int i,j;
				while ((nextLine = bufferedReader.readLine()) != null)
				{
					body.append(nextLine);
					//body.append('\n');
				}
				line = body.toString();
				//if(line.startsWith("Vertices") )
				{
					String[] tokens =line.split(":");
					String[] arraynum = tokens[1].split(",");
					String[] parts1 = tokens[2].split(",");
					String[] parts2 = tokens[3].split(",");
					String[] parts3 = tokens[4].split(",");
					j = (int)Float.parseFloat(arraynum[0]);
					Vertices = new float[j*3];
					Normals = new float[j*3];
					//final short[] Indexes = new short[2148*3];
					//final float[] Indexes = new float[2148*3];
				
					
					for(i=0;i<j*3;i++){
						Vertices[i] = Float.parseFloat(parts1[i]);	
						Normals[i] = Float.parseFloat(parts2[i]);
					}
					
					j = (int)Float.parseFloat(arraynum[1]);
					Index = new short[j*3];	
					for(i=0;i<j*3;i++){
						//parts3[i].replaceAll(" +", ""); 
						//parts3[i].replaceAll("\n", ""); 
						//parts3[i].replaceAll("[^0-9.]","");

						//Indexes[i] = Short.parseShort(parts3[i]);	
						float tempIndexes = Float.parseFloat(parts3[i]);	
						Index[i] = (short)tempIndexes;
					}			     
					//return 	Vertices;	 
					final FloatBuffer BladeVertexDataBuffer = ByteBuffer
							.allocateDirect(Vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder())
							.asFloatBuffer();
					BladeVertexDataBuffer.put(Vertices).position(0);
					
					final FloatBuffer BladeNormalDataBuffer = ByteBuffer
							.allocateDirect(Normals.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder())
							.asFloatBuffer();
					BladeNormalDataBuffer.put(Normals).position(0);
					
					final ShortBuffer BladeIndexDataBuffer = ByteBuffer
							.allocateDirect(Index.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
							.asShortBuffer();
					BladeIndexDataBuffer.put(Index).position(0);
					
					indexCount = Index.length;
					
					GLES20.glGenBuffers(2, vbo, 0);
					GLES20.glGenBuffers(1, ibo, 0);

					if (vbo[0] > 0 && ibo[0] > 0 && vbo[1] > 0) {
						GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
						GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, BladeVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
								BladeVertexDataBuffer, GLES20.GL_STATIC_DRAW);
						
						GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[1]);
						GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, BladeNormalDataBuffer.capacity() * BYTES_PER_FLOAT,
								BladeNormalDataBuffer, GLES20.GL_STATIC_DRAW);
						
						GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
						GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, BladeIndexDataBuffer.capacity()
								* BYTES_PER_SHORT, BladeIndexDataBuffer, GLES20.GL_STATIC_DRAW);

						GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
						GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
					} else {
						errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR, "glGenBuffers");
					}
					
					
				}
				//else
				//	 return Vertices;	
				
			}
			catch (IOException e)
			{
				//return null;
			}


		}	
		
		void PorcheDataReader(final Context context,
				final int resourceId)
		{
			
		 try{
			 final InputStream in = context.getResources().openRawResource(
					resourceId);
			List<String> lines = readLines(in);
			float[] vertices = new float[lines.size() * 3];
			float[] normals = new float[lines.size() * 3];
			float[] uv = new float[lines.size() * 2];
			
			int[] facesVerts = new int[lines.size() * 3];
			int[] facesNormals = new int[lines.size() * 3];
			int[] facesUV = new int[lines.size() * 3];
			
			
			int numVertices = 0;
			int numNormals = 0;
			int numUV = 0;
			int numFaces = 0;
			int vertexIndex =0;
			int normalIndex =0;
			int faceIndex =0;
			int uvIndex =0;
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				if (line.startsWith("v ")) {
					String[] tokens = line.split("[ ]+");
					vertices[vertexIndex] = Float.parseFloat(tokens[1]);
					vertices[vertexIndex + 1] = Float.parseFloat(tokens[2]);
					vertices[vertexIndex + 2] = Float.parseFloat(tokens[3]);
					vertexIndex += 3;
					numVertices++;
					continue;
					}
				if (line.startsWith("vn ")) {
					String[] tokens = line.split("[ ]+");
					normals[normalIndex] = Float.parseFloat(tokens[1]);
					normals[normalIndex + 1] = Float.parseFloat(tokens[2]);
					normals[normalIndex + 2] = Float.parseFloat(tokens[3]);
					normalIndex += 3;
					numNormals++;
					continue;
					}
				if (line.startsWith("vt")) {
					String[] tokens = line.split("[ ]+");
					uv[uvIndex] = Float.parseFloat(tokens[1]);
					uv[uvIndex + 1] = Float.parseFloat(tokens[2]);
					uvIndex += 2;
					numUV++;
					continue;
					}
				if (line.startsWith("f ")) {
					String[] tokens = line.split("[ ]+");
					String[] parts = tokens[1].split("/");
					facesVerts[faceIndex] = getIndex(parts[0], numVertices);
					if (parts.length > 2)
					facesNormals[faceIndex] = getIndex(parts[2], numNormals);
					if (parts.length > 1)
					facesUV[faceIndex] = getIndex(parts[1], numUV);
					faceIndex++;
					parts = tokens[2].split("/");
					facesVerts[faceIndex] = getIndex(parts[0], numVertices);
					if (parts.length > 2)
					facesNormals[faceIndex] = getIndex(parts[2], numNormals);
					if (parts.length > 1)
					facesUV[faceIndex] = getIndex(parts[1], numUV);
					faceIndex++;
					parts = tokens[3].split("/");
					facesVerts[faceIndex] = getIndex(parts[0], numVertices);
					if (parts.length > 2)
					facesNormals[faceIndex] = getIndex(parts[2], numNormals);
					if (parts.length > 1)
					facesUV[faceIndex] = getIndex(parts[1], numUV);
					faceIndex++;
					numFaces++;
					continue;
					}
				}
					//float[] verts = new float[(numFaces * 3)
			        //                  * (3 + (numNormals > 0 ? 3 : 0) + (numUV > 0 ? 2 : 0))];
	    /*
					float[] Vertices = new float[numFaces*9];
					float[] Normals = new float[numFaces*9];
					short[] Index = new short[numFaces*3];
					for (int i = 0, vi = 0,ni =0,ii =0; i < numFaces * 3; i++) {
						int vertexIdx = facesVerts[i] * 3;
						Vertices[vi++] = vertices[vertexIdx];
						Vertices[vi++] = vertices[vertexIdx + 1];
						Vertices[vi++] = vertices[vertexIdx + 2];
						if (numUV > 0) {
						int uvIdx = facesUV[i] * 2;
						verts[vi++] = uv[uvIdx];
						verts[vi++] = 1 - uv[uvIdx + 1];
						}
						if (numNormals > 0) {
							int normalIdx = facesNormals[i] * 3;
							Normals[ni++] = normals[normalIdx];
							Normals[ni++] = normals[normalIdx + 1];
							Normals[ni++] = normals[normalIdx + 2];
						}	
						Index[ii++] = (short)(3*i);
						Index[ii++] = (short)(3*i+1);
						Index[ii++] = (short)(3*i+2);
					}*/
			
			//float[] Vertices = new float[]{100.0f,50.0f,0.0f,10.0f,0.0f,180.0f,0.0f,10.0f,100.0f};
			//float[] Normals = new float[]{1.0f,1.0f,0.0f,1.0f,1.0f,0.0f,1.0f,1.0f,0.0f};
			//short[] Index = new short[]{0,1,2,1,1,2,0};	
			float[] Vertices = new float[numVertices*3];
			float[] Normals = new float[vertexIndex];
			short[] Index = new short[22011*3];
			for (int i = 0; i < numVertices; i++) {
				
				Vertices[3*i] = vertices[3*i]*20.0f;
				Vertices[3*i+1] = vertices[3*i+1]*20.0f;
				Vertices[3*i+2] = vertices[3*i+2]*20.0f;
				if(i<=7000){ //21554
					Normals[3*i ]   = vertices[3*i]*20.0f;
					Normals[3*i +1] = vertices[3*i+1]*20.0f;
					Normals[3*i +2] = vertices[3*i+2]*20.0f;
				}
				else{
				Normals[3*i ] =1.0f;
				Normals[3*i +1] =0.0f;
				Normals[3*i +2] =1.0f;
				}
				
			}
			for (int i = 0; i < 22011*3; i++) {
				Index[i] = (short)facesVerts[i];
			}		
					//return 	Vertices;	 
					final FloatBuffer BladeVertexDataBuffer = ByteBuffer
							.allocateDirect(Vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder())
							.asFloatBuffer();
					BladeVertexDataBuffer.put(Vertices).position(0);
					
					final FloatBuffer BladeNormalDataBuffer = ByteBuffer
							.allocateDirect(Normals.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder())
							.asFloatBuffer();
					BladeNormalDataBuffer.put(Normals).position(0);
					
					final ShortBuffer BladeIndexDataBuffer = ByteBuffer
							.allocateDirect(Index.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
							.asShortBuffer();
					BladeIndexDataBuffer.put(Index).position(0);
					
					indexCount = Index.length;
					
					GLES20.glGenBuffers(2, vbo, 0);
					GLES20.glGenBuffers(1, ibo, 0);

					if (vbo[0] > 0 && ibo[0] > 0 && vbo[1] > 0) {
						GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
						GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, BladeVertexDataBuffer.capacity() * BYTES_PER_FLOAT,
								BladeVertexDataBuffer, GLES20.GL_STATIC_DRAW);
						
						GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[1]);
						GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, BladeNormalDataBuffer.capacity() * BYTES_PER_FLOAT,
								BladeNormalDataBuffer, GLES20.GL_STATIC_DRAW);
						
						GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
						GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, BladeIndexDataBuffer.capacity()
								* BYTES_PER_SHORT, BladeIndexDataBuffer, GLES20.GL_STATIC_DRAW);

						GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
						GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
					} else {
						errorHandler.handleError(ErrorType.BUFFER_CREATION_ERROR, "glGenBuffers");
					}				
										
					
		   } catch (Exception ex) {
			   throw new RuntimeException("couldn't load '" + "'", ex);
				} finally {
					//if (in != null)
					//	try {
					//		in.close();
					//	} catch (Exception ex) {
					//	}
				}
		 
		 
	   }

	   int getIndex(String index, int size) {
			int idx = Integer.parseInt(index);
			if (idx < 0)
			return size + idx;
			else
			return idx - 1;
	   }
	   
	   List<String> readLines(InputStream in) throws IOException {
			List<String> lines = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null)
			lines.add(line);
			return lines;
		}
		
		void release() {
			if (vbo[0] > 0) {
				GLES20.glDeleteBuffers(vbo.length, vbo, 0);
				vbo[0] = 0;
				vbo[1] = 0;
			}

			if (ibo[0] > 0) {
				GLES20.glDeleteBuffers(ibo.length, ibo, 0);
				ibo[0] = 0;
			}
		}
	}
}
