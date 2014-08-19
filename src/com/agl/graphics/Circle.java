package com.agl.graphics;

import android.opengl.GLES20;


/**
 * @author Sergu
 * Used to draw circles or regular polygons
 */
public class Circle extends Sprite {
    protected int precision;
    
    /** Constructor
     * @param radius
     * @param precision number of edges
     */
    public Circle(float radius, int precision)
	{
    	x = 0.f;
    	y = 0.f;
    	w = 2.f*radius;
    	h = radius;
    	if(precision<=0) precision = 8;
		this.precision = precision;
    	setCoords();
    	init();
    	color[1] =0.f;
    	color[0] =1.f;
	}

    /**
     * @param nx center x axis
     * @param ny center y axis
     * @param radius
     * @param precision number of edges
     */
    public Circle(float nx, float ny, float radius, int precision)
	{
    	x = nx;
    	y = ny;
    	w = 2.f*radius;
    	h = radius;
    	if(precision<=0) precision = 8;
		this.precision = precision;
    	setCoords();
    	init();
	}
	
	/** @Override */
	protected void setCoords(){
    	mCoords = new float[3*(precision+2)];
		float part = 2.f*3.1416f/(float)(precision);
		mCoords[0] = x+h;
		mCoords[1] = y+h;
		mCoords[2] = 0.f;
		
		for(int i=1;i<=precision+1;i++){
			mCoords[3*i+0] = x+h+h*(float) (Math.cos((i-1)*part));
			mCoords[3*i+1] = y+h+1.5f*h*(float) (Math.sin((i-1)*part));
			mCoords[3*i+2] = 0.f;
		}
	}
	
	/** @Override */
	protected void drawShape(){
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
	}
	
}
