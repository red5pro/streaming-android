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
package red5pro.org.testandroidproject.tests.PublishPauseTest;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.util.Hashtable;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

public class PublishPauseTest extends PublishTest {

    protected int muteEnum = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        publish.setListener(this);
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        super.onConnectionEvent(r5ConnectionEvent);
        if(r5ConnectionEvent == R5ConnectionEvent.START_STREAMING ) {
            preview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        muteEnum = muteEnum + 1;
                        if (muteEnum > 3) {
                            muteEnum = 0;
                        }

                        switch (muteEnum) {
                            case 1:
                                // mute audio
                                publish.restrainAudio(true);
                                publish.restrainVideo(false);
                                Log.d("PublisherPause", "Mute Audio");
                                break;
                            case 2:
                                // mute video
                                publish.restrainAudio(false);
                                publish.restrainVideo(true);
                                Log.d("PublisherPause", "Mute Video");
                                break;
                            case 3:
                                // mute audio & video
                                publish.restrainAudio(true);
                                publish.restrainVideo(true);
                                Log.d("PublisherPause", "Mute Audio & Video");
                                break;
                            case 0:
                                // unmute audio & video
                                publish.restrainAudio(false);
                                publish.restrainVideo(false);
                                Log.d("PublisherPause", "Umute all");
                                break;
                        }

                    }

                    return true;
                }
            });
        }
    }
}
