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
package red5pro.org.testandroidproject.tests.SharedObjectStreamlessTest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5SharedObject;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.SharedObjectTest.SharedObjectTest;
import red5pro.org.testandroidproject.tests.TestContent;

public class SharedObjectStreamlessTest extends SharedObjectTest {
    private R5Connection connection;

    protected EditText roomInput;
    protected Button connectButton;
    protected Boolean SOConnected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.shared_object_streamless_test, container, false);

        chatView = (TextView)rootView.findViewById(R.id.message_view);
        chatInput = (EditText)rootView.findViewById(R.id.message_input);
        chatSend = (Button)rootView.findViewById(R.id.send_button);
        chatSend.setEnabled(false);

        roomInput = (EditText)rootView.findViewById(R.id.room_input);
        connectButton = (Button)rootView.findViewById(R.id.connect_button);
        connectButton.setEnabled(false);

        assignColorPickerHandler(rootView);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomConnectDisconnect();
            }
        });
        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        messageBuffer = new ArrayList<String>();

        addMessage("Waiting for Stream Connection");

        r5connect();

        return rootView;
    }

    public void r5connect(){
        String b = getActivity().getPackageName();

        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(b);

		String params = TestContent.getConnectionParams();
		if (params != null) {
			config.setParameters(params);
		}

        connection = new R5Connection(config);

        connection.addListener(this);
        connection.startDataOnlyStream();
    }

    protected void roomConnectDisconnect () {
        if (!SOConnected) {
            final SharedObjectTest safeThis = this;
            callThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(250);
                        //start the shared object call
                        System.out.println("Sending call for shared object");
                        sObject = new R5SharedObject(roomInput.getText().toString(), connection);
                        sObject.client = safeThis;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        SOConnected = false;
                    }
                }
            });
            callThread.start();
        } else if (sObject != null) {
            sObject.close();
            sObject.client = null;
            sObject = null;
            SOConnected = false;
            addMessage("Disconnected from " + roomInput.getText() + ".");
        }
        connectButton.setText(SOConnected ? "Disconnect" : "Connect");
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
        if(r5ConnectionEvent.value() == R5ConnectionEvent.START_STREAMING.value()){
            Handler h = new Handler(Looper.getMainLooper());
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectButton.setEnabled(true);
                }
            }, 250);
        } else if (r5ConnectionEvent.value() == R5ConnectionEvent.CLOSE.value() ||
                r5ConnectionEvent.value() == R5ConnectionEvent.DISCONNECTED.value()) {
          SOConnected = false;
        } else {
            System.out.println("Event - " + r5ConnectionEvent.name() + " - " + r5ConnectionEvent.message);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        chatSend.setEnabled(false);
        if(connection != null){
            connection.stopDataOnlyStream();
            connection = null;
        }
    }

    @Override  //callback for remote object connection - remote object now available
    public void onSharedObjectConnect(JSONObject objectValue){

        SOConnected = true;
        addMessage("Connected to " + roomInput.getText().toString() + ".");
        thisUser ="subscriber-"+Integer.toString((int)Math.floor(Math.random()*0x10000),16);

        try {
            JSONObject data = sObject.getData();
            String colorStr = data.has("color") ? data.getString("color") : "#000000";
            setChatViewToHex(colorStr);
        } catch (JSONException e) {}

        Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectButton.setText(SOConnected ?"Disconnect":"Connect");
                chatSend.setEnabled(true);
            }
        }, 500);
    }
}
