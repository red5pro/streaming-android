package red5pro.org.testandroidproject.tests.SharedObjectTest;

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

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 3/1/17.
 */

public class SharedObjectTest extends PublishTest{
    private Thread callThread;
    private R5Stream stream;
    private R5SharedObject sObject;
    private TextView chatView;
    private EditText chatInput;
    private Button chatSend;
    private ArrayList<String> messageBuffer;
    private int thisUser;

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

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        stream = new R5Stream(connection);

        stream.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        stream.client = this;
        stream.setListener(this);

        //show all logging
        stream.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //display.setZOrderOnTop(true);
        preview.attachStream(stream);

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        stream.audioController = new R5AudioController();

        stream.play(TestContent.GetPropertyString("stream1"));
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        if(r5ConnectionEvent.name() == R5ConnectionEvent.START_STREAMING.name()){
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

    //callback for remote object connection - remote object now available
    public void onSharedObjectConnect(JSONObject objectValue){
        try {
            addMessage("Connected to object, there are " + ((objectValue.has("count")) ? objectValue.getInt("count") : "no") + " other people connected");
            thisUser = (objectValue.has("count") ? objectValue.getInt("count") + 1 : 1 );
            //set the count property to add yourself
            sObject.setProperty("count", thisUser);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    //Called whenever a property of the shared object is changed
    public void onUpdateProperty(JSONObject propertyInfo){
//        propertyInfo.keys().next() can be used to find which property has updated.
        try {
            addMessage("Room update - There are now " + propertyInfo.getInt("count") + " users");
        } catch (JSONException e) {}
    }

    public void sendMessage(){
        String chatInText = chatInput.getText().toString();
        if( chatInText.isEmpty() )
            return;

        JSONObject messageOut = new JSONObject();
        try {
            messageOut.put("user", "" + thisUser);
            messageOut.put("message", chatInText);
        } catch (JSONException e) {}

        //Calls for the relevant method with the sent parameters on all clients listening to the shared object
        //Note - This includes the client that sends the call
        sObject.send("messageTransmit", messageOut);

        chatInput.setText("");
    }

    public void messageTransmit( JSONObject messageIn ){

        String user, message;
        try {
            user = messageIn.getString("user");
            message = messageIn.getString("message");
        } catch (JSONException e) { return; }

        String display = "user#" + user + ": " + message;

        addMessage(display);
    }

    private void makeListCall(){

        addMessage("Retrieved list");

        final String port = TestContent.getFormattedPortSetting(TestContent.GetPropertyString("server_port"));
        final String urlStr = "http://" + TestContent.GetPropertyString("host") + port + "/" + TestContent.GetPropertyString("context") + "/streams.jsp";

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
                        if (urlConnection.getResponseCode() == 200 && !Thread.interrupted()) {
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

                while (messageBuffer.size() > 20){
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
    public void onStop() {
        if(callThread != null){
            callThread.interrupt();
            callThread = null;
        }

        if(sObject != null){
            if(sObject.getData().has("count")) {
                try {
                    sObject.setProperty("count", sObject.getData().getInt("count") - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            sObject.close();
            sObject.client = null;
            sObject = null;
        }

        if(stream != null){
            stream.setListener(null);
            stream.stop();

            if(stream.getVideoSource() != null){
                Camera c = ((R5Camera) stream.getVideoSource()).getCamera();
                c.stopPreview();
                c.release();
            }

            stream = null;

            if(publish != null)
                publish = null;
        }

        super.onStop();
    }
}
