package com.agl.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * @author Sergu
 * Polyline
 */
public class PolyLine extends Sprite {
	public enum ColorMode {LOOP,REPEAT_FIRST,REPEAT_FIRST_INVERSE};
	float thickness = 1.f;
	protected float[] points; // list of 2d points
	protected int pointsCount = 0;

	float mColors[]; // colors to read
	ColorMode colorMode = ColorMode.REPEAT_FIRST_INVERSE;
	
	// opengl adds:
	private static boolean shadersInit2 = false;
	protected static int mProgram2 = 0;
	protected FloatBuffer colorBuffer;
	protected int mColorHandle;
	protected float mColorBuf[];
	

	/** Constructor of a void PolyLine
	 */
	public PolyLine()
	{
    	super(0.f,0.f);
    	points = new float[0];
    	setCoords();
        init();
	}
	
	/**
	 *  Constructor of PolyLine with a list of 2D points
	 * @param points
	 */
	public PolyLine(float ... points)
	{
		super(0.f,0.f);
		if(points == null) points = new float[0];
		setCoords(points);
		init();
	}
	
	/**
	 *  Constructor of PolyLine with a list of 2D points
	 * @param points
	 */
	public PolyLine(float nthickness, float[] points)
	{
		super(0.f, 0.f);
		thickness = nthickness;
		setCoords(points);
		init();
	}
	
	@Override
	protected void init(){
		Matrix.setIdentityM(trans, 0);
		Matrix.translateM(trans, 0, x, y, 0);
		transformed = false;

		initVertexBuffer();
		initTexture();
		setDefaultShaders();
		setTextureDirect(idNoTexture);
		
		setShaders2();
		initColorBuffer();
	}
	
	void setCoords(float ... npoints){
		if(npoints.length % 2 != 0)
			return;
		
		points = npoints.clone();
        pointsCount = npoints.length;
		
        setCoords();
	}
	
	protected void reallocatePoints(int newSize){
		float temp[] = new float[newSize];
		final int size = Math.min(newSize, points.length);
		for(int i=0;i<size;++i){
			temp[i] = points[i];
		}
		points = temp;
	}
	
	void setCoords() {
		// 2 points minimum
		if(pointsCount < 4) {
			mCoords = new float[0];
			return;
		}
        mCoords = new float[pointsCount*3];

        Vector2f d;
        d = new Vector2f(points[0], points[1]).sub(new Vector2f(points[2], points[3])).getNormal();
        d.normalize();
        d.mul(thickness);
        mCoords[0] = points[0]-d.x;
    	mCoords[1] = points[1]-d.y;
    	mCoords[2] = 0; // z axis
        mCoords[3] = points[0]+d.x;
    	mCoords[4] = points[1]+d.y;
    	mCoords[5] = 0; // z axis
        
        for(int i=1; i<=pointsCount/2-2; ++i){
        	// points[2*i]
        	
        	Vector2f v0 = new Vector2f(points[2*i-2], points[2*i-1]);
        	Vector2f v1 = new Vector2f(points[2*i  ], points[2*i+1]);
        	Vector2f v2 = new Vector2f(points[2*i+2], points[2*i+3]);
        	
        	v0.set(v1.sub(v0));
        	v0.normalize();
        	v2.set(v2.sub(v1));
        	v2.normalize();
        	d = v0.add(v2).getNormal();
        	float dd = thickness/2.f/Math.max(0.8f,d.dot(v0));
        	d.mul(dd);
        	
        	// point 1
        	mCoords[6*i] = v1.x+d.x;
        	mCoords[6*i+1] = v1.y+d.y;
        	mCoords[6*i+2] = 0; // z axis
        	
        	// point2
        	mCoords[6*i+3] = v1.x-d.x;
        	mCoords[6*i+4] = v1.y-d.y;
        	mCoords[6*i+5] = 0; // z axis
        }
        
        d = new Vector2f(points[pointsCount-4], points[pointsCount-3])
        		.sub(new Vector2f(points[pointsCount-2], points[pointsCount-1])).getNormal();
        d.normalize();
        d.mul(thickness/1.f);
        mCoords[pointsCount*3-6] = points[pointsCount-2]-d.x;
    	mCoords[pointsCount*3-5] = points[pointsCount-1]-d.y;
    	mCoords[pointsCount*3-4] = 0; // z axis
        mCoords[pointsCount*3-3] = points[pointsCount-2]+d.x;
    	mCoords[pointsCount*3-2] = points[pointsCount-1]+d.y;
    	mCoords[pointsCount*3-1] = 0; // z axis
	}
	
	//protected void initVertexBuffer(){}
	
	/** Add a point to the polyline
	 * @param posX x coordinate
	 * @param posY y coordinate
	 */
	public void addPoint(float posX, float posY){
		//try{
			if(pointsCount >= 2 && points[pointsCount-2] == posX 
							   && points[pointsCount-1] == posY ){
				return;
			}
			if(points.length < pointsCount+2){
				reallocatePoints(pointsCount+2+512);
			}
			points[pointsCount] = posX;
			points[pointsCount+1] = posY;
			pointsCount += 2;
			setCoords();
			
			initVertexBuffer();
			//initTexture();
			initColorBuffer();
		/*} catch (Exception e) {

		}*/
	}
	
	public int getSize() {
		return pointsCount;
	}
	
	/** erase the last point */
	public void eraseLastPoint(){
		if(mCoords.length < 3){
			return;
		}
		pointsCount -= 2;
		if(pointsCount > 0){
			setCoords();
		}
	}
	
	/**  Draw the shape */
	/*protected void drawShape(){
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
	}*/
	
	static public void setShaders2() {
		setDefaultShaders();
		if(!shadersInit2){
			mProgram2 = GLES20.glCreateProgram();// create empty OpenGL Program
			int vertexShader = MUtils.loadShader(GLES20.GL_VERTEX_SHADER,
					GLRenderer.loadDataFromAsset("shaders_texture2.vert"));
			int fragmentShader = MUtils.loadShader(GLES20.GL_FRAGMENT_SHADER,
					GLRenderer.loadDataFromAsset("shaders_texture2.frag"));

			GLES20.glAttachShader(mProgram2, vertexShader);
			GLES20.glAttachShader(mProgram2, fragmentShader);
			GLES20.glLinkProgram(mProgram2);
			shadersInit2 = true;
		}
	}
	
	public static void setShadersInit2(boolean shadersInit) {
		shadersInit2 = shadersInit;
	}
	
	protected void initColorBuffer() {
		if(mCoords.length == 0)
			return;

		
		mColorBuf = new float[mCoords.length / COORDS_PER_VERTEX * 4];
		for(int i=0;i<mColorBuf.length;++i){
			mColorBuf[i] = 1.f;
		}
		if(mColors != null){
			if(colorMode == ColorMode.LOOP){
				for(int i=0;i<mColorBuf.length/8;++i){
					int id = (i)%(mColors.length/4);
					for(int j=0;j<4;++j){
						mColorBuf[8*i+j] = mColors[4*id+j];
						mColorBuf[4+8*i+j] = mColors[4*id+j];
					}
				}
			}else if(colorMode == ColorMode.REPEAT_FIRST){
				for(int i=0;i<mColorBuf.length/4-(mColors.length/4);++i){
					for(int j=0;j<4;++j){
						mColorBuf[4*i+j] = mColors[j];
					}
				}
				int offset = mColorBuf.length-(mColors.length%mColorBuf.length)-8;
				if(offset < 0) offset = 0; // security ?
				for(int i = 0; i<mColors.length/4; ++i){
					for(int j=0;j<4;++j){
						mColorBuf[offset+8*i+j] = mColors[4*i+j];
						mColorBuf[offset+4+8*i+j] = mColors[4*i+j];
					}
				}
			} else if(colorMode == ColorMode.REPEAT_FIRST_INVERSE){
				for(int i=mColors.length/4;i<mColorBuf.length/4;++i){
					for(int j=0;j<4;++j){
						mColorBuf[4*i+j] = mColors[j];
					}
				}
				
				for(int i = 0; i<mColors.length/4; ++i){
					for(int j=0;j<4;++j){
						mColorBuf[8*i+j] = mColors[mColors.length-4-4*i+j];
						mColorBuf[4+8*i+j] = mColors[mColors.length-4-4*i+j];
					}
				}
			}
		}
		//---
		ByteBuffer bb = ByteBuffer.allocateDirect(mColorBuf.length * 4);
		bb.order(ByteOrder.nativeOrder()); 
		colorBuffer = bb.asFloatBuffer();
		colorBuffer.put(mColorBuf);
		colorBuffer.position(0);
	}
	

    
    /**
	 * Draw the shape with a certain transformation matrix and a specific shader
	 * 
	 * @param mvpMatrix transformation matrix
	 */
	public void draw(float[] mvpMatrix) {
		if(vertexBuffer == null || mCoords.length <= 0){
    		return;
    	}
		// Add program to OpenGL ES environment
		GLES20.glUseProgram(mProgram2);

		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram2, "aPosition");
		GLES20.glEnableVertexAttribArray(mPositionHandle);// Enable a handle to the triangle vertices

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		mTexCoordHandle = GLES20.glGetAttribLocation(mProgram2, "aTexCoord");
		int mTexHandle = GLES20.glGetUniformLocation(mProgram2, "utexture");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexDataHandle);
		GLES20.glUniform1i(mTexHandle, 0);

		GLES20.glEnableVertexAttribArray(mTexCoordHandle);
		GLES20.glVertexAttribPointer(mTexCoordHandle, TEX_COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, 0, texCoordBuffer);

		// COLOR :
		mColorHandle = GLES20.glGetAttribLocation(mProgram2, "aColor");
		GLES20.glEnableVertexAttribArray(mColorHandle);
		GLES20.glVertexAttribPointer(mColorHandle, 4,
				GLES20.GL_FLOAT, false, 0, colorBuffer);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram2, "uMVPMatrix");

		// Apply the projection and view transformation
		float res[] = new float[16];
		Matrix.setIdentityM(res, 0);
		Matrix.multiplyMM(res, 0, mvpMatrix, 0, trans, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, res, 0);

		//drawShape();
		// Draw the triangle
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
		//GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mColorHandle);
		GLES20.glDisableVertexAttribArray(mTexCoordHandle);
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		

		GLES20.glDisable(GLES20.GL_BLEND);
	}

	public void setThickness(float nthickness){
		thickness = nthickness;
	}
	
	public void setColor(float[] ncolor){
		for(int i=0;i<ncolor.length/3;++i){
			mColors[4*i+0] = ncolor[3*i+0];
			mColors[4*i+1] = ncolor[3*i+1];
			mColors[4*i+2] = ncolor[3*i+2];
			mColors[4*i+3] = 1.0f; // alpha
		}
	}
	
	public void setColorRGBA(float[] ncolor){
		mColors = ncolor.clone();
	}
}
