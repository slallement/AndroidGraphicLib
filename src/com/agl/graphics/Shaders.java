package com.agl.graphics;

import java.util.HashMap;
import java.util.Map;

import com.agl.example.GLES20Renderer;

import android.opengl.GLES20;
import android.util.Log;

/**
 * @author Sergu A shader
 */
public class Shaders {
	/* protected class Uniform{ String name; float attr[];
	 * Uniform(String nname, float[] nattr){ name = nname; attr = nattr; } } */
	protected int mProgram;
	protected Map<String, float[]> uniform; // map of uniform variables of the
											// shaders

	/**
	 * Constructor
	 */
	public Shaders() {
		uniform = new HashMap<String, float[]>();
	}

	/**
	 * Load a shaders
	 * @param vertex_path path of the vertex shader
	 * @param fragment_path path of the fragment shader
	 */
	public void init(String vertex_path, String fragment_path) {
		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		int vertexShader = MUtils.loadShader(GLES20.GL_VERTEX_SHADER,
				GLES20Renderer.loadDataFromAsset(vertex_path));
		int fragmentShader = MUtils.loadShader(GLES20.GL_FRAGMENT_SHADER,
				GLES20Renderer.loadDataFromAsset(fragment_path));
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(mProgram);
	}

	/**
	 * Set a uniform variable of the shaders
	 * @param name name of the attribute
	 * @param attr value
	 */
	public void set(String name, float... attr) {
		uniform.put(name, attr);
	}

	/**
	 * Use the shaders
	 */
	void draw() {
		GLES20.glUseProgram(mProgram);
		for (Map.Entry<String, float[]> u : uniform.entrySet()) {
			int handle = GLES20.glGetUniformLocation(mProgram, u.getKey());
			if (handle != -1) {
				switch (u.getValue().length) {
				case 1:
					GLES20.glUniform1f(handle, u.getValue()[0]);
					// Log.v("shad","name="+u.getKey()+"; val = "+
					// u.getValue()[0]);
					break;
				case 2:
					GLES20.glUniform2f(handle, u.getValue()[0],
							u.getValue()[1]);
					break;
				case 3:
					GLES20.glUniform3f(handle, u.getValue()[0],
							u.getValue()[1], u.getValue()[2]);
					break;
				case 4:
					GLES20.glUniform4f(handle, u.getValue()[0],
							u.getValue()[1], u.getValue()[2], u.getValue()[3]);
					break;
				default:
					;

				}
			} else {
				Log.v("vv", "ERROR handle = -1 for |" + u.getKey() + "|");
			}
		}
	}
}
