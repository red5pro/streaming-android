package red5pro.org.testandroidproject.tests.PublishDeviceOrientationTest;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;
import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/9/16.
 */
public class PublishDeviceOrientationTest extends PublishTest {

    protected int mOrigCamOrientation = 0;
    protected int mOrientation;
    OrientationEventListener mOrientationListener;

    public PublishDeviceOrientationTest() {

    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        int c_orientation = config.orientation;
        Log.d("PublishDeviceOrientTest", "config changed: " + c_orientation);
        Log.d("PublishDeviceOrientTest", "orientation: " + mOrientation);
        int value = mOrigCamOrientation;
        if (c_orientation == 2 && mOrientation >= 270 ) {
            value = 270;//(mOrigCamOrientation + 270) % 360;
        }
        else if (c_orientation == 2 && mOrientation < 270) {
            value = 180;// (mOrigCamOrientation + 90) % 360;
        }
        else if (c_orientation == 1 && mOrientation >= 90) {
            value = 90;//(mOrigCamOrientation + 180) % 360;
        }
        else {
            value = 0;//(mOrigCamOrientation + 180) % 360;
        }
        camOrientation = value;
        cam.setDisplayOrientation(camOrientation);
        //camera.setOrientation(camOrientation);
    }

    @Override
    public void onResume() {
        super.onResume();
        mOrientationListener.enable();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrientationListener.disable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mOrientationListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL ) {
            @Override
            public void onOrientationChanged(int orientation) {

                mOrientation = orientation;

            }

        };

        return super.onCreateView(inflater, container, savedInstanceState);

    }

//    protected Camera openFrontFacingCameraGingerbread() {
//        int cameraCount = 0;
//        Camera cam = null;
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        cameraCount = Camera.getNumberOfCameras();
//        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
//            Camera.getCameraInfo(camIdx, cameraInfo);
//            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                try {
//                    cam = Camera.open(camIdx);
//                    camOrientation = cameraInfo.orientation;
//                    applyDeviceRotation();
//                    break;
//                } catch (RuntimeException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return cam;
//    }

    protected void applyDeviceRotation() {
        super.applyDeviceRotation();
        mOrigCamOrientation = camOrientation;
    }

}
