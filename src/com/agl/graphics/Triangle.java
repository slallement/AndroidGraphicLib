package com.agl.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.agl.example.GLES20Renderer;

import android.opengl.GLES20;

/**
 * @author Sergu
 * Shape of a simple triangle 
 */
public class Triangle {

    private FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    //private int mTextureHandle;
    int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // in counterclockwise order:
         0.0f,  0.0f, 0.0f,   // top
        100.f, 0.0f, 0.0f,   // bottom left
         100.f, 100.0f, 0.0f    // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex
    
    /*private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 aPosition;" +
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            "  gl_Position = uMVPMatrix * aPosition;" +
            "}";

        private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 uColor;" +
            "void main() {" +
            "  gl_FragColor = uColor;" +
            "}";*/

    /**
     * Constructor of a void triangle 
     * (use defaults coordinates)
     */
    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        // prepare shaders and OpenGL program
        /*int vertexShader = GLES20Renderer.loadShader(GLES20.GL_VERTEX_SHADER,
        		vertexShaderCode);
        int fragmentShader = GLES20Renderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
        		fragmentShaderCode);*/
        
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        int vertexShader = MUtils.loadShader(GLES20.GL_VERTEX_SHADER,
        		GLES20Renderer.loadDataFromAsset("shaders_vertex_base.vert"));
        int fragmentShader = MUtils.loadShader(GLES20.GL_FRAGMENT_SHADER,
        		GLES20Renderer.loadDataFromAsset("shaders_frag_base.frag"));
        
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);  
    }
    
    /**
     * @Override
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
