package com.agl.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Environment;
import android.util.Log;

public abstract class GLRenderer implements Renderer {

    /*private boolean mFirstDraw;*/
    protected boolean mSurfaceCreated;
    protected int mWidth;
    protected int mHeight;
    protected long mLastTime;
    protected int mFPStemp = 0;
    protected int mFPS;
    protected int nbFrameElapsed = 0;
    protected static Context mContext;

    public GLRenderer() {
        /*mFirstDraw = true;*/
        mSurfaceCreated = false;
        mWidth = -1;
        mHeight = -1;
        mLastTime = System.currentTimeMillis();
        mFPS = 0;
    }
    
    public GLRenderer(Context c) {
        /*mFirstDraw = true;*/
        mSurfaceCreated = false;
        mWidth = -1;
        mHeight = -1;
        mLastTime = System.currentTimeMillis();
        mFPS = 0;
        mContext = c;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed,
            EGLConfig config) {
        /*if (Util.DEBUG) {
            Log.i(Util.LOG_TAG, "Surface created.");
        }
        mSurfaceCreated = true;
        mWidth = -1;
        mHeight = -1;*/
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width,
            int height) {
        /*if (!mSurfaceCreated && width == mWidth 
            && height == mHeight) {
            if (Util.DEBUG) {
                Log.i(Util.LOG_TAG,
                        "Surface changed but already handled.");
            }
            return;
        }
        if (Util.DEBUG) {
            // Android honeycomb has an option to keep the
            // context.
            String msg = "Surface changed width:" + width
                    + " height:" + height;
            if (mSurfaceCreated) {
                msg += " context lost.";
            } else {
                msg += ".";
            }
            Log.i(Util.LOG_TAG, msg);
        }*/

        mWidth = width;
        mHeight = height;

        onCreate(mWidth, mHeight, mSurfaceCreated);
        mSurfaceCreated = false;
    }

    /*@Override
    public void onDrawFrame(GL10 notUsed) {
        //onDrawFrame(mFirstDraw);

        /*if (Util.DEBUG) {
            mFPS++;
            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime >= 1000) {
                mFPS = 0;
                mLastTime = currentTime;
            }
        }

        if (mFirstDraw) {
            mFirstDraw = false;
        }
    }*/

    /*public int getFPS() {
        return mFPS;
    }*/
    
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

    public abstract void onCreate(int width, int height, boolean contextLost);
    
	private  File getOutputMediaFile(){
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
	    String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm_SS").format(new Date());
	    File mediaFile;
        //String mImageName = "IMG_"+ timeStamp +".jpg";
	    String mImageName = "IMG_"+ nbFrameElapsed +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);  
	    return mediaFile;
	} 

	public Bitmap captureScreenShot() {
		int width = mWidth; // use your favorite width
		int height = mHeight; // use your favorite height
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
			sBuffer[i] = ( short ) ( ( ( v & 0x1f ) << 11 ) | ( v & 0x7e0 ) | ( ( v & 0xf800 ) >> 11 ) );
		}
		sb.rewind();
		bitmap.copyPixelsFromBuffer( sb );
		return bitmap;
	}
	
	public void saveBitmap(Bitmap screenshot, String filePath,  String fileName) {
		/*OutputStream outStream = null;
		filePath = Environment.getExternalStorageDirectory().toString() + "/" + filePath;
		File dir = new File( filePath );
		dir.mkdirs();
		File output = new File( filePath, fileName );
		*/
		String TAG = "screenshot";
		File imgFile = getOutputMediaFile();
	    if (imgFile == null) {
	        Log.d(TAG,
	                "Error creating media file, check storage permissions: ");// e.getMessage());
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

	/** Require the context to be set */
	public void captureScreenShot(final String path, final String fileName) {
		Bitmap screenshot = captureScreenShot();
		saveBitmap(screenshot, path, fileName);
		screenshot.recycle();
	}
	
	static public Context getContext() {
		return mContext;
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

}