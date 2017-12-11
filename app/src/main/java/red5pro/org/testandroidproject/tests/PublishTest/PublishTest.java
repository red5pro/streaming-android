package red5pro.org.testandroidproject.tests.PublishTest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.PublishTestListener;
import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/9/16.
 */
public class PublishTest extends TestDetailFragment implements R5ConnectionListener {
    protected R5VideoView preview;
    protected R5Stream publish;
    protected Camera cam;
    protected R5Camera camera;
    protected int camOrientation;

    protected PublishTestListener publishTestListener;

    public PublishTest(){

    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent event) {
        Log.d("Publisher", ":onConnectionEvent " + event.name());
        if (event.name() == R5ConnectionEvent.LICENSE_ERROR.name()) {
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                       @Override
                       public void run() {
                           AlertDialog alertDialog = new AlertDialog.Builder(PublishTest.this.getActivity()).create();
                           alertDialog.setTitle("Error");
                           alertDialog.setMessage("License is Invalid");
                           alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK",
                                   new DialogInterface.OnClickListener()

                                   {
                                       public void onClick (DialogInterface dialog,int which){
                                           dialog.dismiss();
                                       }
                                   }

                           );
                           alertDialog.show();
                       }
                   });
        }
        if (event.name() == R5ConnectionEvent.START_STREAMING.name()){
//            publish.setFrameListener(new R5FrameListener() {
//                @Override
//                public void onBytesReceived(byte[] bytes, int i, int i1) {
//                    Uncomment for framelistener performance test
//                }
//            });
        }
        else if (event.name() == R5ConnectionEvent.BUFFER_FLUSH_START.name()) {
            if (publishTestListener != null) {
                publishTestListener.onPublishFlushBufferStart();
            }
        }
        else if (event.name() == R5ConnectionEvent.BUFFER_FLUSH_EMPTY.name() ||
                    event.name() == R5ConnectionEvent.DISCONNECTED.name()) {
            if (publishTestListener != null) {
                publishTestListener.onPublishFlushBufferComplete();;
                publishTestListener = null;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.publish_test, container, false);

        preview = (R5VideoView)rootView.findViewById(R.id.videoPreview);

        publish();

        return rootView;
    }
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
            cam = openFrontFacingCameraGingerbread();
            cam.setDisplayOrientation((camOrientation + 180) % 360);

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
                    camOrientation = cameraInfo.orientation;
                    applyDeviceRotation();
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

        return cam;
    }

    protected void applyDeviceRotation(){
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 270; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 90; break;
        }

        Rect screenSize = new Rect();
        getActivity().getWindowManager().getDefaultDisplay().getRectSize(screenSize);
        float screenAR = (screenSize.width()*1.0f) / (screenSize.height()*1.0f);
        if( (screenAR > 1 && degrees%180 == 0) || (screenAR < 1 && degrees%180 > 0) )
            degrees += 180;

        System.out.println("Apply Device Rotation: " + rotation + ", degrees: " + degrees);

        camOrientation += degrees;

        camOrientation = camOrientation%360;
    }

    public void stopPublish(PublishTestListener listener) {

        publishTestListener = listener;
        if (publish != null) {
            publish.stop();

            if(publish.getVideoSource() != null) {
                Camera c = ((R5Camera) publish.getVideoSource()).getCamera();
                c.stopPreview();
                c.release();
            }
            publish = null;
        }

    }

    @Override
    public Boolean isPublisherTest () {
        return true;
    }

}
