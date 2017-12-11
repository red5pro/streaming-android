package red5pro.org.testandroidproject.tests.PublishDeviceOrientationTest;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

/**
 * Created by davidHeimann on 2/9/16.
 */
public class PublishDeviceOrientationTest extends PublishTest {

    boolean mOrientationDirty;
    protected int mOrigCamOrientation = 0;
    View.OnLayoutChangeListener mLayoutListener;
    OrientationEventListener mOrientListener;

    protected int camDisplayOrientation;

    public PublishDeviceOrientationTest() {

    }

    protected void reorient() {
        cam.setDisplayOrientation((camDisplayOrientation + 180) % 360);
        camera.setOrientation(camOrientation);
        mOrientationDirty = false;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        int d_rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Log.w("PublishDeviceOrientTest", "d_rotation: " + d_rotation);

        int degrees = 0;
        switch (d_rotation) {
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

        Log.w("PublishDeviceOrientTest", "degrees: " + degrees);
        camDisplayOrientation = (mOrigCamOrientation + degrees) % 360;
        camOrientation = d_rotation % 2 != 0 ? camDisplayOrientation - 180 : camDisplayOrientation;
        mOrientationDirty = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        preview.addOnLayoutChangeListener(mLayoutListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        preview.removeOnLayoutChangeListener(mLayoutListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        mOrigCamOrientation = camOrientation;
        super.applyDeviceRotation();
    }

}
