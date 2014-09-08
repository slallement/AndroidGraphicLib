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

import com.agl.graphics.GLRenderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Environment;
import android.util.Log;

public abstract class GLRenderer1 implements Renderer {

    /*private boolean mFirstDraw;*/
    protected boolean mSurfaceCreated;
    protected int mWidth;
    protected int mHeight;
    protected long mLastTime;
    protected int mFPStemp = 0;
    protected int mFPS;
    protected long nbFrameElapsed = 0;
    protected static Context mContext;

    public GLRenderer1() {
        /*mFirstDraw = true;*/
        mSurfaceCreated = false;
        mWidth = -1;
        mHeight = -1;
        mLastTime = System.currentTimeMillis();
        mFPS = 0;
    }
    
    public GLRenderer1(Context c) {
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
    
    // getOutputMediaFile(nbFrameElapsed)


	/** Require the context to be set */
	public void captureScreenShot(final String path, final String fileName) {
		Bitmap screenshot = GLRenderer.captureScreenShot(mWidth,mHeight);
		GLRenderer.saveBitmap(screenshot, path, "IMG_"+nbFrameElapsed);
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