package com.agl.example;

import com.agl.graphics.PolyLine;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity extends Activity {
	private TextView info = null;
	private GLSurfaceView mSurfaceView = null;
	private GLES20Renderer mRenderer = null;

	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * info = new TextView(this);
	 * info.setText("Ok");
	 * setContentView(info);
	 * }
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (hasGLES20()) {
			mSurfaceView = new GLSurfaceView(this);
			mSurfaceView.setEGLContextClientVersion(2);
			//mSurfaceView.setPreserveEGLContextOnPause(true);
			mRenderer = new GLES20Renderer(this.getApplicationContext());
			mSurfaceView.setRenderer(mRenderer);
			setContentView(mSurfaceView);
		} else {
			// Time to get a new phone, OpenGL ES 2.0 not
			// supported.
			info = new TextView(this);
			info.setText("OpenGL ES 2.0 not supported ... ");
			setContentView(info);
		}
		
	}

	private boolean hasGLES20() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		return info.reqGlEsVersion >= 0x20000;
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * The activity must call the GL surface view's onResume() on activity
		 * onResume().
		 */
		if (mSurfaceView != null) {
			mSurfaceView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		/*
		 * The activity must call the GL surface view's onPause() on activity
		 * onPause().
		 */
		if (mSurfaceView != null) {
			mSurfaceView.onPause();
		}
	}
	float mPreviousX;
	float mPreviousY;
	
	protected int nbPointer = 0;
	private SparseArray<Integer> mActivePointers = new SparseArray<Integer>();

	@Override
    public boolean onTouchEvent(MotionEvent e) {
		/*
        float x = e.getX();
        float y = e.getY();*/
        /*
        if( e.getAction() == MotionEvent.ACTION_MOVE){
        	//mRenderer.destX = x- mSurfaceView.getWidth() / 2;
        	//mRenderer.destY = -y+ mSurfaceView.getHeight() / 2;
        	//Log.v("tag","v:"+mRenderer.destX+"|"+mRenderer.destY);
	        float dx = x - mPreviousX;
	        float dy = y - mPreviousY;
        	mRenderer.destX += dx;
        	mRenderer.destY -= dy;
        	if(dx*dx+dy*dy > 0.01f){
        		float realY =  -y+2.f* mSurfaceView.getHeight() / 2;
        		if(mRenderer.s_line.size() > 0){
        		mRenderer.s_line.get(mRenderer.s_line.size()-1).addPoint(x,realY);
        		}
        	}
        }else if( e.getAction() == MotionEvent.ACTION_DOWN){
        	mRenderer.addLine();
        }*/
		
		
        /*if(e.getAction() == MotionEvent.ACTION_MOVE){
	        mRenderer.mPx = x- mSurfaceView.getWidth() / 2;
	        mRenderer.mPy = y- mSurfaceView.getHeight() / 2;
        }*/
		/*
        mPreviousX = x;
        mPreviousY = y;*/
		
	    int pointerIndex = e.getActionIndex();
	    int pointerId = e.getPointerId(pointerIndex);  // get pointer ID
	    int maskedAction = e.getActionMasked();
	    switch (maskedAction) {
		    case MotionEvent.ACTION_DOWN:
		    case MotionEvent.ACTION_POINTER_DOWN: {
		    	mRenderer.addLine();
		    	nbPointer++;
		    	mRenderer.s_line.get(mRenderer.s_line.size()-1).setColor(nbPointer*1.f/e.getPointerCount(), 1.f, 0.f);
		    	mActivePointers.put(pointerId, mRenderer.s_line.size()-1);
		    	// mActive pointer has the right position in the list associed to the right id of a finger
		    	break;
		    }
		    case MotionEvent.ACTION_MOVE: { // a pointer was moved
		    	for (int size = e.getPointerCount(), i = 0; i < size; i++) {
		    		int id = e.getPointerId(i);
		    		int pos = mActivePointers.get(e.getPointerId(i));
		    		PolyLine line = mRenderer.s_line.get(pos);
		    		if (line != null) {
		    			
		    			line.addPoint(e.getX(i), mSurfaceView.getHeight()-e.getY(i));
		    		}
		    	}
		    	break;
		    }
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		    case MotionEvent.ACTION_CANCEL: {
		    	nbPointer--;
		    	mActivePointers.remove(pointerId);
		    	break;
		    }
	    }
	    
        return true;
    }
	
}
