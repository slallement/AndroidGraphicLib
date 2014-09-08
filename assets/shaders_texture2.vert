uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec2 aTexCoord;
attribute vec4 aColor;

varying vec2 vTexCoordOut; 
varying vec3 fragmentColor;
varying vec4 vColorOut;

void main() {
	gl_Position = uMVPMatrix * aPosition;
	vTexCoordOut = aTexCoord;
	vColorOut = aColor;
}

