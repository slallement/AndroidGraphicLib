package com.agl.graphics;

/**
 * class for representing 2d vectors
 * @author Serguei Lallement
 *
 */
public class Vector2f {
	public float x,y;
	
	public Vector2f(float nx, float ny) {
		x = nx;
		y = ny;
	}
	
	public Vector2f() {
		x = 0f;
		y = 0f;
	}

	public void set(float nx, float ny){
		x = nx;
		y = ny;
	}
	
	public void set(Vector2f v){
		x = v.x;
		y = v.y;
	}
	
	public Vector2f add(Vector2f v){
		return new Vector2f(x+v.x, y+v.y);
	}
	
	public void mul(float scale){
		x *= scale;
		y *= scale;
	}
	
	public void mul(float scalex, float scaley){
		x *= scalex;
		y *= scaley;
	}
	
	public Vector2f sub(Vector2f v){
		return new Vector2f(x-v.x, y-v.y);
	}
	
	public void normalize() {
		float t = (float) Math.sqrt(x*x+y*y);
		if(t == 0.f)
			x = y = 0f;
		x /= t;
		y /= t;
	}
	
	public float dot(Vector2f v2) {
		return x*v2.x+y*v2.y;
	}
	
	Vector2f getNormal() {
		return new Vector2f(-y,x);
	}
	
	Vector2f copy() {
		return new Vector2f(x,y);
	}
	
	
}