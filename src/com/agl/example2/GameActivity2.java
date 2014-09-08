package com.agl.example2;

import com.agl.graphics.PolyLine;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity2 extends Activity {
	private TextView info = null;
	private GLSurfaceView mSurfaceView = null;
	private DemoGame game = null;
	float lastx = 0.0f;
	float lasty = 0.0f;

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
			game = new DemoGame(this.getApplicationContext());
			mSurfaceView.setRenderer(game);
			setContentView(mSurfaceView);
		} else {
			// OpenGL ES 2.0 not  supported.
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
		if (mSurfaceView != null) {
			mSurfaceView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mSurfaceView != null) {
			mSurfaceView.onPause();
		}
	}
	float mPreviousX;
	float mPreviousY;
	
	protected int nbPointer = 0;
	private SparseIntArray mActivePointers = new SparseIntArray();

	@Override
    public boolean onTouchEvent(MotionEvent e) {

	    int pointerIndex = e.getActionIndex();
	    int pointerId = e.getPointerId(pointerIndex);  // get pointer ID
	    int maskedAction = e.getActionMasked();
	    switch (maskedAction) {
		    case MotionEvent.ACTION_DOWN:
		    case MotionEvent.ACTION_POINTER_DOWN: {
		    	nbPointer++;
		    	mActivePointers.put(pointerId,0);
		    	final float x = e.getX(nbPointer-1), y = mSurfaceView.getHeight()-e.getY(nbPointer-1);
		    	lastx = x;
	    		lasty = y;
		    	break;
		    }
		    case MotionEvent.ACTION_MOVE: { // a pointer was moved
		    	for (int size = e.getPointerCount(), i = 0; i < size; i++) {
		    		int id = e.getPointerId(i);
		    		int pos = mActivePointers.get(e.getPointerId(i));
		    		final float x = e.getX(i), y = mSurfaceView.getHeight()-e.getY(i);
		    		float dx = x-lastx, dy = y-lasty;
		    		lastx = x;
		    		lasty = y;
		    		game.movePlayer(dx, dy);
		    	}
		    	break;
		    }
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		    case MotionEvent.ACTION_CANCEL: {
		    	nbPointer--;
		    	mActivePointers.removeAt(pointerId);
		    	break;
		    }
	    }
	    
        return true;
    }
	
}

