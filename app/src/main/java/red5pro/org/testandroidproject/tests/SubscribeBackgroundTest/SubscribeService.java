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
package red5pro.org.testandroidproject.tests.SubscribeBackgroundTest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 12/6/17.
 */

public class SubscribeService extends Service {

    private R5Stream subscribe;
    private R5VideoView display;
    private Notification holderNote;
    private final SubscribeServiceBinder mBinder = new SubscribeServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        startSubscribe();

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

    public void startSubscribe(){

        if( subscribe != null){
            subscribe.stop();
        }

        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("subscribe_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID( getPackageName() );

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        if(display != null) {
            attachDisplay();
        }

        subscribe.audioController = new R5AudioController();
        subscribe.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        subscribe.play(TestContent.GetPropertyString("stream1"), TestContent.GetPropertyBool("hwAccel_on"));
    }

    private void attachDisplay(){
        display.attachStream(subscribe);
        display.showDebugView(TestContent.GetPropertyBool("debug_view"));
    }

    public void setDisplay( R5VideoView view ){
        if(view == null){
            return;
        }
        else {

            display = view;

            if (subscribe != null) {
                attachDisplay();
            } else startSubscribe();
        }
    }

    public void setDisplayOn(boolean setOn){

        if(!setOn){
            subscribe.deactivate_display();

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
						.setContentText("Streaming from the background")
						.setSmallIcon(R.drawable.ic_launcher)
						.build();
					startForeground(7335776, holderNote);
				}
            }
        }
        else {
            if(holderNote != null){
                stopForeground(true);
                holderNote = null;
            }

            subscribe.activate_display();
        }
    }

    @Override
    public void onDestroy() {

        if(holderNote != null) {
            stopForeground(true);
            holderNote = null;
        }

        if(subscribe != null){
            subscribe.stop();
        }

        super.onDestroy();
    }

    class SubscribeServiceBinder extends Binder {
        SubscribeService getService(){
            return SubscribeService.this;
        }
    }
}
