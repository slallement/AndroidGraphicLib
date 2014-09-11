package com.agl.example4;

import java.util.ArrayList;
import com.agl.graphics.Triangle;
import com.agl.graphics.Vector2f;

public class PenroseTiles {
	final double goldenRatio = (1.0 + Math.sqrt(5.0)) / 2.0;
	final float finv_goldenRatio = (float)(2.0/(1.0 + Math.sqrt(5.0)));
	static public class PenTriangle {
		public Vector2f a,b,c;
		public int color;
		public PenTriangle(int ncolor, Vector2f na, Vector2f nb, Vector2f nc){
			color = ncolor;
			a = na.copy();
			b = nb.copy();
			c = nc.copy();
		}
	}
	
	ArrayList<PenTriangle> tri = new ArrayList<PenTriangle>();
	
	public PenroseTiles(ArrayList<PenTriangle> li, int subdiv){
		init(li);
		Generate(subdiv);
	}
	
	public PenroseTiles(int subdiv){
		ArrayList<PenTriangle> li = new ArrayList<PenTriangle>();
		/*li.add(new PenTriangle(0, new Vector2f(210.f,410.f),
									new Vector2f(10.f,10.f),
									new Vector2f(410.f,10.f) ));*/
		Vector2f A = new Vector2f(200.f,300.f);
		Vector2f B = new Vector2f();
		Vector2f C = new Vector2f();
		final float ratio = 2.f*3.1416f/10.f;
		final float rad = 200.f;
		for(int i=0;i<10;++i){
			B.setPolar(rad, (float)i*ratio);
			C.setPolar(rad, ((float)i+1.f)*ratio);
			if(i%2 == 0){
				Vector2f T = B.copy(); B = C; C = T;
			}
			B.translate(A);
			C.translate(A);
			li.add(new PenTriangle(0, A,B,C));
		}
		init(li);
		Generate(subdiv);
	}
	
	public Triangle[] getTriangles(){
		Triangle[] res = new Triangle[tri.size()];
		float[] red = new float[]{0.98f,0.98f,0.1f,1.f};
		float[] blue = new float[]{0.3f,0.55f,0.7f,1.f};
		float[] col;
		for(int i=0;i<res.length;++i){
			PenTriangle t = tri.get(i);
			res[i] = new Triangle(t.a.x,t.a.y,t.b.x,t.b.y,t.c.x,t.c.y);
			if(t.color == 0){
				col = red;
			}else{
				col = blue;
			}
			res[i].setColor(col[0], col [1], col[2]);
		}
		return res;
	}
	
	public void Generate(int subdiv){
		//ArrayList<PenTriangle> tri2;
		for(int i=0;i<subdiv;++i){
			//tri2 = new ArrayList<PenTriangle>();
			float sz = tri.size();
			for(int j=0;j<sz;++j){
				Vector2f A = tri.get(j).a.copy();
				Vector2f B = tri.get(j).b.copy();
				Vector2f C = tri.get(j).c.copy();
				if(tri.get(j).color == 0){
					 Vector2f P = new Vector2f();//= A.add(B.sub(A).mul(finv_goldenRatio));
					 P.x = A.x + (B.x-A.x)*finv_goldenRatio;
					 P.y = A.y + (B.y-A.y)*finv_goldenRatio;
					 tri.set(j, new PenTriangle(0, C, P, B));
					 //tri2.add(new PenTriangle(0, C, P, B));
					 tri.add(new PenTriangle(1, P, C, A));
				}else{
					Vector2f Q = new Vector2f();//B.add(A.sub(B).mul(finv_goldenRatio));
					Vector2f R = new Vector2f();//B.add(C.sub(B).mul(finv_goldenRatio));
					Q.x = B.x + (A.x-B.x)*finv_goldenRatio;
					Q.y = B.y + (A.y-B.y)*finv_goldenRatio;
					R.x = B.x + (C.x-B.x)*finv_goldenRatio;
					R.y = B.y + (C.y-B.y)*finv_goldenRatio;
					tri.set(j, new PenTriangle(1, R, C, A));
				    //tri2.add(new PenTriangle(1, R, C, A));
				    tri.add(new PenTriangle(1, Q, R, B));
				    tri.add(new PenTriangle(0, R, Q, A));
				}
			}
			
		}
	}
	
	public void init(ArrayList<PenTriangle> li){
		tri = li;
	}
}