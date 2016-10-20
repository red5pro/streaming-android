package com.red5pro.red5proexamples.examples.twoway;


import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.red5proexamples.R;
import com.red5pro.red5proexamples.examples.BaseExample;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.event.R5RemoteCallContainer;
import com.red5pro.streaming.view.R5VideoView;

import org.json.JSONArray;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwoWayExample extends BaseExample implements R5ConnectionListener {

    Thread listThread;
    boolean hasPublished;

    public TwoWayExample() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two_way_example, container, false);

        Resources res = getResources();

        if(publish == null) {

            subscribe = getNewStream(0);

            //find the view and attach the stream
            R5VideoView r5VideoView = (R5VideoView) view.findViewById(R.id.video2);
            r5VideoView.attachStream(subscribe);
            r5VideoView.showDebugView(res.getBoolean(R.bool.debugView));

            subscribe.client = this;
            subscribe.setListener(this);

            publish = getNewStream(1);

            //find the view and attach the stream
            R5VideoView r5PublishView = (R5VideoView) view.findViewById(R.id.video);
            r5PublishView.attachStream(publish);
            r5PublishView.showDebugView(res.getBoolean(R.bool.debugView));

            publish.client = this;
            publish.setListener(this);

            publish.publish(getStream1(), R5Stream.RecordType.Live);

            cam.startPreview();

        }

        return view;
    }


    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
        //System.out.println("Connection: "+r5ConnectionEvent.message+String.valueOf(r5ConnectionEvent.value()));
        if(r5ConnectionEvent == R5ConnectionEvent.CONNECTED){
            if(!hasPublished) {

                listThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                    while (!Thread.interrupted() && publish != null) {

                        try {
                            Thread.sleep(4000);

                            publish.connection.call(new R5RemoteCallContainer("streams.getLiveStreams", "R5GetLiveStreams", null));
                        } catch (Exception e) {
                            System.out.println("failed to get new streams");
                        }
                    }
                    }
                });
                listThread.start();
                hasPublished = true;
            }
            else {
                System.out.println("Subscribed - two way active");
            }
        }
    }


    public void R5GetLiveStreams(String streams){
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
                if(getStream2().equals(names.getString(i))){
                    subscribe.play(getStream2());
                    listThread.interrupt();
                    return;
                }
            } catch (Exception e){
                System.out.println("Item at index " + i + " cannot be retrieved as a String");
            }
        }
    }

}
