//
// Copyright © 2015 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package red5pro.org.testandroidproject.tests.PublishCameraSwapTest;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.red5pro.streaming.source.R5Camera;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class PublishCameraSwapTest extends PublishTest {

    protected int currentCamMode = Camera.CameraInfo.CAMERA_FACING_BACK;

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
    protected void attachCamera(){
        int rotate = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ? 180 : 0;
        cam = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ?
                openFrontFacingCameraGingerbread() : openBackFacingCameraGingerbread();
        cam.setDisplayOrientation((camOrientation + rotate) % 360);

        camera = new R5Camera(cam, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
        camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
        camera.setOrientation(camOrientation);
        camera.setFramerate(TestContent.GetPropertyInt("fps"));
        publish.attachCamera(camera);
    }

    protected boolean onPublishTouch( MotionEvent e ) {

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
