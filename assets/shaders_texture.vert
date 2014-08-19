uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec2 aTexCoord;
varying vec2 vTexCoordOut; 

void main() {
	gl_Position = uMVPMatrix * aPosition;
	vTexCoordOut = aTexCoord;
}

