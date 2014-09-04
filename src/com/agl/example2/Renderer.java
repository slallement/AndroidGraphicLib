package com.agl.example2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.agl.example.GLRenderer;
import com.agl.graphics.Sprite;
import com.android.GLText.GLText;

/**
 * @author Sergu
 * Test application
 */
public abstract class Renderer extends GLRenderer {
	float[] mProjMatrix = new float[16];
	float[] mVMatrix = new float[16];
	float[] mMVPMatrix = new float[16];
	private GLText glText; 
	
	Renderer(Context c)
	{
		super(c);
	}
	
	@Override
	public void onCreate(int width, int height, boolean contextLost) {
		Sprite.setShadersInit(false);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearColor(0f, 0f, 0f, 1f);
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		computeFPS();
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		scene();
		
		// text
		glText.begin( 1.0f, 1.0f, 1.0f, 1.0f, mMVPMatrix ); // Begin Text Rendering (Set Color WHITE)
		GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA); // active transparent bg
        glText.drawC("FPS :"+mFPS, 20.f, 10.f,0.f);
        glText.end();
	}

	public abstract void scene();
	
	public void draw(Sprite s){
		s.draw(mMVPMatrix);
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        glText = new GLText(mContext.getAssets());
        glText.load( "ARBONNIE.ttf", 32, 2, 2 ); // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
	}
	
	@Override
	/**
	 * Reset the viewport size to fit the new screen
	 */
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		mWidth = width;
		mHeight = height;
		GLES20.glViewport(0, 0, width, height);

		Matrix.orthoM(mProjMatrix, 0, 0.f, width / 1.f, 0.f, height / 1.f, 0.f,1.f);
		
		// Set the camera position (View matrix)
		Matrix.setLookAtM(mVMatrix, 0,
				0.f, 0.f, 1.f,
				0.f, 0.f, 0.f,
				0.f, 1.f, 0.f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        onCreate(mWidth, mHeight, mSurfaceCreated);
	}
}
