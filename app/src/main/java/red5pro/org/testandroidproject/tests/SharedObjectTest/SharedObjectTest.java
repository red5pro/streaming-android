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
package red5pro.org.testandroidproject.tests.SharedObjectTest;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5SharedObject;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.view.R5VideoView;

import org.apache.mina.proxy.utils.StringUtilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import red5pro.org.testandroidproject.PublishTestListener;
import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 3/1/17.
 */

public class SharedObjectTest extends PublishTest{
    protected Thread callThread;
    private R5Stream stream;
    protected R5SharedObject sObject;
    protected TextView chatView;
    protected EditText chatInput;
    protected int maxMessages = 20;
    protected Button chatSend;
    protected ArrayList<String> messageBuffer;
    protected String thisUser = "subscriber-";
    protected boolean useHTTPS = false;

    protected Button redButton;
    protected Button greenButton;
    protected Button blueButton;
    protected Button blackButton;

    protected View.OnClickListener colorPicker = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (sObject == null) return;

            Drawable background = view.getBackground();
            int color = Color.BLACK;
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }
            String hexStr = "#"+Integer.toHexString(color).substring(2);
            sObject.setProperty("color", hexStr);
            setChatViewToHex(hexStr);
        }
    };

    protected void assignColorPickerHandler (View root) {
        redButton = (Button)root.findViewById(R.id.redButton);
        greenButton = (Button)root.findViewById(R.id.greenButton);
        blueButton = (Button)root.findViewById(R.id.blueButton);
        blackButton = (Button)root.findViewById(R.id.blackButton);
        redButton.setOnClickListener(colorPicker);
        greenButton.setOnClickListener(colorPicker);
        blueButton.setOnClickListener(colorPicker);
        blackButton.setOnClickListener(colorPicker);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.shared_object_test, container, false);

        preview = (R5VideoView)rootView.findViewById(R.id.videoPreview);
        chatView = (TextView)rootView.findViewById(R.id.chatView);
        chatInput = (EditText)rootView.findViewById(R.id.chatInput);
        chatSend = (Button)rootView.findViewById(R.id.chatSend);
        chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        assignColorPickerHandler(rootView);

        messageBuffer = new ArrayList<String>();

        addMessage("Waiting for Stream Connection");

        makeListCall();

        return rootView;
    }

    @Override
    protected void publish() {
        super.publish();
        stream = publish;
    }

    private void subscribe(){
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
        stream = new R5Stream(connection);

        stream.audioController = new R5AudioController();
        stream.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        stream.client = this;
        stream.setListener(this);

        //show all logging
        stream.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //display.setZOrderOnTop(true);
        preview.attachStream(stream);

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        stream.play(TestContent.GetPropertyString("stream1"), TestContent.GetPropertyBool("hwAccel_on"));
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        if(r5ConnectionEvent.value() == R5ConnectionEvent.START_STREAMING.value()){
            final SharedObjectTest safeThis = this;
            callThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(250);
                        //start the shared object call
                        sObject = new R5SharedObject("sharedChatTest", stream.connection);
                        sObject.client = safeThis;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            callThread.start();
        }
    }

    protected void setChatViewToHex (final String hexStr) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatView.setTextColor(Color.parseColor(hexStr));
            }
        });
    }

    //callback for remote object connection - remote object now available
    public void onSharedObjectConnect(JSONObject objectValue) {
        addMessage("Connected to object.");
        try {
            JSONObject data = sObject.getData();
            String colorStr = data.has("color") ? data.getString("color") : "#000000";
            setChatViewToHex(colorStr);
        } catch (JSONException e) {}
    }

    //Called whenever a property of the shared object is changed
    public void onUpdateProperty(JSONObject propertyInfo){
//        propertyInfo.keys().next() can be used to find which property has updated.
        try {
            String hexString = propertyInfo.getString("color");
            setChatViewToHex(hexString);
        } catch (JSONException e) {}
    }

    public void sendMessage(){
        String chatInText = chatInput.getText().toString();
        if( chatInText.isEmpty() )
            return;

        JSONObject messageOut = new JSONObject();
        try {
            messageOut.put("user", thisUser);
            messageOut.put("message", chatInText);
        } catch (JSONException e) {}

        //Calls for the relevant method with the sent parameters on all clients listening to the shared object
        //Note - This includes the client that sends the call
        if (sObject != null) {
            sObject.send("messageTransmit", messageOut);
            chatInput.setText("");
        }

    }

    public void messageTransmit( JSONObject messageIn ){

        String user, message;
        try {
            user = messageIn.getString("user");
            message = messageIn.getString("message");
        } catch (JSONException e) { return; }

        String display = "User " + user + ": " + message;

        addMessage(display);
    }

    private void makeListCall(){

        addMessage("Retrieved list");

        final String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
        String protocol = (port.isEmpty() || port.equals("443")) ? "https" : "http";
        final String urlStr = protocol + "://" + TestContent.GetPropertyString("host") + port + "/" + TestContent.GetPropertyString("context") + "/streams.jsp";

        if(callThread != null) {
            callThread.interrupt();
            callThread = null;
        }

        callThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    String responseString = "error: somehow string not assigned to?";
                    try {
                        final int code = urlConnection.getResponseCode();
                        Boolean isInterrupted = Thread.interrupted();
                        if (code == 200 && !isInterrupted) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            responseString = stringBuilder.toString().replaceAll("\\s+", "");
                            bufferedReader.close();
                        } else {
                            responseString = "error: http issue, response code - " + urlConnection.getResponseCode();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        if(e.getLocalizedMessage().contains("Cleartext HTTP traffic")){
                            useHTTPS = true;
                            addMessage("Retrying with https");
                        }
                    }finally {
                        urlConnection.disconnect();
                    }

                    if(!Thread.interrupted()) {
                        if (!responseString.startsWith("error")) {
                            boolean willPublish = true;
                            JSONArray list = new JSONArray(responseString);

                            for (int i = 0; i < list.length(); i++) {
                                if (list.getJSONObject(i).getString("name").equals( TestContent.GetPropertyString("stream1") )) {
                                    willPublish = false;
                                    break;
                                }
                            }
                            final boolean willPublishFinal = willPublish;

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(willPublishFinal){
                                        publish();
                                        addMessage("Begin publish");
                                    }
                                    else {
                                        subscribe();
                                        addMessage("Begin subscribe");
                                    }
                                }
                            });

                        } else {
                            System.out.println(responseString);
                            Thread.sleep(1000);
                            makeListCall();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        callThread.start();
    }

    public void addMessage(final String message){

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        String dateString = format.format(new Date(System.currentTimeMillis()));
        messageBuffer.add(dateString + " " + message);
        chatUpdate();
    }

    public void chatUpdate(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                while (messageBuffer.size() > maxMessages){
                    messageBuffer.remove(0);
                }

                String textOut = "";

                for ( String line:messageBuffer ) {
                    textOut += line + "\n";
                }

                chatView.setText( textOut );
            }
        });
    }

    @Override
    public void stopPublish(PublishTestListener listener) {
        if(stream == publish){
            stream = null;
        }
        super.stopPublish(listener);
    }

    @Override
    public void onStop() {
        if(callThread != null){
            callThread.interrupt();
            callThread = null;
        }

        if(sObject != null){
            sObject.close();
            sObject.client = null;
            sObject = null;
        }

        if(stream != null){
            stream.setListener(null);
            stream.stop();
            stream = null;
        }

        super.onStop();
    }

}
