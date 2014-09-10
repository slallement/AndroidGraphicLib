package com.agl.example2;

import java.util.ArrayList;

import com.agl.graphics.Circle;
import com.agl.graphics.GLRenderer;
import com.agl.graphics.Trail;
import com.android.GLText.GLText;

import android.content.Context;
import android.opengl.GLES20;

/** A little game for demo
 * you can move a green circle and your goal is to avoid the red moving circle
 * if you loose, the green circle becomes yellow
 * 
 * @author Serguei Lallement
 *
 */
public class DemoGame extends GLRenderer {

	Circle player = null;
	ArrayList<Circle> ennemies = new ArrayList<Circle>();
	Clock clock1 = new Clock();
	private GLText glText;
	public Trail trail;
	
	DemoGame(Context c) {
		super(c);
	}
	
	@Override
	public void onCreate(int width, int height, boolean contextLost) {
		super.onCreate(width, height, contextLost);
		trail = new Trail();
		trail.setThickness(2.f);
		trail.setPosition(mWidth/2.f, mHeight/2.f);
		trail.setColor(new float[]{
				0.0f,0.7f,0.0f,1.0f,
				0.0f,5.0f,0.0f,0.0f
		});
        
        
        player = new Circle(50.f,100);
		player.setColor(0.0f, 1.0f, 0.0f);
		player.move(mWidth/2, mHeight/2); // center
		
		glText = new GLText(mContext.getAssets());
        glText.load("ARBONNIE.ttf", 32, 2, 2 );
	}
	
	// side effect : generate a new number at each call
	static float randGenerator() {
		return (float) ((Math.random()-0.5)*2.f);
	}
	
	/// update the game logic
	void updateGame() {
		
		if(clock1.restartIf(1.f)){
			Circle c =  new Circle(20.f+30.f*(float)Math.random(),100);
			c.setColor(1.0f, 0.0f, 0.0f);
			do {
				c.move((float)(mWidth*Math.random()), (float)(mHeight*Math.random()));
			} while(testCircleCircle2(player.getX(), player.getY(), player.getRadius()*1.5f,
										c.getX(), c.getY(), c.getRadius()));
			ennemies.add(c);
		}
		for(Circle c : ennemies){
			c.move(randGenerator()*10.f, randGenerator()*10.f);
		}
		for(int i=0;i<ennemies.size();++i){
			if(isOut(ennemies.get(i)) ){
				ennemies.remove(i);
			}
		}
		testCollisions();
	}
	
	boolean isOut(Circle c) {
		return (c.getX() < 0 || c.getX() > mWidth 
				|| c.getY() < 0 || c.getY() > mHeight);
	}
	
	static boolean testCircleCircle(Circle c1, Circle c2) {
		float dx = (c1.getX()-c2.getX());
		float dy = (c1.getY()-c2.getY());
		float rad = c1.getRadius() + c2.getRadius();
		return (dx*dx+dy*dy < rad*rad);
	}
	
	static boolean testCircleCircle2(float c1x, float c1y, float c1r, float c2x, float c2y, float c2r) {
		float dx = c1x-c2x;
		float dy = c1y-c2y;
		float rad = c1r+c2r;
		return (dx*dx+dy*dy < rad*rad);
	}
	
	void testCollisions() {
		for(Circle c : ennemies){
			if(testCircleCircle(player, c)){
				player.setColor(1.f, 1.f, 0.f);
			}
		}
	}
	
	@Override
	public void scene(){
		updateGame();
		display();
	}
	
	double t1 = 0;
	double q = 5.0/2;
	void display()
	{
		draw(player);
		for(Circle c : ennemies){
			draw(c);
		}
		
		// text
		glText.begin( 1.0f, 1.0f, 1.0f, 1.0f, mMVPMatrix ); // Begin Text Rendering (Set Color WHITE)
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA); // active transparent bg
		glText.drawC("Avoid the red circles", mWidth/2.f, mHeight*4.f/5.f, 0.f);
		glText.end();
		
		draw(trail);

		if(dt < 1000.0){
			t1 += dt*5.0;
			trail.update(dt);
			trail.setRotation((float) t1*3.1416f);
			trail.setOrigin(
					(float)(100.0/q*( (q-1.0)*Math.cos(t1))+20.0*Math.cos( (q-1.0)*t1) ),
					(float)(100.0/q*( (q-1.0)*Math.sin(t1))-20.0*Math.sin( (q-1.0)*t1) )
					//(float)(100.0*Math.cos(2.0*t1)),
					//(float)(100.0*Math.sin(2.0*t1))
					);
		}
	}

	public void movePlayer(float dx, float dy) {
		if(player == null)
			return;
		player.move(dx, dy);
		trail.move(dx,dy);
	}
	
	public class Clock {
		long t1,t2;
		void restart() {
			t1 = System.currentTimeMillis();
		}
		float getElapsedTime() {
			return (System.currentTimeMillis()-t1)/1000.f;
		}
		boolean restartIf(float time){
			boolean res = ((System.currentTimeMillis()-t1)/1000.f >= time);
			if(!res){
				return false;
			}
			t1 = System.currentTimeMillis();
			return true;
		}
	}

}
