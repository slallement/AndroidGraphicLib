package com.agl.graphics;

import android.opengl.GLES20;

/**
 * @author Sergu
 * Polyline
 */
public class PolyLine extends Sprite {
	
	/** Constructor of a void PolyLine
	 */
	public PolyLine()
	{
    	super(0.f,0.f);
        init();
	}
	
	/**
	 *  Constructor of PolyLine with a list of 2D points
	 * @param points
	 */
	public PolyLine(float ... points)
	{
		super(0.f,0.f);
		init();
		if(points.length % 2 !=0)
			return;
        mCoords = new float[points.length/2*3];
        for(int i=0;i<points.length/2;++i){
        	mCoords[3*i] = points[2*i];
        	mCoords[3*i+1] = points[2*i+1];
        	mCoords[3*i+2] = 0; // z axis
        }
	}
	
	/*
	public PolyLine(float posX, float posY)
	{
    	super(posX,posY);
        // texture buffer
        ByteBuffer tcbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tcbb.order(ByteOrder.nativeOrder());
        texCoordBuffer = tcbb.asFloatBuffer();
        texCoordBuffer.put(textureCoords);
        texCoordBuffer.position(0);
	}*/
	
	/*protected void init(){
		Matrix.setIdentityM(trans, 0);
    	Matrix.translateM(trans, 0, x, y, 0);
    	transformed = false;
        
        // texture buffer
        ByteBuffer tcbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tcbb.order(ByteOrder.nativeOrder());
        texCoordBuffer = tcbb.asFloatBuffer();
        texCoordBuffer.put(textureCoords);
        texCoordBuffer.position(0);
        
        setDefaultShaders();
    	setTextureDirect(idNoTexture);
	}*/
	
	protected void initVertexBuffer(){}
	
	/** Add a point to the polyline
	 * @param posX x coordinate
	 * @param posY y coordinate
	 */
	public void addPoint(float posX, float posY){
		try{
			if(mCoords == null){
				mCoords = new float[]{posX,posY,0.f};
				updateVertexBuffer();
				return;
			}
			float t[] = new float[mCoords.length+3];
			int pos = mCoords.length;
			t[pos] = posX;
			t[pos+1] = posY;
			t[pos+2] = 0;
			for(int i=0;i<mCoords.length;i++){
				t[i] = mCoords[i];
			}
			mCoords = t;
			updateVertexBuffer();
		} catch (Exception e) {

		}
	}
	
	/** erase the last point */
	public void eraseLastPoint(){
		if(mCoords.length < 3){
			return;
		}
		float t[] = new float[mCoords.length-3];
		for(int i=0;i<t.length;i++){
			t[i] = mCoords[i];
		}
		mCoords = t;
		updateVertexBuffer();
	}
	
	/**  Draw the shape */
	protected void drawShape(){
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount);
	}
	
	/**
	 * @override
	 */
    public void draw(float[] mvpMatrix) {
    	if(vertexBuffer == null)
    		return;
    	super.draw(mvpMatrix);
    }

}
