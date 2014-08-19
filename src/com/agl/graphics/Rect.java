package com.agl.graphics;

/**
 * @author Sergu
 *         A simple rectangle
 */
public class Rect extends Sprite {

	/**
	 * Constructor of a rectangle
	 * @param nx position x
	 * @param ny position y
	 * @param nw width
	 * @param nh height
	 */
	public Rect(float nx, float ny, float nw, float nh) {
		x = nx;
		y = ny;
		w = nw;
		h = nh;
		setCoords();
		init();
	}

	/**
	 * Constructor of a rectangle with a null size
	 * @param nx position x
	 * @param ny position y
	 */
	public Rect(float nx, float ny) {
		x = nx;
		y = ny;
		w = 0;
		h = 0;
		setCoords();
		init();
	}

	/**
	 * Constructor of a rectangle with a null size at coordinate (0,0)
	 */
	public Rect() {
		x = 0.f;
		y = 0.f;
		w = 0;
		h = 0;
		setCoords();
		init();
	}

	protected void setCoords() {
		mCoords = new float[] { 0, 0, 0.f,
								w, 0, 0.f,
								0, h, 0.f,
								w, h, 0.f };
	}

}
