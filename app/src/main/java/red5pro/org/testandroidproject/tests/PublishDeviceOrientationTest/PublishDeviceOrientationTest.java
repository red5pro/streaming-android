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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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
    View.OnLayoutChangeListener mLayoutListener;
    boolean mOrientationDirty;

    public PublishDeviceOrientationTest() {

    }

    protected void reorient() {
        cam.setDisplayOrientation((camOrientation + 180) % 360);
        camera.setOrientation(camOrientation);
        mOrientationDirty = false;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        int c_orientation = config.orientation;
        Log.w("PublishDeviceOrientTest", "config changed: " + c_orientation);
        Log.w("PublishDeviceOrientTest", "orientation: " + mOrientation);
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

        int d_rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Log.w("PublishDeviceOrientTest", "d_rotation: " + d_rotation);
        Log.w("PublishDeviceOrientTest", "value: " + value);

        int degrees = 0;
        switch (d_rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 0; break;
            case Surface.ROTATION_270: degrees = 90; break;
        }

        Log.w("PublishDeviceOrientTest", "degrees: " + degrees);
        camOrientation = (mOrigCamOrientation + degrees) % 360;
        mOrientationDirty = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        preview.addOnLayoutChangeListener(mLayoutListener);
        mOrientationListener.enable();
    }

    @Override
    public void onPause() {
        super.onPause();
        preview.removeOnLayoutChangeListener(mLayoutListener);
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

        mLayoutListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                Log.w("PublishDeviceOrientTest", "onLayoutChange");
                if (mOrientationDirty) {
                    Log.w("PublishDeviceOrientTest", "dirty orientation");
                    reorient();
                }
            }
        };

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    protected void applyDeviceRotation() {
        super.applyDeviceRotation();
        mOrigCamOrientation = camOrientation;
    }

}
