package red5pro.org.testandroidproject.tests.TwoWayTest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.event.R5RemoteCallContainer;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import org.json.JSONArray;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 3/4/16.
 */
public class TwoWayTest extends PublishTest {
    protected R5VideoView display;
    protected R5Stream subscribe;
    protected Button subButton;
    protected Thread listThread;
    protected boolean isPublishing = false;
    protected boolean isSubscribing = false;
    protected Activity parent = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.twoway_test, container, false);

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("buffer_time"));
        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        R5Camera camera = null;
        if(TestContent.GetPropertyBool("video_on")) {
            //attach a camera video source
            cam = openFrontFacingCameraGingerbread();
            cam.setDisplayOrientation((camOrientation + 180) % 360);

            camera = new R5Camera(cam, TestContent.GetPropertyInt("camera_width"), TestContent.GetPropertyInt("camera_height"));
            camera.setBitrate(TestContent.GetPropertyInt("bitrate"));
            camera.setOrientation(camOrientation);
        }

        if(TestContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            R5Microphone mic = new R5Microphone();
            publish.attachMic(mic);
        }

        preview = (R5VideoView)rootView.findViewById(R.id.videoPreview);

        preview.attachStream(publish);

        if(TestContent.GetPropertyBool("video_on"))
            publish.attachCamera(camera);

        publish.client = this;
        publish.setListener(new R5ConnectionListener() {
            @Override
            public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
                if(r5ConnectionEvent == R5ConnectionEvent.START_STREAMING){

                        isPublishing = true;
                        sendRemoteCall();

                }

                if(r5ConnectionEvent == R5ConnectionEvent.DISCONNECTED){

                    if(isSubscribing){
                        subscribe.stop();
                        subscribe = null;
                        isSubscribing = false;
                    }

                    isPublishing = false;
                }
            }
        });

        preview.showDebugView(TestContent.GetPropertyBool("debug_view"));

        publish.publish(TestContent.GetPropertyString("stream1"), R5Stream.RecordType.Live);

        if(TestContent.GetPropertyBool("video_on"))
            cam.startPreview();

        display = (R5VideoView)rootView.findViewById(R.id.videoView);

        return rootView;
    }


    private void sendRemoteCall(){
        listThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);

                    publish.connection.call(new R5RemoteCallContainer("streams.getLiveStreams", "R5GetLiveStreams", null));
                } catch (Exception e) {
                    if(e.toString().contains("InterruptedException"))
                        e.printStackTrace();
                    System.out.println("failed to get new streams");
                }
            }
        });
        listThread.start();

    }

    private void checkFrameCount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2500);

                    R5Stream.R5Stats subStats = subscribe.getStats();
                    System.out.println("Subscribe stream is currently holding: " + subStats.subscribe_queue_size + " frames in queue");

                    if(subStats.subscribe_queue_size <= 1){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                subscribe.stop();
                                subscribe = null;
                            }
                        });
                        Thread.sleep(500);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onSubscribeReady();
                            }
                        });
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void R5GetLiveStreams(String streams){

        if(subscribe!=null)
            return;

        System.out.println("Got the streams: "+streams);

        //parse string as JSON
        JSONArray names;
        try {
            names = new JSONArray(streams);
        } catch (Exception e) {
            System.out.println("Failed to parse streams to JSONArray");
            return;
        }

        //Look for the other stream, subscribe when available
        for(int i  = 0; i < names.length(); i++){
            try {
                if(TestContent.GetPropertyString("stream2").equals(names.getString(i))){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                getActivity().runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        onSubscribeReady();
                                    }
                                });
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    return;
                }
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Item at index " + i + " cannot be retrieved as a String");
            }
        }

        try{
            //the target stream hasn't been found, try again
            Thread.sleep(1500);
            sendRemoteCall();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void onSubscribeReady( ){

        if( subscribe != null )
            return;

        System.out.println("Subscribing");

        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("buffer_time"));
        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //find the view and attach the stream
        display.attachStream(subscribe);

        display.showDebugView(TestContent.GetPropertyBool("debug_view"));

        R5ConnectionListener listener = new R5ConnectionListener() {
            @Override
            public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

                if(r5ConnectionEvent == R5ConnectionEvent.START_STREAMING){

                    isSubscribing = true;
                }

                if(r5ConnectionEvent == R5ConnectionEvent.ERROR){

                    subscribe.stop();
                    subscribe = null;
                    isSubscribing = false;
                    sendRemoteCall();
                }

                if(r5ConnectionEvent == R5ConnectionEvent.DISCONNECTED){

                    if(isSubscribing){
                        subscribe.stop();
                        subscribe = null;
                        isSubscribing = false;
                    }

                    isSubscribing = false;
                }
            }
        };
        subscribe.setListener(listener);

        subscribe.play(TestContent.GetPropertyString("stream2"));
    }

    @Override
    public void onStop() {

        if(subscribe != null){
            subscribe.stop();
        }

        super.onStop();
    }
}
