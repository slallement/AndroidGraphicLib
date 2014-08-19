precision mediump float;
uniform float uTime;
uniform vec4 uColor;
uniform sampler2D uTexture; 
varying vec2 vTexCoordOut; 

void main()
{
    const float freq = 1.;
    const float lambda = 0.8;
    float amplitude = 0.1;
    // lookup the pixel in the texture
    vec2 offset = vec2(0,-0.5*0.+0.3*amplitude*sin(6.28*(vTexCoordOut.x-freq*uTime)/lambda));
    vec4 pixel = texture2D(uTexture, vTexCoordOut.xy+offset.xy);
    //vec4 pixel = texture2D(uTexture, vTexCoordOut);

    // multiply it by the color
    gl_FragColor = uColor * pixel;
}
