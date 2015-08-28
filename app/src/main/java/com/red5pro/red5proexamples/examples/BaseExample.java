package com.red5pro.red5proexamples.examples;


import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import com.red5pro.red5proexamples.R;
import android.content.res.Resources;
import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by azupko on 7/23/15.
 */
public class BaseExample extends Fragment {


    protected R5Stream subscribe;
    protected R5Stream publish;

    public static boolean swapped = false;

    protected String getPublishStreamName(){
        if(!swapped) return getString(R.string.stream1);
        else return getString(R.string.stream2);
    }
    protected String getSubscribeStreamName(){
        if(!swapped) return getString(R.string.stream2);
        else return getString(R.string.stream1);
    }

    protected Camera cam;

    public BaseExample(){}

    protected R5Stream getNewStream(int type){


        Resources res = getResources();

        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
        R5Connection connection = new R5Connection(config);

        R5Stream stream = new R5Stream(connection);
        stream.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        if(type == 1) { //publishing

            cam = openFrontFacingCameraGingerbread();
            cam.setDisplayOrientation(90);

            R5Camera camera  = new R5Camera(cam, 320, 240);
            camera.setBitrate(res.getInteger(R.integer.bitrate));
            camera.setOrientation(-90);
            R5Microphone mic = new R5Microphone();

            stream.attachMic(mic);
            stream.attachCamera(camera);
        }

        return stream;

    }

    protected Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("R5 Test Activity", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }


    @Override
    public void onStop(){

        super.onStop();

        if(publish != null)
            publish.stop();

        if(subscribe != null)
            subscribe.stop();

        publish = subscribe = null;

        if(cam != null) {
            cam.stopPreview();
            cam.release();
        }

    }

}
