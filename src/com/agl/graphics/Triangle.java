package com.agl.graphics;

import android.opengl.GLES20;

/**
 * @author Sergu
 * Shape of a simple triangle 
 */
public class Triangle extends Sprite{

    /**
     * Constructor of a void triangle 
     * (use defaults coordinates)
     */
    public Triangle() {
    	setCoords(0,0, 100,0 ,0,100);
    }
    
    /**
     * Constructor of a void triangle 
     * (use defaults coordinates)
     */
    public Triangle(float ax, float ay, float bx, float by, float cx, float cy) {
    	setCoords(ax, ay, bx, by, cx, cy);
    }
    
	protected void setCoords(float ax, float ay, float bx, float by, float cx, float cy) {
		mCoords = new float[]{
		        ax, ay, 0.0f,
		        bx, by, 0.0f,
		        cx, cy, 0.0f
		   };
		init();
    }
	
	protected void drawShape() {
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
	}
	
}
