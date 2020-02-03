//
// Copyright © 2015 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package red5pro.org.testandroidproject.tests.Subscribe360Test;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.red5pro.streaming.R5StreamFormat;
import com.red5pro.streaming.source.R5StreamImageBytes;
import com.red5pro.streaming.view.R5VideoViewRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/*
    HELLO! This was originally to be a 360 renderer, but to minimize complexity as to why
    this can't render properly, it has become an attempt at trying to run the OpenGL routines from
    core through OpenGL API in Java.

    It does not like me.

    Though this tool was very useful in showing me it doesn't like me: https://github.com/google/gapid
*/
public class CustomVideoViewRenderer extends R5VideoViewRenderer {

    private final short elements[] = {
            0, 1, 2,
            2, 3, 0
    };

    private float texcoords[] = {
            -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            1.0f,  1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
    };

    private final String vertexShaderCode =
            "precision mediump float;" +
            "attribute vec4 position;" +
            "attribute vec3 color;" +
            "attribute vec2 texcoord;" +
            "uniform mat4 ProjectionMatrix;" +
            "varying vec3 Color;" +
            "varying vec2 Texcoord;" +
            "uniform mat2 rotation_matrix;" +
            "void main(void) {" +
            "   vec2 tmp = texcoord;" +
            "   tmp -= vec2(0.5, 0.5);" +
            "   tmp = rotation_matrix * tmp;" +
            "   tmp += vec2(0.5, 0.5);" +
            "   Texcoord = tmp;" +
            "   Color = color;" +
            "   gl_Position = ProjectionMatrix * position;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "varying vec3 Color;" +
            "varying vec2 Texcoord;" +
            "uniform sampler2D SamplerY;" +
            "uniform sampler2D SamplerU;" +
            "uniform sampler2D SamplerV;" +
            "void main(void) {" +
            "   vec3 yuv;" +
            "   vec3 rgb;" +
            "   yuv.x = texture2D(SamplerY, Texcoord).r;" +
            "   yuv.y = texture2D(SamplerU, Texcoord).r - 0.5;" +
            "   yuv.z = texture2D(SamplerV, Texcoord).r - 0.5;" +
            "   rgb = mat3( 1,       1,         1," +
            "               0,       -0.39465,  2.03211," +
            "               1.13983, -0.58060,  0) * yuv;" +
            "   gl_FragColor = vec4(rgb, 1.0);" +
            "}";

    public static int loadShader (int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    protected int mProgram;
    protected int mPosition;
    protected int mColor;
    protected int mTexture;
    protected int modelViewProjectionMatrix;

    protected int mSamplerY;
    protected int mSamplerU;
    protected int mSamplerV;

    protected int[] ytex = new int[1];
    protected int[] utex = new int[1];
    protected int[] vtex = new int[1];
    protected FloatBuffer mPositionBuffer;
    protected ShortBuffer mElementBuffer;
    protected int[] vbo = new int[1];
    protected int[] ibo = new int[1];
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;

    private volatile Boolean isRendering = false;

    public CustomVideoViewRenderer (GLSurfaceView view) {
        super(view);
        glInit(view);
    }

    public void glInit(GLSurfaceView view) {
        this.view.setEGLContextClientVersion(2);
    }

    protected void setVertexPosition(int index, float x, float y) {
        texcoords[index*7] = x;
        texcoords[index*7+1] = y;
    }

    protected void initBuffers () {

        int i;
        /* [>> BUFFERS] */
        // vertex buffer.
        mPositionBuffer = ByteBuffer.allocateDirect(texcoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositionBuffer.put(texcoords).position(0);
        // element buffer.
        mElementBuffer = ByteBuffer.allocateDirect(elements.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mElementBuffer.put(elements).position(0);

        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glGenBuffers(1, ibo, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mPositionBuffer.capacity() * BYTES_PER_FLOAT,
                mPositionBuffer, GLES20.GL_DYNAMIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mElementBuffer.capacity() * BYTES_PER_SHORT,
                mElementBuffer, GLES20.GL_DYNAMIC_DRAW);
        /* [<< BUFFERS] */

        i = GLES20.glGetError();
        if (i != 0) {
            Log.d("ERROR", "INIT BUFFERS ERROR Happened: " + i);
            return;
        }


    }

    protected void initProgram (int program) {

        int i;

        GLES20.glUseProgram(mProgram);

        mPosition = GLES20.glGetAttribLocation(program, "position");
        mColor = GLES20.glGetAttribLocation(program, "color");
        mTexture = GLES20.glGetAttribLocation(program, "texcoord");
        modelViewProjectionMatrix = GLES20.glGetAttribLocation(program, "ProjectionMatrix");

        mSamplerY = GLES20.glGetUniformLocation(program, "SamplerY");
        mSamplerU = GLES20.glGetUniformLocation(program, "SamplerU");
        mSamplerV = GLES20.glGetUniformLocation(program, "SamplerV");

        i = GLES20.glGetError();
        if (i != 0) {
            Log.d("ERROR", "INIT PROGRAM ERROR Happened: " + i);
            return;
        }

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        super.onSurfaceCreated(glUnused, config);

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        initBuffers();

        int i;

        mProgram = GLES20.glCreateProgram();

        /* [>> SHADERS] */
        int vertexShader = CustomVideoViewRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = CustomVideoViewRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        i = GLES20.glGetError();
        if (i != 0) {
            Log.d("ERROR", "SETUP ERROR Happened (Shaders): " + i);
            return;
        }
        /* [<< SHADERS] */

        initProgram(mProgram);

//        mPositionBuffer.position(0);
        GLES20.glVertexAttribPointer(mPosition, 2,
                GLES20.GL_FLOAT, false,
                7 * BYTES_PER_FLOAT, 0);
        GLES20.glEnableVertexAttribArray(mPosition);

//        mPositionBuffer.position(2);
        GLES20.glVertexAttribPointer(mColor, 3,
                GLES20.GL_FLOAT, false,
                7 * BYTES_PER_FLOAT, 2 * BYTES_PER_FLOAT);
        GLES20.glEnableVertexAttribArray(mColor);

//        mPositionBuffer.position(5);
        GLES20.glVertexAttribPointer(mTexture, 2,
                GLES20.GL_FLOAT, false,
                7 * BYTES_PER_FLOAT, 5 * BYTES_PER_FLOAT);
        GLES20.glEnableVertexAttribArray(mTexture);

        i = GLES20.glGetError();
        if (i != 0) {
            Log.d("ERROR", "SETUP ERROR Happened (Buffers): " + i);
            return;
        }

        /* [>> TEXTURES] */
        GLES20.glUniform1i(mSamplerY, 0);
        GLES20.glUniform1i(mSamplerU, 1);
        GLES20.glUniform1i(mSamplerV, 2);
        GLES20.glGenTextures(1, ytex, 0);
        GLES20.glGenTextures(1, utex, 0);
        GLES20.glGenTextures(1, vtex, 0);
        /* [<< TEXTURES] */

        i = GLES20.glGetError();
        if (i != 0) {
            Log.d("ERROR", "SETUP ERROR Happened (Textures): " + i);
            return;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        super.onSurfaceChanged(gl, width, height);

    }

    private static final int FIELD_OF_VIEW_DEGREES = 90;
    private static final float Z_NEAR = .1f;
    private static final float Z_FAR = 100;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    @Override
    public void onDrawFrame(int rotation, int scaleMode) {

        R5StreamImageBytes bytes = this.stream.getStreamImageBytes();

        if (bytes == null || isRendering ||
                (bytes != null && bytes.isEmpty())) {
            return;
        }

        isRendering = true;

        int i = 0;

        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);

        int pixel_w = bytes.width;
        int pixel_h = bytes.height;
        R5StreamFormat format = bytes.getFormat();
        float w = (this.width * 1.0f) / (pixel_w * 1.0f);
        float h = (this.height * 1.0f) / (pixel_h * 1.0f);

        setVertexPosition(0, -w, h);
        setVertexPosition(1, w, h);
        setVertexPosition(2, w, -h);
        setVertexPosition(3, -w, -h);
        mPositionBuffer.clear();
        mPositionBuffer.put(texcoords);

        mPositionBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mPositionBuffer.capacity() * BYTES_PER_FLOAT,
                mPositionBuffer, GLES20.GL_DYNAMIC_DRAW);

        mElementBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mElementBuffer.capacity() * BYTES_PER_SHORT,
                mElementBuffer, GLES20.GL_DYNAMIC_DRAW);

        i = GLES20.glGetError();
        if (i != 0) {
            isRendering = false;
            Log.d("ERROR", "DRAW ERROR (1) Happened: " + i);
            return;
        }

        GLES20.glViewport(0, 0, width, height);
//        Matrix.perspectiveM(
//                projectionMatrix, 0, FIELD_OF_VIEW_DEGREES, (float) width / height, Z_NEAR, Z_FAR);
//        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
//        GLES20.glUniformMatrix4fv (modelViewProjectionMatrix, 1, false, viewProjectionMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, elements.length, GLES20.GL_UNSIGNED_SHORT, 0);

        i = GLES20.glGetError();
        if (i != 0) {
            isRendering = false;
            Log.d("ERROR", "DRAW ERROR (2) Happened: " + i);
            return;
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ytex[0]);
//        GLES20.glUniform1i(mSamplerY, 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, pixel_w, pixel_h, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes.data[0]));

        i = GLES20.glGetError();
        if (i != 0) {
            isRendering = false;
            Log.d("ERROR", "DRAW ERROR (3) Happened: " + i);
            return;
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, utex[0]);
//        GLES20.glUniform1i(mSamplerUV, 1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, pixel_w / 2, pixel_h / 2, 0,
                GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes.data[1]));

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vtex[0]);
//        GLES20.glUniform1i(mSamplerUV, 1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, pixel_w / 2, pixel_h / 2, 0,
                GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes.data[2]));

        isRendering = false;
        i = GLES20.glGetError();
        if (i != 0) {
            Log.d("ERROR", "DRAW ERROR (4) Happened: " + i);
            return;
        }

    }

}
