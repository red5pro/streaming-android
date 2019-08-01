package red5pro.org.testandroidproject.tests.PublishCameraDeviceOrientationTest;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.streaming.source.R5Camera;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by toddanderson on 19/07/19.
 */
public class PublishCameraDeviceOrientationTest extends PublishTest {

    private static String TAG = "PCDOT";

    private int currentCamMode = Camera.CameraInfo.CAMERA_FACING_BACK;
    protected boolean mOrientationDirty;
    protected int camDisplayOrientation;
    protected int mOriginalCameraOrientation;
    View.OnLayoutChangeListener mLayoutListener;

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
    protected void attachCamera() {

//        Log.w(TAG, ">> attachCamera()");
//        String camString = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ? "front" : "back";
//        Log.d(TAG, "CameraMode: " + camString);

        cam = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ?
                openFrontFacingCameraGingerbread() : openBackFacingCameraGingerbread();
        cam.setDisplayOrientation(camDisplayOrientation);

        camera = new R5Camera(cam, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
        camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
        camera.setOrientation(camOrientation);
        camera.setFramerate(TestContent.GetPropertyInt("fps"));

        publish.attachCamera(camera);

//        Log.d(TAG, "CAMERA:setDisplayOrientation() :    " + camDisplayOrientation);
//        Log.d(TAG, "BROWSER:setOrientation() : " + camOrientation);
//        Log.d(TAG, "<< attachCamera()");

    }

    private boolean onPublishTouch(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_UP && publish != null) {
            R5Camera publishCam = (R5Camera)publish.getVideoSource();

            Camera newCam = null;
            camOrientation = camDisplayOrientation = 0;
            mOrientationDirty = false;

            //NOTE: Some devices will throw errors if you have a camera open when you attempt to open another
            publishCam.getCamera().stopPreview();
            publishCam.getCamera().release();

            //NOTE: The front facing camera needs to be 180 degrees further rotated than the back facing camera
            if (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                newCam = openBackFacingCameraGingerbread();
                if(newCam != null) {
                    currentCamMode = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
            } else {
                newCam = openFrontFacingCameraGingerbread();
                if(newCam != null) {
                    currentCamMode = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
            }

            if (newCam != null) {

                updateOrientationValues();
                newCam.setDisplayOrientation(camDisplayOrientation);

                publishCam.setCamera(newCam);
                publishCam.setOrientation(camOrientation);

                newCam.startPreview();

//                Log.d(TAG, "CAMERA:setDisplayOrientation() :    " + camDisplayOrientation);
//                Log.d(TAG, "BROWSER:setOrientation() : " + camOrientation);
//                Log.d(TAG, "<< onPublishTouch()");

                this.cam = newCam;
            }
        }

        return true;
    }

    @Override
    protected Camera openFrontFacingCameraGingerbread() {
//        Log.w(TAG, ">> openFrontFacingCameraGingerbread()");
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                    mOriginalCameraOrientation = cameraInfo.orientation;
                    camOrientation = cameraInfo.orientation;
//                    Log.w(TAG, "Original Orientation: " + mOriginalCameraOrientation);
                    applyDeviceRotation();
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
//        Log.w(TAG, "<< openFrontFacingCameraGingerbread()");
        return cam;
    }

    protected Camera openBackFacingCameraGingerbread() {
//        Log.w(TAG, ">> openBackFacingCameraGingerbread()");
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    cam = Camera.open(camIdx);
                    mOriginalCameraOrientation = cameraInfo.orientation;
                    camOrientation = cameraInfo.orientation;
//                    Log.w(TAG, "Original Orientation: " + mOriginalCameraOrientation);
                    applyInverseDeviceRotation();
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
//        Log.w(TAG, "<< openBackFacingCameraGingerbread()");
        return cam;
    }

    protected void applyDeviceRotation() {

        super.applyDeviceRotation();
//        Log.w(TAG, ">> applyDeviceRotation()");
        updateOrientationValues();
//        Log.d(TAG, "<< applyDeviceRotation()");

    }

    protected void applyInverseDeviceRotation() {

//        Log.w(TAG, ">> applyInverseDeviceRotation()");
        updateOrientationValues();
//        Log.d(TAG, "<< applyInverseDeviceRotation()");

    }

    protected void updateOrientationValues () {

//        Log.d(TAG, ">> updateOrientationValues()");
        boolean isFront = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT);
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

//        String camString = isFront ? "front" : "back";
//        Log.d(TAG, "CameraMode: " + camString);
//        Log.d(TAG, "Rotation: " + rotation);

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 90; break;
            case Surface.ROTATION_90: degrees = 0; break;
            case Surface.ROTATION_180: degrees = 270; break;
            case Surface.ROTATION_270: degrees = 180; break;
        }

        if (!isFront) {
            switch (mOriginalCameraOrientation) {
                case 0: break;
                case 180: break;
                case 90: break;
                case 270: degrees += 180; break;
            }
        }

        boolean isBackfacingOrNonPortrait = !isFront || (rotation % 2 != 0);

        camOrientation = isBackfacingOrNonPortrait ? degrees : (degrees + 180);
        camOrientation = camOrientation % 360;
        camDisplayOrientation = degrees % 360;

//        Log.w(TAG, "camOrientation: " + camOrientation);
//        Log.w(TAG, "camDisplayOrientation: " + camDisplayOrientation);
//        Log.w(TAG, "camOrignalOrientation: " + mOriginalCameraOrientation);
//        Log.d(TAG, "<< updateOrientationValues()");
    }

    protected void reorient() {

//        Log.w(TAG, ">> reorient()");
        String camString = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ? "front" : "back";
//        Log.d(TAG, "CameraMode: " + camString);

        cam.setDisplayOrientation(camDisplayOrientation);
        camera.setOrientation(camOrientation);
        mOrientationDirty = false;

//        Log.d(TAG, "CAMERA:setDisplayOrientation() :    " + camDisplayOrientation);
//        Log.d(TAG, "BROWSER:setOrientation() : " + camOrientation);
//        Log.d(TAG, "<< reorient()");

        //call for a redraw to fix the aspect
        publish.setScaleMode(0);
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

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

//        Log.d(TAG, ">> onConfigurationChanged()");
        updateOrientationValues();
        mOrientationDirty = true;
//        Log.d(TAG, "<< onConfigurationChanged()");
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

}
