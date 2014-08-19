package com.agl.example2;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.agl.example.GLRenderer;
import com.android.GLText.GLText;

/**
 * @author Sergu
 * Test application
 */
public class Renderer extends GLRenderer {
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
		glText = new GLText(mContext.getAssets());
        glText.load("ARBONNIE.ttf", 32, 2, 2 );
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		computeFPS();
		long time2 = SystemClock.uptimeMillis();
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		scene();
	}

	public void scene(){
		glText.begin( 1.0f, 1.0f, 1.0f, 1.0f, mMVPMatrix ); // Begin Text Rendering (Set Color WHITE)
		GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA); // active transparent bg
        glText.drawC("Nothing yet ! Under development", 200.f, 300.f,0.f);
        glText.end();
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
