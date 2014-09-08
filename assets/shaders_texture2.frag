precision mediump float;
uniform vec4 uColor;
uniform sampler2D uTexture; 
varying vec2 vTexCoordOut;
varying vec4 vColorOut;

void main() {
	gl_FragColor = vColorOut * texture2D(uTexture, vTexCoordOut);
}

