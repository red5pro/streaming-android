package com.red5pro.red5proexamples.examples.streamsend;


import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.red5proexamples.R;
import com.red5pro.red5proexamples.examples.BaseExample;
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

import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 */
public class StreamSendExample extends BaseExample implements R5ConnectionListener {


    public StreamSendExample() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_streamsend_example, container, false);

        Resources res = getResources();

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
        R5Connection connection = new R5Connection(config);

        //if the 'Switch Names' hasn't been toggled, publish
        if(!BaseExample.swapped) {
            //setup a new stream using the connection
            publish = new R5Stream(connection);

            //show all logging
            publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

            //attach a camera video source
            cam = openFrontFacingCameraGingerbread();
            cam.setDisplayOrientation(90);

            R5Camera camera = new R5Camera(cam, 320, 240);
            camera.setBitrate(res.getInteger(R.integer.bitrate));
            camera.setOrientation(-90);

            publish.attachCamera(camera);

            //attach a microphone
            R5Microphone mic = new R5Microphone();

            publish.attachMic(mic);

            SurfaceView r5PublishView = (SurfaceView) view.findViewById(R.id.video1);

            publish.setView(r5PublishView);

            publish.client = this;
            publish.setListener(this);

            r5PublishView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent e) {
                    Hashtable<String, String> map = new Hashtable<String, String>();
                    map.put("value", "A simple string");

                    publish.send(new R5RemoteCallContainer("onStreamSend", map));

                    return true;
                }
            });

            publish.publish(getStream1(), R5Stream.RecordType.Live);

            cam.startPreview();

        }//otherwise subscribe to catch the messages
        else {

            //setup a new stream using the connection
            subscribe = new R5Stream(connection);

            subscribe.client = this;
            subscribe.setListener(this);

            //show all logging
            subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

            //find the view and attach the stream
            R5VideoView r5SubscribeView = (R5VideoView) view.findViewById(R.id.video2);
            r5SubscribeView.attachStream(subscribe);

            subscribe.play(getStream2());

        }

        return view;
    }

    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        if ( r5ConnectionEvent == R5ConnectionEvent.CONNECTED )
        {
            System.out.println("Publish stream ready to send RPC");
        }
        if ( r5ConnectionEvent.message != null && r5ConnectionEvent.message != "" )
        {
            System.out.println("Received message: " + r5ConnectionEvent.message);
        }
    }

    public void onStreamSend( String received ) {

        String[] parsedReceive = received.split(";");
        System.out.println("Received data from publisher:");
        for (String s : parsedReceive) {
            String key = s.split("=")[0];
            String value = s.split("=")[1];
            System.out.println("Received key: " + key + "; with value: " + value);
        }
    }

}
