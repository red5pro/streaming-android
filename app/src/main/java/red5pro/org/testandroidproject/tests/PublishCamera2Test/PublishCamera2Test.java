package red5pro.org.testandroidproject.tests.PublishCamera2Test;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Camera2;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by dheimann on 3/24/17.
 */

@TargetApi(22)
public class PublishCamera2Test extends TestDetailFragment implements R5ConnectionListener {
    protected R5VideoView preview;
    protected R5Stream publish;
    private R5Camera2 camera2;
    private CameraDevice camera;
    private CameraCharacteristics camInfo;
    protected int camOrientation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.publish_test, container, false);

        preview = (R5VideoView)rootView.findViewById(R.id.videoPreview);

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

        if(TestContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            R5Microphone mic = new R5Microphone();
            publish.attachMic(mic);
        }

        preview.attachStream(publish);


        CameraManager manager = (CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] camList = manager.getCameraIdList();
            for(String id : camList){
                CameraCharacteristics info = manager.getCameraCharacteristics(id);
                if(info.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT){
                    camOrientation = info.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    camInfo = info;
                    manager.openCamera(id, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            if(preview == null)
                                return;
                            startPublish(camera);
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {}

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {}
                    }, null);
                    break;
                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    public void startPublish(CameraDevice device){

        camera = device;

        camera2 = new R5Camera2(camera, camInfo, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
        camera2.setBitrate(TestContent.GetPropertyInt("bitrate"));
        camera2.setOrientation(camOrientation);
        camera2.setFramerate(TestContent.GetPropertyInt("fps"));

        publish.attachCamera(camera2);

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.setListener(this);
        publish.publish(TestContent.GetPropertyString("stream1"), getPublishRecordType());
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent event) {
        Log.d("Publisher", ":onConnectionEvent " + event.name());
        if (event.name() == R5ConnectionEvent.START_STREAMING.name()){

        }
    }

    @Override
    public void onStop() {

        preview = null;

        if(publish != null){
            publish.stop();

            if(camera != null){
                camera.close();
                camera = null;
            }

            publish = null;
        }

        super.onStop();
    }
}
