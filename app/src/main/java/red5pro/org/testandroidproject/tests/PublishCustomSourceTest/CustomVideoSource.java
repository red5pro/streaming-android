package red5pro.org.testandroidproject.tests.PublishCustomSourceTest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.source.R5VideoSource;

/**
 * How to use this class. Replace the Red5Camera instance with an instance of this.
 * Aquire a YV12 image from the camera, screen shot, or other resource.
 * Call 'setImage(buffer)' to drive the video stream.
 * The image will be cropped and centered in a gradient field. within the encodeYUV420 method
 *
 * Created by Andy Shaules on 4/8/2015.
 */
public class CustomVideoSource extends R5VideoSource {
    int width = 320;
    int height = 240;
    int bpp = 12;
    byte bufferIn[] ;
    byte bufferOut[] ;
    private Runnable engine;
    private volatile boolean doEncode=true;
    private long streamTime = 0;
    private Bitmap bitmap;
    int[] pixels = new int[320 * 240];
    boolean change = false;


    @Override
    protected void initSource() {
        //set the raw image input type, only YV12 is currently supported
        setFrameType(ImageFormat.YV12);
    }

    /**
     * This draws the 'lastImage' member into the incoming buffer while customizing pixels
     * @param yuv420sp
     * @param argb
     * @param width
     * @param height
     */
    void encodeYUV420(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;
        int vuIndex = frameSize + frameSize/4;
        int a, R, G, B, Y, U, V;
        int index = 0;
        for ( int y = 0; y < height; y++) {
            for ( int x = 0; x < width; x++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                yuv420sp[yIndex] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));

                if (y % 2 == 0 && x % 2 == 0) {
                    yuv420sp[uvIndex] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[vuIndex] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }

                yIndex++;

                if (y % 2 == 0 && x % 2 == 0) {

                    uvIndex++;
                    vuIndex++;
                }

                index ++;
            }
        }
    }

    @Override
    public void startEncoding() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(doEncode) {
                    double scaledTime = System.currentTimeMillis()*0.01;
                    int cursor = 0;
                    float scale = 0.04f;

                    for (int x = 0; x < 320; x++) {
                        for (int y = 0; y < 240; y++) {


                            float cx = x * scale;
                            float cy = y * scale;

                            double v = Math.sin(cx + scaledTime);
                            v += Math.sin(cy + scaledTime);
                            v += Math.sin(cx + cy + scaledTime);

                            cx += scale * Math.sin(scaledTime * 0.33f);
                            cy += scale * Math.sin(scaledTime * 0.2f);

                            v += Math.sin(Math.sqrt(cx * cx + cy * cy + 1.0) + scaledTime);

                            pixels[(y*width)+x] = ((int) (Math.sin(v * Math.PI) * 255.0)) << 16 |  //r
                                    ((int) (Math.cos(v * Math.PI) * 255.0)) << 8;  //g
                            //( 0 );  //b
                        }
                    }

                    change = true;

                    try{

                        Thread.sleep(100);
                    }catch (Exception e){
                    }
                }
            }
        }).start();

        engine=new Runnable() {
            @Override
            public void run() {
                int sizeInBits = (int) ((width * height) * bpp);
                bufferIn=new byte[sizeInBits/8];
                bufferOut=new byte[sizeInBits/8];
                int  cursor = 0;
                float scale = 0.4f;

                //a nice base gradient
                for(int o=0;o<240;o++){
                    for(int h=0;h<320;h++){

                        pixels[cursor++]= o<<16 | (240-o)<<8 |  ( (int)( h /320.0 * 255.0) & 0xFF);
                    }
                }

                // Call to encoding function : convert pixels to Yuv Binary data
                encodeYUV420(bufferIn, pixels, 320, 240);
                long startTime = System.currentTimeMillis();
                long numFrames = 0;
                double duration = 100.0;
                long sleepGrainularity =(long) duration;//milliseconds

                while(doEncode){

                    double timeStamp = System.currentTimeMillis()-startTime;
                    timeStamp*=1000;

                    cursor = 0;

                    double scaledTime = timeStamp * 0.00001;

                    long now = System.currentTimeMillis();
                    long delta = now - startTime;
                    double elapsed = numFrames * duration;

                    if(change) {

                        encodeYUV420(bufferIn, pixels, 320, 240);
                        change = false;

                    }else{
                        try {
                            Thread.sleep(sleepGrainularity);
                        } catch (InterruptedException ie) {

                        }
                    }

                    prepareFrame(bufferIn,bufferOut);
                    if(!doEncode)
                        break;

                    if(R5AudioController.getInstance().getAudioSampleTime()*1000>streamTime)
                        streamTime=R5AudioController.getInstance().getAudioSampleTime()*1000;
                    else
                        streamTime=(long)timeStamp;

                    encode(bufferOut,streamTime,false);
                }
            }
        };
        new Thread(engine,"video input").start();
    }

    @Override
    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void stopEncoding() {

        doEncode=false;
        engine=null;
    }
}
