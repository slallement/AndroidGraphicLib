package com.agl.example;

import com.agl.graphics.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.agl.graphics.Circle;
import com.agl.graphics.Layer;
import com.agl.graphics.PolyLine;
import com.agl.graphics.Polygon;
import com.agl.graphics.Rect;
import com.agl.graphics.Shaders;
import com.agl.graphics.Sprite;
import com.agl.graphics.Triangle;
import com.android.GLText.GLText;


import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author Serguei Lallement
 * Test application
 */
public class GLES20Renderer extends GLRenderer {
	float[] mProjMatrix = new float[16];
	float[] mVMatrix = new float[16];
	float[] mMVPMatrix = new float[16];
	private float[] mTriMat = new float[16];
	float mPx = 0.f;
	float mPy = 0.f;
	float angle = 0.f;
	float angle2 = 0.f;
	long t2 = 0L;
	float destX = 0.f;
	float destY = 0.f;
	float dt = 0.f;
	long nbFrameElapsed = 0;
	long time1 = 0L;
	
	
	private GLText glText;
	protected Rect s1 = null;
	protected Rect s2 = null;
	protected Circle mCircle = null;
	protected Triangle mTriangle = null;
	protected Polygon mPoly = null;
	protected Sprite mTest = null;
	protected Layer l1 = null;
	protected Rect s3 = null;
	protected Shaders s_s3 = null;
	public ArrayList<PolyLine> s_line = new ArrayList<PolyLine>();
	
	protected float beginTime = (float)SystemClock.uptimeMillis();
	
	public GLES20Renderer(Context c) {
		super(c);
	}

	public void addLine(){
		s_line.add(new PolyLine());
		
		//s_line.get(s_line.size()-1).setTexture(R.drawable.blank);
		s_line.get(s_line.size()-1).setColor(1.f, 1.f, 0.f);
	}
	
	@Override
	public void onCreate(int width, int height, boolean contextLost) {
		// init 
		
		
		Sprite.setShadersInit(false);
		Log.v("tag","crea");
		/*s_line = new PolyLine();
		s_line.setTexture(R.drawable.blank);
		s_line.setColor(1.f, 1.f, 0.f);*/
		mTriangle = new Triangle();
		mCircle = new Circle(100.f,6);
		//mCircle.setTexture(R.drawable.blank);
		mPoly = new Polygon(200.f,120.f,new float[]{
				0.f,0.f,
				100.f,0.f,
				120.f,150.f,
				0.f,140.f} );
		//mPoly.setTexture(R.drawable.blank);
		mPoly.setColor(1.f, 0.f, 0.f);
		mTest = new Rect(100.f,100.f,250.f,250.f);
		s1 = new Rect(100.f,100.f,150.f,150.f);
		s3 = new Rect(0.f,0.f,800.f,480.f);
		
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearColor(0f, 0f, 0f, 1f);
		
		l1 = new Layer(800,480);
		s1.setTextureFitting(R.drawable.ic_launcher);
		
		//mTest.setTexture(R.drawable.ic_launcher);
		mTest.setTexture(R.drawable.tex1);
		
		s3.setTextureDirect(l1.getTexture());
		s_s3 = new Shaders();
		s_s3.init("shaders_texture.vert","sinwave.frag");
		//s_s3.init("shaders_texture.vert","sinwave.frag");
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        glText = new GLText(mContext.getAssets());
        glText.load( "ARBONNIE.ttf", 32, 2, 2 ); // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		computeFPS();
		long time2 = SystemClock.uptimeMillis();
		dt = 1.f * (time2 - time1);
		time1 = time2;
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		scene();
	}

	public void subScene() {
		float aaa = time1*0.01f;
		mTest.setRotation2(aaa);
		s1.setRotation2(aaa);
		mTest.draw(mMVPMatrix);
		s1.draw(mMVPMatrix);
		mCircle.draw(mMVPMatrix);
		
		// triangle direct matrix manip
		float[] scratch = new float[16];
		Matrix.setIdentityM(mTriMat, 0);
		mTriangle.draw(mMVPMatrix);
		Matrix.translateM(mTriMat, 0, 400.f, 100.f, 0.f);
		Matrix.rotateM(mTriMat, 0, aaa, 0.f, 0.f,1.f);
		Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mTriMat, 0);
		mTriangle.draw(scratch);
		
		// line
        for(int i=0;i<s_line.size();i++){
        	s_line.get(i).draw(mMVPMatrix);
        }
		
	}
	
	static final float lambda = 0.8f;
	int n_screenshot = 0;
	
	public void scene(){
		l1.use();
		subScene();
		
		l1.unuse();
		
		float[] scratch = new float[16];
		float[] model = new float[16];
		Matrix.setIdentityM(model, 0);
		Matrix.translateM(model, 0, destX, destY, 0.f);
		Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, model, 0);
		// time compute :
		/*float ttt = ((float) (time1)-beginTime)*0.001f;
		if(ttt>lambda)beginTime+=lambda;
		while(ttt>lambda)ttt-=lambda;*/
		float ttt = nbFrameElapsed * 0.01f;
		
		s_s3.set("uTime", ttt);
		s3.draw(scratch,s_s3);
		mPoly.draw(mMVPMatrix);
		//s3.draw(scratch);
		
		// text
		glText.begin( 1.0f, 1.0f, 1.0f, 1.0f, mMVPMatrix ); // Begin Text Rendering (Set Color WHITE)
		GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA); // active transparent bg
        glText.drawC("Test String !", 100.f, 200.f,0.f);
        glText.drawC("FPS :"+mFPS, 500.f, 380.f,0.f);
        glText.end();
        // line
        for(int i=0;i<s_line.size();i++){
        	s_line.get(i).draw(mMVPMatrix);
        }
        if(n_screenshot % 2 == 0)
        	captureScreenShot("/screenshot/", "screen.png" );
        n_screenshot++;
	}
	
	public void draw(Sprite s){
		s.draw(mMVPMatrix);
	}

	@Override
	/**
	 * Reset the viewport size to fit the new screen
	 */
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		mWidth = width;
		mHeight = height;
		GLES20.glViewport(0, 0, width, height);

		// float ratio = (float) width / height;
		/*final float left = -width / 2.0f;
		final float right = width / 2.0f;
		final float bottom = -height / 2.0f;
		final float top = height / 2.0f;
		final float near = 0.1f;
		final float far = 200.0f;*/

		// Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
		// Matrix.orthoM(mProjMatrix, 0, left, right, top, bottom, near, far);
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