package com.agl.graphics;

import android.opengl.GLES20;
import android.util.Log;

/**
 * @author Sergu Class representing any polygon with an arbitrary list of points
 */
public class Polygon extends Sprite {
	int nbPoints = 0;

	/**
	 * Constructor of a polygon
	 * 
	 * @param posX position x
	 * @param posY position y
	 * @param points ist of points
	 */
	public Polygon(float posX, float posY, float[] points) {
		super(posX, posY);
		if (points.length % 2 != 0) {
			Log.v("opengl", "Error: attempt to create a polygon "
					+ "whith missing coordinates");
			return;
		}
		nbPoints = points.length / 2;
		mCoords = new float[3 * (nbPoints + 2)];
		float centerX = 0.f, centerY = 0.f;
		for (int i = 0; i < nbPoints; i++) {
			mCoords[3 * (i + 1)] = points[2 * i];
			mCoords[3 * (i + 1) + 1] = points[2 * i + 1];
			centerX += points[2 * i];
			centerY += points[2 * i + 1];
		}
		mCoords[0] = centerX / nbPoints;
		mCoords[1] = centerY / nbPoints;
		mCoords[3 * (nbPoints + 1)] = points[0];
		mCoords[3 * (nbPoints + 1) + 1] = points[1];
		// setTexture(R.drawable.blank);
		init();
	}

	/**
	 * Constructor of a polygon at coordinates (0,0)
	 * 
	 * @param points
	 */
	public Polygon(float[] points) {
		this(0.f, 0.f, points);
	}

	/**
	 * @Override
	 */
	protected void drawShape() {
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
	}

}
