package com.agl.graphics;

import android.content.Context;
import android.opengl.GLES20;

/**
 * @author Sergu A structure representing a texture (id in memory + size)
 */
public class Texture {
	protected int[] mId;
	protected int mIdRes;
	protected int w = -1;
	protected int h = -1;

	/**
	 * Set the texture
	 * @param context
	 * @param id id of a texture
	 */
	public void setByRessource(Context context, int id) {
		mIdRes = id;
		int data[][] = MUtils.loadTexture2(context, id);
		mId = data[0];
		w = data[1][0];
		h = data[2][0];
	}

	/**
	 * Get the id of the texture
	 * @return id in memory
	 */
	public int getHandle() {
		return mId[0];
	}

	/**
	 * Free the texture in memory
	 */
	public void delete() {
		GLES20.glDeleteTextures(mId.length, mId, 0);
	}
}
