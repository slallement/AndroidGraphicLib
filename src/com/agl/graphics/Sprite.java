package com.agl.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.agl.example.GLES20Renderer;
import com.agl.graphics.R;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * @author Sergu 
 * Abstract class for all drawable elements
 */
public abstract class Sprite {
	protected static int mProgram = 0;
	protected static int idNoTexture = 0;
	private static boolean shadersInit = false;

	protected FloatBuffer vertexBuffer;
	protected FloatBuffer texCoordBuffer;
	protected int mPositionHandle;
	protected int mColorHandle;
	protected int mTexCoordHandle;
	protected int mTexDataHandle;
	int mMVPMatrixHandle;

	float trans[] = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	boolean transformed;
	float w, h;
	float x, y; // position

	// number of coordinates per vertex in this array
	static final int TEX_COORDS_PER_VERTEX = 2;
	static final int COORDS_PER_VERTEX = 3;
	protected float mCoords[] = null;// in counterclockwise order:

	// Set color with red, green, blue and alpha (opacity) values
	protected float color[] = { 1.f, 1.f, 1.f, 1.f };
	protected static float textureCoords[] = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			1.0f, 1.0f, 1.0f };

	protected int vertexCount;
	protected final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per
																// vertex

	// methods -----------------------------------------------------------------

	Sprite(float nx, float ny) {
		x = nx;
		y = ny;
	}

	Sprite() {
	}

	protected void init() {
		Matrix.setIdentityM(trans, 0);
		Matrix.translateM(trans, 0, x, y, 0);
		transformed = false;

		initVertexBuffer();

		initTexture();

		setDefaultShaders();

		setTextureDirect(idNoTexture);
	}

	protected void initVertexBuffer() {
		// mTexDataHandle = 0;
		vertexCount = mCoords.length / COORDS_PER_VERTEX;

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(mCoords.length * 4); // (number
																		// of
																		// coordinate
																		// values
																		// * 4
																		// bytes
																		// per
																		// float)
		bb.order(ByteOrder.nativeOrder()); // use the device hardware's native
											// byte order
		vertexBuffer = bb.asFloatBuffer(); // create a floating point buffer
											// from the ByteBuffer
		vertexBuffer.put(mCoords);
		vertexBuffer.position(0);
	}

	/** set texture buffer */
	protected void initTexture() {
		ByteBuffer tcbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
		tcbb.order(ByteOrder.nativeOrder());
		texCoordBuffer = tcbb.asFloatBuffer();
		texCoordBuffer.put(textureCoords);
		texCoordBuffer.position(0);
	}

	static protected void setDefaultShaders() {
		if (!isShadersInit()) {
			mProgram = GLES20.glCreateProgram();// create empty OpenGL Program
			int vertexShader = MUtils.loadShader(GLES20.GL_VERTEX_SHADER,
					GLES20Renderer.loadDataFromAsset("shaders_texture.vert"));
			int fragmentShader = MUtils.loadShader(GLES20.GL_FRAGMENT_SHADER,
					GLES20Renderer.loadDataFromAsset("shaders_texture.frag"));

			GLES20.glAttachShader(mProgram, vertexShader);
			// add the vertex shader to program
			GLES20.glAttachShader(mProgram, fragmentShader);
			// add the fragment shader to program
			GLES20.glLinkProgram(mProgram);
			idNoTexture = MUtils.loadTexture(GLES20Renderer.getContext(),
					R.drawable.blank)[0];
			setShadersInit(true);
		}
	}

	protected void updateVertexBuffer() {
		vertexCount = mCoords.length / COORDS_PER_VERTEX;
		ByteBuffer bb = ByteBuffer.allocateDirect(mCoords.length * 4);
		// (number of coordinate values * 4 bytes per float)
		bb.order(ByteOrder.nativeOrder());
		// use the device hardware's native byte order
		vertexBuffer = bb.asFloatBuffer();
		// create a floating point buffer from the ByteBuffer
		vertexBuffer.put(mCoords);
		vertexBuffer.position(0);
	}

	public void setColor(float r, float g, float b, float a)
	// warning : doesn't check if the color is valid
	{
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = a;
	}

	public void setColor(float r, float g, float b)
	// warning : doesn't check if the color is valid
	{
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}
	
	/** Translate the shape from origin 
	 * @param x
	 * @param y
	 */
	public void setPosition(float newX, float newY){
		Matrix.translateM(trans, 0, newX-x, newY-y, 0);
		x = newX;
		y = newY;
	}
	
	/** Translate the shape from its current position
	 * @param x
	 * @param y
	 */
	public void move(float dx, float dy){
		x += dx;
		y += dy;
		Matrix.translateM(trans, 0, dx, dy, 0);
	}

	/**
	 * Set the rotation of the sprite
	 * 
	 * @param angle in radians
	 * @param centerX center of the rotation (x coord)
	 * @param centerY center of the rotation (y coord)
	 */
	public void setRotation(float angle, float centerX, float centerY) {
		Matrix.setIdentityM(trans, 0);
		Matrix.translateM(trans, 0, x, y, 0);
		Matrix.translateM(trans, 0, -centerX, -centerY, 0);
		Matrix.rotateM(trans, 0, angle, 0, 0, 1.f);
		Matrix.translateM(trans, 0, centerX, centerY, 0);
	}

	/**
	 * Set the rotation of the sprite from its center
	 * 
	 * @param angle
	 */
	public void setRotation2(float angle) {
		float centerX = w / 2.f;
		float centerY = h / 2.f;
		Matrix.setIdentityM(trans, 0);
		Matrix.translateM(trans, 0, x, y, 0);
		Matrix.translateM(trans, 0, centerX, centerY, 0);
		Matrix.rotateM(trans, 0, angle, 0, 0, 1.f);
		Matrix.translateM(trans, 0, -centerX, -centerY, 0);
	}

	/**
	 * Set the rotation of the sprite from the origin
	 * 
	 * @param angle
	 */
	public void setRotation(float angle) {
		Matrix.setIdentityM(trans, 0);
		Matrix.translateM(trans, 0, x, y, 0);
		Matrix.rotateM(trans, 0, angle, 0, 0, 1.f);
	}

	/**
	 * Change the texture of the sprite
	 * 
	 * @param id id of the texture
	 */
	public void setTexture(int id)
	// id is a ressource identifier
	{
		mTexDataHandle = MUtils.loadTexture(GLES20Renderer.getContext(), id)[0];
	}

	/* public void setShaders(String path1, String path2){
	 * 
	 * } */

	/**
	 * Set texture and adapt the size of the sprite to fit the texture
	 * 
	 * @param id texture identifier
	 */
	public void setTextureFitting(int id)
	// id is a ressource identifier
	// this method adapt the size to fit the texture
	{
		int data[][] = MUtils.loadTexture2(GLES20Renderer.getContext(), id);
		mTexDataHandle = data[0][0];
		w = data[1][0];
		h = data[2][0];
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(mCoords.length * 4);
		// (number of coordinate values * 4 bytes per float)
		bb.order(ByteOrder.nativeOrder()); // use the device hardware's
											// native byte order
		vertexBuffer = bb.asFloatBuffer(); // create a floating point
											// buffer from the ByteBuffer
		vertexBuffer.put(mCoords);
		vertexBuffer.position(0);
	}

	/**
	 * Set the texture (not changing the sprite size)
	 * 
	 * @param id texture identifier
	 */
	public void setTextureDirect(int id) {
		mTexDataHandle = id;
	}

	/**
	 * Draw the shape with a certain transformation matrix and a specific shader
	 * 
	 * @param mvpMatrix transformation matrix
	 */
	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL ES environment
		GLES20.glUseProgram(mProgram);

		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
		int mTexHandle = GLES20.glGetUniformLocation(mProgram, "utexture");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexDataHandle);
		GLES20.glUniform1i(mTexHandle, 0);

		GLES20.glEnableVertexAttribArray(mTexCoordHandle);
		GLES20.glVertexAttribPointer(mTexCoordHandle, TEX_COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, 0, texCoordBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

		// Apply the projection and view transformation
		float res[] = new float[16];
		Matrix.setIdentityM(res, 0);
		Matrix.multiplyMM(res, 0, mvpMatrix, 0, trans, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, res, 0);

		drawShape();
		// Draw the triangle
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mTexCoordHandle);
		GLES20.glDisableVertexAttribArray(mPositionHandle);

		GLES20.glDisable(GLES20.GL_BLEND);
	}

	/**
	 * Draw the shape with a certain transformation matrix and a specific shader
	 * 
	 * @param mvpMatrix transformation matrix
	 * @param s shader
	 */
	public void draw(float[] mvpMatrix, Shaders s) {
		s.draw();

		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
		int mTexHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexDataHandle);
		GLES20.glUniform1i(mTexHandle, 0);

		GLES20.glEnableVertexAttribArray(mTexCoordHandle);
		GLES20.glVertexAttribPointer(mTexCoordHandle, TEX_COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, 0, texCoordBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

		// Apply the projection and view transformation
		float res[] = new float[16];
		Matrix.setIdentityM(res, 0);
		Matrix.multiplyMM(res, 0, mvpMatrix, 0, trans, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, res, 0);

		drawShape();

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mTexCoordHandle);
		GLES20.glDisableVertexAttribArray(mPositionHandle);

		GLES20.glDisable(GLES20.GL_BLEND);
	}

	/**
	 * Draw the sprite (as if it was a rectangle by default)
	 */
	protected void drawShape() {
		// Draw the rect
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
	}

	public static boolean isShadersInit() {
		return shadersInit;
	}

	public static void setShadersInit(boolean shadersInit) {
		Sprite.shadersInit = shadersInit;
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
}
