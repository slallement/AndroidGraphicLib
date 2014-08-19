precision mediump float;
uniform vec4 uColor;
uniform sampler2D uTexture; 
varying vec2 vTexCoordOut; 

void main() {
	gl_FragColor = uColor * texture2D(uTexture, vTexCoordOut);
}

