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
package red5pro.org.testandroidproject.tests.PublishBackgroundTest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Surface;
import android.view.WindowManager;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.TestContent;

public class PublishService extends Service {

    private R5Stream publish;
    private R5VideoView preview;
    private Camera cam;
    private R5Camera camera;
    private int camOrientation;
    private Notification holderNote;
    private final PublishServiceBinder mBinder = new PublishServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        startPublish();

        super.onCreate();
    }

	@RequiresApi(Build.VERSION_CODES.O)
	private String createNotificationChannel(String channelId, String channelName) {
		NotificationChannel chan = new NotificationChannel(channelId,
			channelName, NotificationManager.IMPORTANCE_NONE);
		chan.setLightColor(Color.BLUE);
		chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		NotificationManager service = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		service.createNotificationChannel(chan);
		return channelId;
	}

    private R5Stream.RecordType getPublishRecordType () {
        String type = TestContent.GetPropertyString("record_mode");
        if (type.equals("Record")) {
            return R5Stream.RecordType.Record;
        } else if (type.equals("Append")) {
            return R5Stream.RecordType.Append;
        }
        return R5Stream.RecordType.Live;

    }

    private void startPublish(){
        if(publish != null){
            publish.stop();
        }

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID( getPackageName() );

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

        if(preview != null){
            attachDisplay();
        }

        if(TestContent.GetPropertyBool("video_on")) {
            //attach a camera video source

            cam = openFrontFacingCamera();
            cam.setDisplayOrientation((camOrientation + 180) % 360);

            camera = new R5Camera(cam, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
            camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
            camera.setOrientation(camOrientation);
            camera.setFramerate(TestContent.GetPropertyInt("fps"));
            publish.attachCamera(camera);
        }

        publish.publish(TestContent.GetPropertyString("stream1"), getPublishRecordType());

        if(TestContent.GetPropertyBool("video_on")) {
            cam.startPreview();
        }
    }

    private void attachDisplay(){

        preview.attachStream(publish);
        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));
    }

    public void setDisplay(R5VideoView view ){
        if(view == null){
            return;
        }
        else if(holderNote != null){
            setDisplayOn(true);
        }
        else {

            preview = view;

            if (publish != null) {
                attachDisplay();
            } else startPublish();
        }
    }

    public void setDisplayOn(boolean setOn){

        if(!setOn){
            publish.restrainVideo(true);
            cam.stopPreview();
            cam.release();
            camera.setCamera(null);
            cam = null;

			if(holderNote == null){
				Notification.Builder builder = null;
				String channelId = "";
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					channelId = createNotificationChannel("r5pro_service", "Red5 Pro Service");
					builder = new Notification.Builder(getApplicationContext(), channelId);
				} else {
					builder = new Notification.Builder(getApplicationContext());
				}

				if (builder != null) {
					holderNote = builder.setContentTitle("R5Testbed")
						.setContentText("Publishing from the background")
						.setSmallIcon(R.drawable.ic_launcher)
						.build();
					startForeground(57234111, holderNote);
				}
			}
        }
        else {
            if(holderNote != null){
                stopForeground(true);
                holderNote = null;
            }

            cam = openFrontFacingCamera();
            cam.setDisplayOrientation((camOrientation + 180) % 360);
            camera.setCamera(cam);
            camera.setOrientation(camOrientation);

            publish.restrainVideo(false);
            cam.startPreview();
        }
    }

    @Override
    public void onDestroy() {

        if(holderNote != null) {
            stopForeground(true);
            holderNote = null;
        }

        if(publish != null){
            publish.stop();
            publish = null;
        }
        if(cam != null) {
            cam.stopPreview();
            cam.release();
            cam = null;
        }

        super.onDestroy();
    }

    protected Camera openFrontFacingCamera() {
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
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        int rotation = window.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 270; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 90; break;
        }

        Rect screenSize = new Rect();
        window.getDefaultDisplay().getRectSize(screenSize);
        float screenAR = (screenSize.width()*1.0f) / (screenSize.height()*1.0f);
        if( (screenAR > 1 && degrees%180 == 0) || (screenAR < 1 && degrees%180 > 0) )
            degrees += 180;

        System.out.println("Apply Device Rotation: " + rotation + ", degrees: " + degrees);

        camOrientation += degrees;

        camOrientation = camOrientation%360;
    }

    class PublishServiceBinder extends Binder {
        PublishService getService() {
            return PublishService.this;
        }
    }
}
