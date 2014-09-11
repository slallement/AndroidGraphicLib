package com.agl.example3;

import android.content.Context;
import com.agl.graphics.GLRenderer;
import com.agl.graphics.MUtils;
import com.agl.graphics.Trail;
import com.agl.graphics.Vector2f;

public class Demo3 extends GLRenderer {
	Trail trail1, trail2, trail3;
	float[] colors;
	double t = 0.0;
	Trail[] curve_border;
	Vector2f[] curve_border_pos;
	float curve_border_radius;
	
	
	public Demo3(Context c){
		super(c);
		colors = new float[]{
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.2f, 0.0f, 0.0f,
				1.0f, 0.5f, 0.0f, 0.8f,
				1.0f, 1.0f, 0.0f, 0.5f,
				1.0f, 0.0f, 0.0f, 0.f};
		
	}
	
	@Override
	public void onCreate(int width, int height, boolean contextLost) {
		super.onCreate(width, height, contextLost);
		trail1 = new Trail();
		trail1.setThickness(1.f);
		trail1.setLength(100);
		trail1.setColor(colors);
		
		trail2 = new Trail();
		trail2.setThickness(1.f);
		trail2.setLength(100);
		trail2.setColor(colors);
		
		trail3 = new Trail();
		trail3.setThickness(1.f);
		trail3.setLength(100);
		trail3.setColor(colors);
		
		trail1.move(50.f, 100.f);
		trail2.move(250.f, 100.f);
		trail3.move(500.f, 100.f);
		
		final int count = 5;
		curve_border = new Trail[count];
		curve_border_pos = new Vector2f[curve_border.length];
		curve_border_radius = mWidth/(2*curve_border.length);
		for(int i=0;i<curve_border.length;++i){
			curve_border[i] = new Trail();
			curve_border_pos[i] = new Vector2f();
			float[] c = MUtils.hsvToRgb(0.f, 0.0f, 0.5f);
			curve_border[i].setColor(new float[]{c[0],c[1],c[2],1f});
			curve_border[i].setThickness(1f);
			curve_border[i].linkOrigin(curve_border_pos[i]);
			curve_border[i].move(curve_border_radius*(1+2*i),0.f);
			curve_border[i].setLength(370);
			curve_border[i].setRefreshRate(0.01);
		}
	}
		
	public static double fun1(double t, double val){
		t *= 2.f;
		return val*Math.abs(1.0-MUtils.mod(t-val,val*2)/val );
	}
	
	public Vector2f letter_a(){
		double rt = fun1(t, 4-1);
		Vector2f pos = Vector2f.lerp((float)rt,
				new Vector2f(120.f+12.f*7.f,200-20.f*7.f),
				new Vector2f(0.f,0.f),
				new Vector2f(120.f,200.f),
				new Vector2f(240.f,0.f));
		pos.translate(10.f*(float)Math.random(),10.f*(float)Math.random());
		
		return pos;
	}
	
	public Vector2f letter_g(){
		double rt = fun1(t, 5-1);
		float l1 = 120.f;
		float l2 = 240.f;
		Vector2f pos = Vector2f.lerp((float)rt,
				new Vector2f(l1,  l2 ),
				new Vector2f(0.f, l1 ),
				new Vector2f(l1,  0.f),
				new Vector2f(l2,  l1 ),
				new Vector2f(l1,  l1) );
		pos.translate(10.f*(float)Math.random(),10.f*(float)Math.random());
		return pos;
	}
	
	public Vector2f letter_l(){
		double rt = fun1(t, 3-1);
		float l1 = 120.f;
		float l2 = 240.f;
		Vector2f pos = Vector2f.lerp((float)rt,
				new Vector2f(20.f,  l2),
				new Vector2f(0.f,     0.f ),
				new Vector2f(l1,     20.f )
				);
		pos.translate(10.f*(float)Math.random(),10.f*(float)Math.random());
		return pos;
	}

	@Override
	public void scene() {
		if(dt < 1.f)
			t += dt;
	
		trail1.setOrigin(letter_a());
		trail2.setOrigin(letter_g());
		trail3.setOrigin(letter_l());
		
		trail1.update(dt);
		trail2.update(dt);
		trail3.update(dt);
		

		
		for(int i=0;i<curve_border.length;++i){
			double rt = t-3.1416;
			curve_border_pos[i].setPolar(curve_border_radius*(1.f+4.f*(float)Math.cos(4.0*rt))/5.f,(float)rt);
			curve_border_pos[i].y = Math.abs(curve_border_pos[i].y);			
			curve_border[i].update(dt);
			draw(curve_border[i]);
		}
	
		draw(trail1);
		draw(trail2);
		draw(trail3);

	}

}
