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
package red5pro.org.testandroidproject.tests.SubscribeRendererRGBScalarTest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamFormat;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5FrameListener;
import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by toddanderson on 08/30/2018.
 */
public class SubscribeRendererRGBScalarTest extends SubscribeTest {

   @Override
   public void Subscribe(){

       //Create the configuration from the tests.xml
       R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
               TestContent.GetPropertyString("host"),
               TestContent.GetPropertyInt("port"),
               TestContent.GetPropertyString("context"),
               TestContent.GetPropertyFloat("subscribe_buffer_time"));
       config.setLicenseKey(TestContent.GetPropertyString("license_key"));
       config.setBundleID(getActivity().getPackageName());

	   String params = TestContent.getConnectionParams();
	   if (params != null) {
		   config.setParameters(params);
	   }

       R5Connection connection = new R5Connection(config);

       //setup a new stream using the connection
       subscribe = new R5Stream(connection);

       //Some devices can't handle rapid reuse of the audio controller, and will crash
       //Recreation of the controller assures that the example will always be stable
       subscribe.audioController = new R5AudioController();
       subscribe.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

       subscribe.client = this;
       subscribe.setListener(this);

       //show all logging
       subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

       //display.setZOrderOnTop(true);
       display.attachStream(subscribe);

       display.showDebugView(TestContent.GetPropertyBool("debug_view"));

       subscribe.playWithForcedRGBDecode(TestContent.GetPropertyString("stream1"));
   }

   @Override
   public void onConnectionEvent(R5ConnectionEvent event) {
       Log.d("Subscriber", ":onConnectionEvent " + event.name());
       super.onConnectionEvent(event);
       if (event.name() == R5ConnectionEvent.LICENSE_ERROR.name()) {
           Handler h = new Handler(Looper.getMainLooper());
           h.post(new Runnable() {
               @Override
               public void run() {
                   AlertDialog alertDialog = new AlertDialog.Builder(SubscribeRendererRGBScalarTest.this.getActivity()).create();
                   alertDialog.setTitle("Error");
                   alertDialog.setMessage("License is Invalid");
                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK",
                           new DialogInterface.OnClickListener()

                           {
                               public void onClick (DialogInterface dialog,int which){
                                   dialog.dismiss();
                               }
                           }

                   );
                   alertDialog.show();
               }
           });
       }
       else if (event.name() == R5ConnectionEvent.START_STREAMING.name()){
//            subscribe.setFrameListener(new R5FrameListener() {
//                @Override
//                public void onFrameReceived(Object o, R5StreamFormat r5StreamFormat, int w, int h) {
//                    int format = r5StreamFormat.value(); // 1 - RGB
//                    if (r5StreamFormat.equals(R5StreamFormat.RGB)) {
//                        byte[] rgb_frame = (byte[]) o; // Cast and access data plane as byte array.
//                    }
//                }
//            });
       }
   }

}
