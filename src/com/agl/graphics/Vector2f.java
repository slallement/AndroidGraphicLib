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
	
	public void translate(float vx, float vy){
		x += vx;
		y += vy;
	}
	
	public void translate(Vector2f v){
		x += v.x;
		y += v.y;
	}
	
	public Vector2f add(Vector2f v){
		return new Vector2f(x+v.x, y+v.y);
	}
	
	public Vector2f mul(float scale){
		return new Vector2f(x *scale, y * scale);
	}
	
	public Vector2f mul(float scalex, float scaley){
		return new Vector2f(x * scalex, y * scaley);
	}
	public void scale(float scale){
		x *= scale;
		y *= scale;
	}
	
	public void scale(float scalex, float scaley){
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
	
	public void setPolar(float r, float angle){
		x = r*(float)Math.cos((double)angle);
		y = r*(float)Math.sin((double)angle);
	}
	
	public float dot(Vector2f v2) {
		return x*v2.x+y*v2.y;
	}
	
	public Vector2f getNormal() {
		return new Vector2f(-y,x);
	}
	
	public Vector2f copy() {
		return new Vector2f(x,y);
	}
	
	static public Vector2f lerp(Vector2f v1, Vector2f v2, float t){
		return new Vector2f((1-t)*v1.x + t*v2.x,
							(1-t)*v1.y + t*v2.y);
	}
	
	static public float lerp(float v1, float v2, float t) {
		  return (1-t)*v1 + t*v2;
	}
	
	static public Vector2f lerp(float t, Vector2f ... v){
		int i = ((int)Math.floor(t))%(v.length-1);
		int j = i+1;
		if(i<0){
			i += (v.length-1);
			j += (v.length-1);
		}
		if(j > v.length-1){
			i = 0; j = 1;
		}
		t = t-(float)Math.floor(t);
		return new Vector2f((1.f-t)*v[i].x + t*v[j].x,
							(1.f-t)*v[i].y + t*v[j].y);
	}
	
}