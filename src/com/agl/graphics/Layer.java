package com.agl.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.util.Log;

/**
 * @author Sergu
 * Class to store a render off screen
 */
public class Layer {
	// RENDER TO TEXTURE VARIABLES
	int[] buf, fbo, renderTex, rb; // the framebuffer, the renderbuffer and the
								// texture to render
	int texW = -1; // the texture's width
	int texH = -1; // the texture's height
	IntBuffer texBuffer; // Buffer to store the texture
	boolean ok = false;

	/**
	 * @param width width of the render texture
	 * @param height height of the render texture
	 */
	public Layer(int width, int height) {
		texW = width;
		texH = height;
	
		final int FLOAT_SIZE_BYTES = 4;
		// create the ints for the framebuffer, render buffer and texture
		fbo = new int[1];
		rb = new int[1];
		renderTex = new int[1];

		// -------------------------------------------------------------
		// texture
		GLES20.glGenTextures(1, renderTex, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);

		// parameters - we have to make sure we clamp the textures to the
		// edges
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

		// create an empty intbuffer first
		buf = new int[texW * texH];
		texBuffer = ByteBuffer
				.allocateDirect(buf.length * FLOAT_SIZE_BYTES)
				.order(ByteOrder.nativeOrder()).asIntBuffer();

		// generate the textures
		/*GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
				texW, texH, 0, GLES20.GL_RGB,
				GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);*/
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
				texW, texH, 0, GLES20.GL_RGB,
				GLES20.GL_UNSIGNED_BYTE, texBuffer);
		 

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

		// -------------------------------------------------------------
		// render buffer object
		GLES20.glGenRenderbuffers(1, rb, 0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, rb[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
				GLES20.GL_DEPTH_COMPONENT16, texW, texH);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

		// -------------------------------------------------------------
		// FBO
		GLES20.glGenFramebuffers(1, fbo, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);

		// attach the texture to the FBO
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				renderTex[0], 0);
		
		/*GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthMask(false);
		GLES20.glDisable(GLES20.GL_BLEND);*/
		//GLES20.glViewport(0, 0, mWidth, mHeight);
		
		// attach the RBO
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, rb[0]);
		/*GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_RGBA, GLES20.GL_RENDERBUFFER, rb[0]);*/

		// check if ok
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status == GLES20.GL_FRAMEBUFFER_COMPLETE){
			ok = true;
		}else{
			if(status ==  GLES20.GL_FRAMEBUFFER_UNSUPPORTED){
				Log.v("tag","erreur fbo non support�s");
			}
			Log.v("tag","erreur fbo ("+status+" ) w= "+texW);
		}
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
		
		// switch back to window-system-provided framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

		// create render buffer and bind 16-bit depth buffer
		// GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
		// GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
		// GLES20.GL_DEPTH_COMPONENT16, texW, texH);
		
	}

	/*public void finalize() {
		GLES20.glDeleteTextures(1, renderTex, 0);
		GLES20.glDeleteFramebuffers(1, fbo, 0);
		GLES20.glDeleteRenderbuffers(1, rb, 0);
	}*/

	/** Bind the framebuffer */
	public void use() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}
	

	/** unbind the framebuffer */
	public void unuse() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		// reset to the default framebuffer
	}
	
	/** Draw the texture
	 */
	void draw() {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);
		//GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
	
	/** Get the texture
	 * @return the texture id
	 */
	public int getTexture(){
		return renderTex[0];
	}


}