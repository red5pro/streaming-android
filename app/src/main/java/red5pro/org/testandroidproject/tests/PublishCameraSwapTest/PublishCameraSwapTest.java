package red5pro.org.testandroidproject.tests.PublishCameraSwapTest;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class PublishCameraSwapTest extends PublishTest {

    private int currentCamMode = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onPublishTouch(event);
            }
        });
    }

    @Override
    protected void publish(){
        String b = getActivity().getPackageName();

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(b);

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);

        publish.audioController.sampleRate =  TestContent.GetPropertyInt("sample_rate");

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        if(TestContent.GetPropertyBool("video_on")) {
            //attach a camera video source
            int rotate = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ? 180 : 0;
            cam = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ?
                    openFrontFacingCameraGingerbread() : openBackFacingCameraGingerbread();
            cam.setDisplayOrientation((camOrientation + rotate) % 360);

            camera = new R5Camera(cam, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
            camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
            camera.setOrientation(camOrientation);
            camera.setFramerate(TestContent.GetPropertyInt("fps"));
        }

        if(TestContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            R5Microphone mic = new R5Microphone();
            publish.attachMic(mic);
        }

        preview.attachStream(publish);

        if(TestContent.GetPropertyBool("video_on"))
            publish.attachCamera(camera);

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.setListener(this);
        publish.publish(TestContent.GetPropertyString("stream1"), R5Stream.RecordType.Live);

        if(TestContent.GetPropertyBool("video_on")) {
            cam.startPreview();
        }

    }

    private boolean onPublishTouch( MotionEvent e ) {

        if( e.getAction() == MotionEvent.ACTION_UP && publish != null) {
            R5Camera publishCam = (R5Camera)publish.getVideoSource();

            Camera newCam = null;

            //NOTE: Some devices will throw errors if you have a camera open when you attempt to open another
            publishCam.getCamera().stopPreview();
            publishCam.getCamera().release();

            //NOTE: The front facing camera needs to be 180 degrees further rotated than the back facing camera
            int rotate = 0;
            if( currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT ) {
                newCam = openBackFacingCameraGingerbread();
                rotate = 0;
                if(newCam != null)
                    currentCamMode = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            else{
                newCam = openFrontFacingCameraGingerbread();
                rotate = 180;
                if(newCam != null)
                    currentCamMode = Camera.CameraInfo.CAMERA_FACING_FRONT;
            }

            if( newCam != null ){

                newCam.setDisplayOrientation( (camOrientation + rotate) %360 );

                publishCam.setCamera(newCam);
                publishCam.setOrientation( camOrientation );

                newCam.startPreview();
            }
        }

        return true;
    }

    protected Camera openBackFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        System.out.println( "Number of cameras: " + cameraCount );
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    cam = Camera.open(camIdx);
                    camOrientation = cameraInfo.orientation;
                    applyInverseDeviceRotation();
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

        return cam;
    }

    protected void applyInverseDeviceRotation(){
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 270; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 90; break;
        }

        camOrientation += degrees;

        camOrientation = camOrientation%360;
    }
}
