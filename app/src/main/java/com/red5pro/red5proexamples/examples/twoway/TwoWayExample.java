package com.red5pro.red5proexamples.examples.twoway;


import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
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
import com.red5pro.streaming.view.R5VideoView;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwoWayExample extends BaseExample implements R5ConnectionListener {

    Thread listThread;

    public TwoWayExample() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two_way_example, container, false);

        Resources res = getResources();

        if(publish == null) {

            //subscribe = getNewStream(0);

            //find the view and attach the stream
            //R5VideoView r5VideoView = (R5VideoView) view.findViewById(R.id.video);
           // r5VideoView.attachStream(subscribe);

            //subscribe.play(getString(R.string.stream1));

            publish = getNewStream(1);

            //find the view and attach the stream
            R5VideoView r5PublishView = (R5VideoView) view.findViewById(R.id.video2);

            publish.setView(r5PublishView);

            publish.client = this;
            publish.setListener(this);

            publish.publish(getString(R.string.stream1), R5Stream.RecordType.Live);

            cam.startPreview();

        }

        return view;
    }


    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
        //System.out.println("Connection: "+r5ConnectionEvent.message+String.valueOf(r5ConnectionEvent.value()));
        if(r5ConnectionEvent == R5ConnectionEvent.CONNECTED){


            listThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.interrupted() && publish != null){

                        try{
                            Thread.sleep(4000);

                            publish.connection.call(new R5RemoteCallContainer("streams.getLiveStreams", "R5GetLiveStreams", null));
                        }catch(Exception e){
                            System.out.println("failed to get new streams");
                        }
                    }
                }
            });
            listThread.start();
        }
    }

    public void R5GetLiveStreams(String streams){
        System.out.println("Got the streams: "+streams);
    }

    @Override
    public void onStop(){

        super.onStop();


    }

}
