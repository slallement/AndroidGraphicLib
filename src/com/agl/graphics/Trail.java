package com.agl.graphics;

/**
 * Class to make a polyline evolving in time 
 * @author Serguei Lallement
 *
 */
public class Trail extends PolyLine {
	/**
	 * the positions of the point that will be added to the trail
	 */
	Vector2f origin = new Vector2f();

	/**
	 * the number of subdivision (points) of the polyline created
	 */
	protected int curveLength = 30;
	/**
	 * duration between the adding of a points to the curve
	 */
	protected double refreshRate = 0.05;

	protected int pointsBegining = 0;

	protected double time = 0.0;

	/**
	 * Constructor of an empty trail
	 */
	public Trail() {
		time = 0.0;
	}

	/**
	 * Set the position of the new points that will be added to the trail
	 * @param nx
	 * @param ny
	 */
	public void setOrigin(float nx, float ny) {
		origin.x = nx;
		origin.y = ny;
	}
	
	/**
	 * Move the position of the new points that will be added to the trail
	 * @param nx
	 * @param ny
	 */
	public void moveOrigin(float dx, float dy) {
		origin.x += dx;
		origin.y += dy;
	}

	/**
	 * should be called at each frame
	 * @param dt
	 */
	public void update(double dt) {
		time += dt;
		if (time > refreshRate) {
			time -= refreshRate;
			updateTrail();
		}
	}

	protected void updateTrail() {
		addPoint(origin.x, origin.y);
		if (pointsCount - pointsBegining > curveLength)
			eraseFirstPoint();
	}

	// ----

	protected void eraseFirstPoint() {
		if (pointsBegining + 2 > pointsCount) {
			return;
		}
		pointsBegining += 2;
		setCoords();
	}

	@Override
	protected void reallocatePoints(int newSize) {
		if (newSize <= points.length) {
			for (int i = 0; i < newSize; ++i) {
				points[i] = points[pointsBegining + i];
			}
			pointsCount -= pointsBegining;
			pointsBegining = 0;
			return;
		}
		newSize -= pointsBegining;

		float temp[] = new float[newSize];
		final int size = Math.min(newSize, points.length);
		for (int i = 0; i + pointsBegining < size; ++i) {
			temp[i] = points[pointsBegining + i];
		}
		points = temp;
		pointsCount -= pointsBegining;
		pointsBegining = 0;
	}

	@Override
	public void addPoint(float posX, float posY) {
		// try{
		if (pointsCount >= 2 && points[pointsCount - 2] == posX
				&& points[pointsCount - 1] == posY) {
			return;
		}
		if (points.length < pointsCount + 2) {
			reallocatePoints(pointsCount + 2 + curveLength);
		}
		points[pointsCount] = posX;
		points[pointsCount + 1] = posY;
		pointsCount += 2;
		setCoords();

		initVertexBuffer();
		// initTexture();
		initColorBuffer();
		/* } catch (Exception e) {
		 * 
		 * } */
	}

	@Override
	void setCoords() {
		// 2 points minimum
		if (pointsCount < 4) {
			mCoords = new float[0];
			return;
		}
		mCoords = new float[(pointsCount - pointsBegining) * 3];

		Vector2f d;
		d = new Vector2f(points[pointsBegining + 0], points[pointsBegining + 1])
				.sub(new Vector2f(points[pointsBegining + 2],
						points[pointsBegining + 3])).getNormal();
		d.normalize();
		d.mul(thickness);
		mCoords[0] = points[pointsBegining + 0] - d.x;
		mCoords[1] = points[pointsBegining + 1] - d.y;
		mCoords[2] = 0; // z axis
		mCoords[3] = points[pointsBegining + 0] + d.x;
		mCoords[4] = points[pointsBegining + 1] + d.y;
		mCoords[5] = 0; // z axis

		for (int i = 1; i <= (pointsCount - pointsBegining) / 2 - 2; ++i) {
			Vector2f v0 = new Vector2f(points[pointsBegining + 2 * i - 2],
					points[pointsBegining + 2 * i - 1]);
			Vector2f v1 = new Vector2f(points[pointsBegining + 2 * i],
					points[pointsBegining + 2 * i + 1]);
			Vector2f v2 = new Vector2f(points[pointsBegining + 2 * i + 2],
					points[pointsBegining + 2 * i + 3]);

			v0.set(v1.sub(v0));
			v0.normalize();
			v2.set(v2.sub(v1));
			v2.normalize();
			d = v0.add(v2).getNormal();
			float dd = thickness / 2.f / Math.max(0.8f, d.dot(v0));
			d.mul(dd);

			// point 1
			mCoords[6 * i] = v1.x + d.x;
			mCoords[6 * i + 1] = v1.y + d.y;
			mCoords[6 * i + 2] = 0; // z axis

			// point2
			mCoords[6 * i + 3] = v1.x - d.x;
			mCoords[6 * i + 4] = v1.y - d.y;
			mCoords[6 * i + 5] = 0; // z axis
		}
		int offset = pointsCount;
		d = new Vector2f(points[offset - 4], points[offset - 3])
				.sub(new Vector2f(points[offset - 2], points[offset - 1]))
				.getNormal();
		d.normalize();
		d.mul(thickness / 1.f);
		mCoords[mCoords.length - 6] = points[offset - 2] - d.x;
		mCoords[mCoords.length - 5] = points[offset - 1] - d.y;
		mCoords[mCoords.length - 4] = 0; // z axis
		mCoords[mCoords.length - 3] = points[offset - 2] + d.x;
		mCoords[mCoords.length - 2] = points[offset - 1] + d.y;
		mCoords[mCoords.length - 1] = 0; // z axis
	}
}
