package com.agl.example2;

import com.agl.graphics.Circle;

import android.content.Context;
import android.util.Log;

public class DemoGame extends Renderer {

	Circle player = null;
	
	DemoGame(Context c) {
		super(c);
	}
	
	@Override
	public void onCreate(int width, int height, boolean contextLost) {
		super.onCreate(width, height, contextLost);
		player = new Circle(50.f,100);
		player.setColor(0.0f, 1.0f, 0.0f);
		player.move(mWidth/2, mHeight/2); // center
		
		Log.v("tag","isok");
	}
	
	@Override
	public void scene(){
		player.draw(mMVPMatrix);
		//draw(player);
	}

	public void movePlayer(float dx, float dy) {
		player.move(dx, dy);
	}
	
	

}
