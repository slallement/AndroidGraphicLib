package com.agl.example4;

import java.util.ArrayList;

import android.content.Context;
import android.os.SystemClock;

import com.agl.example4.PenroseTiles.PenTriangle;
import com.agl.graphics.GLRenderer;
import com.agl.graphics.Layer;
import com.agl.graphics.Rect;
import com.agl.graphics.Sprite;
import com.agl.graphics.Triangle;
import com.agl.graphics.Vector2f;

public class Demo4 extends GLRenderer{
	final float pen_size = Math.min(mWidth, mHeight)/2.f;
	Triangle[] tiles;
	Layer layer1;
	Sprite layer1_draw;
	
	public Demo4 (Context c){
		super(c);
	}
	
	void make_penrose() {
		ArrayList<PenTriangle> li = new ArrayList<PenTriangle>();
		Vector2f A = new Vector2f(pen_size,pen_size);
		Vector2f B = new Vector2f();
		Vector2f C = new Vector2f();
		final float ratio = 2.f*3.1416f/10.f;
		final float rad = pen_size;
		for(int i=0;i<10;++i) {
			B.setPolar(rad, (float)i*ratio);
			C.setPolar(rad, ((float)i+1.f)*ratio);
			if(i%2 ==0){
				Vector2f T = B.copy(); B = C; C = T;
			}
			B.translate(A);
			C.translate(A);
			li.add(new PenTriangle(0, A,B,C));
		}
		if(tiles == null)
			tiles = (new PenroseTiles(li,5)).getTriangles();
	}
	
	@Override
	public void onCreate(int width, int height, boolean contextLost) {
		super.onCreate(width, height, contextLost);
		
		make_penrose();
		
		layer1 = new Layer((int)pen_size*2, (int)pen_size*2);
		layer1_draw = new Rect(0.f,0.f,(int)pen_size*2, (int)pen_size*2);
		layer1_draw.setTextureDirect(layer1.getTexture());
		layer1.use();
		draw(tiles);
		layer1.unuse();
		layer1_draw.move(-pen_size,-pen_size);
		layer1_draw.move(mWidth/2.f, mHeight/2.f);
	}
	
	@Override
	public void scene() {
		layer1_draw.setRotation2((float)SystemClock.uptimeMillis()*0.01f*3.1416f*2.f);
		draw(layer1_draw);
	}

}
