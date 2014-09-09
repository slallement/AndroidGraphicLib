package com.agl.graphics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * @author Sergu Useful class
 */
public class MUtils {
	/**
	 * return the texture handle (ref to int[])
	 * 
	 * @param context
	 * @param resourceId
	 * @return
	 */
	public static int[] loadTexture(final Context context, final int resourceId) {
		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// useful for ransparency ?
			options.inScaled = false; // No pre-scaling

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(), resourceId, options);
			if (bitmap == null) {
				throw new RuntimeException("Error decoding bitmap");
			}
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle;
	}

	/** return texture handle, width and height */
	public static int[][] loadTexture2(final Context context,
			final int resourceId) {
		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		if (textureHandle[0] != 0) {

			options.inScaled = false; // No pre-scaling

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(), resourceId, options);
			if (bitmap == null) {
				throw new RuntimeException("Error decoding bitmap");
			}
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return new int[][] { textureHandle, new int[] { options.outWidth },
				new int[] { options.outHeight } };
	}

	/**
	 * Read a text file passed as a ressource id
	 * 
	 * @param context
	 * @param resourceId
	 * @return
	 */
	public static String readTextFileFromRawResource(final Context context,
			final int resourceId) {
		final InputStream inputStream = context.getResources().openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try {
			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine);
				body.append('\n');
			}
		} catch (IOException e) {
			return null;
		}

		return body.toString();
	}

	/**
	 * Load a shader
	 * 
	 * @param type
	 * @param shaderCode
	 * @return the shader id
	 */
	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	/**
	 * Load a file
	 * (need authorizations ?)
	 * 
	 * @param filename
	 * @return a vertex code
	 */
	static public String loadFile(String filename) {
		StringBuilder vertexCode = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				vertexCode.append(line);
				vertexCode.append('\n');
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"unable to load shader from file [" + filename + "]", e);
		}

		return vertexCode.toString();
	}
	
	/**
	 * Converts color in the HSV color space to the RBG color space
	 * @param hue (the color)
	 * @param saturation (0.f -> black and white -- 1.f -> colorful)
	 * @param value (0.f -> black)
	 * @return list of 3 colors (RGB) from 0.0f to 1.0f
	 */
	public static float[] hsvToRgb(float hue, float saturation, float value) {
	    float r, g, b;

	    int h = (int)(hue * 6);
	    float f = hue * 6 - h;
	    float p = value * (1 - saturation);
	    float q = value * (1 - f * saturation);
	    float t = value * (1 - (1 - f) * saturation);

	    if (h == 0) {
	        r = value;
	        g = t;
	        b = p;
	    } else if (h == 1) {
	        r = q;
	        g = value;
	        b = p;
	    } else if (h == 2) {
	        r = p;
	        g = value;
	        b = t;
	    } else if (h == 3) {
	        r = p;
	        g = q;
	        b = value;
	    } else if( h ==4) {
	        r = t;
	        g = p;
	        b = value;
	    } else if (h == 5) {
	        r = value;
	        g = p;
	        b = q;
	    } else {
	        throw new RuntimeException("Something went wrong when converting from HSV to RGB. (" + hue + ", " + saturation + ", " + value+")");
	    }
	    return new float[]{r,g,b};
	}
}
