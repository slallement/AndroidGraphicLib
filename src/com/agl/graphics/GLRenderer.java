package com.agl.graphics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;
/**
 * @author Sergu
 * Test application
 */
public abstract class GLRenderer implements Renderer {
    protected boolean mSurfaceCreated;
	protected static Context mContext;
    protected int mWidth;
    protected int mHeight;
    
    // fps module
    long mLastTime;
    int mFPStemp = 0;
    protected int mFPS;
    protected long nbFrameElapsed = 0;
    
    // camera matrix
    protected float[] mProjMatrix = new float[16];
    protected float[] mVMatrix = new float[16];
    protected float[] mMVPMatrix = new float[16];

	long t1 = 0;
	protected double dt = 0.0;
	
	public GLRenderer() {
		mSurfaceCreated = false;
        mWidth = -1;
        mHeight = -1;
        mFPS = 0;
	}
	
	public GLRenderer(Context c) {
		mSurfaceCreated = false;
		mContext = c;
        mWidth = -1;
        mHeight = -1;
        mFPS = 0;
	}
	
	//@Override
	public void onCreate(int width, int height, boolean contextLost) {
		Sprite.setShadersInit(false);
		PolyLine.setShadersInit2(false);
		PolyLine.setShaders2();
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearColor(0f, 0f, 0f, 1f);
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		computeFPS();
		long t2 = System.currentTimeMillis();
		dt = (t2-t1)/1000.0;
		t1 = t2;
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		scene();
	}

	public abstract void scene();
	
	public void draw(Sprite s){
		s.draw(mMVPMatrix);
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
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
	
	static public String loadDataFromAsset(final String path) {
        // load text
        try {
            // get input stream for text
            InputStream is = mContext.getAssets().open(path); // context static
            // check size
            int size = is.available();
            // create buffer for IO
            byte[] buffer = new byte[size];
            // get data to buffer
            is.read(buffer);
            // close stream
            is.close();
            // set result to TextView
            return new String(buffer);
        }catch(IOException ex) {
            return "";
        }
    }
	
	public void computeFPS(){
        mFPStemp++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastTime >= 1000) {
            mFPS = mFPStemp;
            mFPStemp = 0;
            mLastTime = currentTime;
        }
        ++nbFrameElapsed;
	}
	
	// SCREENSHOT MODULE ------------------------------------------------------
	static public Bitmap captureScreenShot(int w, int h) {
		int width = w; // use your favorite width
		int height = h; // use your favorite height
		int screenshotSize = width * height;
		ByteBuffer bb = ByteBuffer.allocateDirect( screenshotSize * 4 );
		bb.order( ByteOrder.nativeOrder() );
		// any opengl context will do
		GLES20.glReadPixels( 0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bb );
		int pixelsBuffer[] = new int[screenshotSize];
		bb.asIntBuffer().get( pixelsBuffer );
		bb = null;
		Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.RGB_565 );
		bitmap.setPixels( pixelsBuffer, screenshotSize - width, -width, 0, 0, width, height );
		pixelsBuffer = null;

		short sBuffer[] = new short[screenshotSize];
		ShortBuffer sb = ShortBuffer.wrap( sBuffer );
		bitmap.copyPixelsToBuffer( sb );

		//Making created bitmap (from OpenGL points) compatible with Android bitmap
		for ( int i = 0; i < screenshotSize; ++i ) {
			short v = sBuffer[i];
			sBuffer[i] = ( short ) ( ( ( v & 0x1f ) << 11 ) 
					| ( v & 0x7e0 ) | ( ( v & 0xf800 ) >> 11 ) );
		}
		sb.rewind();
		bitmap.copyPixelsFromBuffer( sb );
		return bitmap;
	}
	
	static private File getOutputMediaFile(String file_name){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this. 
	    File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
	            + "/Android/data/"
	            + mContext.getPackageName()
	            + "/Files"); 

	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    }
	    // Create a media file name
	    //String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm_SS").format(new Date());
        //String mImageName = "IMG_"+ timeStamp +".jpg";
	    //String mImageName = "IMG_"+ id_image +".jpg";
	    File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + file_name);  
	    return mediaFile;
	} 

	// use: captureScreenShot(mWidth, mHeight)
	
	
	static public void saveBitmap(Bitmap screenshot, String filePath, String fileName) {
		/*OutputStream outStream = null;
		filePath = Environment.getExternalStorageDirectory().toString() + "/" + filePath;
		File dir = new File( filePath );
		dir.mkdirs();
		File output = new File( filePath, fileName );
		*/
		String TAG = "screenshot";
		File imgFile = getOutputMediaFile(fileName);
	    if (imgFile == null) {
	        Log.d(TAG, "Error creating media file, check storage permissions: ");// e.getMessage());
	        return;
	    } 
	    try {
	        FileOutputStream fos = new FileOutputStream(imgFile);
	        screenshot.compress(Bitmap.CompressFormat.PNG, 90, fos);
	        fos.close();
	        //Log.d(TAG, "screenshot ok");
	    } catch (FileNotFoundException e) {
	        Log.d(TAG, "File not found: " + e.getMessage());
	    } catch (IOException e) {
	        Log.d(TAG, "Error accessing file: " + e.getMessage());
	    }  
	}
	
	public void captureScreenShot(final String path, final String fileName) {
		Bitmap screenshot = captureScreenShot(mWidth,mHeight);
		saveBitmap(screenshot, path, "IMG_"+nbFrameElapsed);
		screenshot.recycle();
	}
	// end of screenshot module ------------------------------------------------

	public static Context getContext() {
		return mContext;
	}
}
