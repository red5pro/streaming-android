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
package red5pro.org.testandroidproject.tests.SubscribeRemoteCallTest;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.util.Hashtable;

import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;

/**
 * Created by davidHeimann on 4/26/16.
 */
public class SubscribeRemoteCallTest extends SubscribeTest {
    private TextView messageView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscribe.client = this;
    }

    public void whateverFunctionName( String message ){

        System.out.println("Recieved message from publisher: " + message);

        String[] parsedMessage = message.split(";");
        Hashtable<String, String> map = new Hashtable<String, String>();
        for (String s : parsedMessage) {
            String key = s.split("=")[0];
            String value = s.split("=")[1];
            System.out.println("Received key: " + key + "; with value: " + value);

            map.put(key,value);
        }

        final Hashtable<String, String> mapFinal = map;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (messageView == null) {
                    messageView = new TextView(display.getContext());
                    ((FrameLayout) display.getParent()).addView(messageView);
                    messageView.setBackgroundColor(Color.LTGRAY);
                }

                if (mapFinal.containsKey("message")) {
                    messageView.setText(mapFinal.get("message"));
                }

                FrameLayout.LayoutParams position = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (mapFinal.containsKey("touchX")) {
                    position.leftMargin = (int) (Float.parseFloat(mapFinal.get("touchX")) * display.getWidth());
                }
                if (mapFinal.containsKey("touchY")) {
                    position.topMargin = (int) (Float.parseFloat(mapFinal.get("touchY")) * display.getHeight());
                }
                messageView.setLayoutParams(position);
            }
        });
    }
}
