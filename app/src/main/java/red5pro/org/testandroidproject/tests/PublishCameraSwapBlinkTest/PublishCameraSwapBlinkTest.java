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
package red5pro.org.testandroidproject.tests.PublishCameraSwapBlinkTest;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.source.R5Camera;

import red5pro.org.testandroidproject.tests.PublishCameraSwapTest.PublishCameraSwapTest;
import red5pro.org.testandroidproject.tests.TestContent;

public class PublishCameraSwapBlinkTest extends PublishCameraSwapTest {

    boolean swapping = false;

    @Override
    protected boolean onPublishTouch(final MotionEvent e) {

        if( e.getAction() != MotionEvent.ACTION_UP || publish == null) {
            return true;
        }

        if(swapping) return true;
        swapping = true;

        ((R5BlinkCamera)camera).coverCam = true;

        // Wait for two frames, guarantees at least one black frame is sent pre- and post- swap
        final int waitMS = 2000/camera.getFramerate();

        System.out.println(" TESTING - On Publish Touch - " + waitMS + "ms set");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(waitMS);
                }catch (Exception e){
                    return;
                }

                Handler mainLoop = new Handler(Looper.getMainLooper());
                mainLoop.post(new Runnable() {
                    @Override
                    public void run() {

                        if(publish == null){
                            return;
                        }

                        PublishCameraSwapBlinkTest.super.onPublishTouch(e);

                        System.out.println(" TESTING - On Publish Touch - swapping");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Thread.sleep(waitMS);
                                } catch (Exception e) {
                                    return;
                                }
                                if(publish == null){
                                    return;
                                }

                                ((R5BlinkCamera)camera).coverCam = false;

                                System.out.println(" TESTING - On Publish Touch - done");

                                swapping = false;
                            }
                        }).start();
                    }
                });
            }
        }).start();


        return true;
    }

    // Only need to override the camera assignment to the custom class below, otherwise identical to parent function
    @Override
    protected void attachCamera(){
        int rotate = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ? 180 : 0;
        cam = (currentCamMode == Camera.CameraInfo.CAMERA_FACING_FRONT) ?
                openFrontFacingCameraGingerbread() : openBackFacingCameraGingerbread();
        cam.setDisplayOrientation((camOrientation + rotate) % 360);

        camera = new R5BlinkCamera(cam, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
        camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
        camera.setOrientation(camOrientation);
        camera.setFramerate(TestContent.GetPropertyInt("fps"));
        publish.attachCamera(camera);
    }
}

// Just muting the video doesn't clear the last drawn frame, so it would still be upside-down on the metadata update
// This class not only presents a black frame as needed, but also removes the need for the extra metadata packets from muting
// The black frame could also be replaced with any YUV image data (appropriately scaled and/or padded to match the dimensions)
// Just note that for this use case, the image should make sense both right-side-up and up-side-down
class R5BlinkCamera extends R5Camera {

    boolean coverCam = false;
    private byte[] blackData = null;

    R5BlinkCamera(Camera cam, int widthIn, int heightIn){
        super(cam, widthIn, heightIn);
    }

    @Override
    protected void initSource() {
        super.initSource();

        // Building the blank frame here so that the width and height are corrected to what the camera supports
        if(blackData == null) {
            int frameSize = width * height;
            blackData = new byte[(int) (frameSize * 1.5)];
            for (int i = 0; i < (int) (frameSize * 1.5); i++) {
                if (i < frameSize) blackData[i] = (byte) 0;
                else blackData[i] = (byte) 128; // All zeros would be green in YUV, 0/128/128 is black
            }
        }
    }

    @Override
    public synchronized void encode(byte[] input, long time, boolean reset) {
        // Replacing data here instead of onPreviewFrame to prevent interrupting the output buffer swaps
        if(coverCam){
            input = blackData;
        }

        super.encode(input, time, reset);
    }
}
