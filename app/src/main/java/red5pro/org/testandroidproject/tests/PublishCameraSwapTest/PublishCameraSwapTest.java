package red5pro.org.testandroidproject.tests.PublishCameraSwapTest;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.red5pro.streaming.source.R5Camera;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

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
