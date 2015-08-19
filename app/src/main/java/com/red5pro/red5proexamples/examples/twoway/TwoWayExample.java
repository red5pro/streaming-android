package com.red5pro.red5proexamples.examples.twoway;


import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.red5pro.red5proexamples.R;
import com.red5pro.red5proexamples.examples.BaseExample;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.view.R5VideoView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwoWayExample extends BaseExample {


    public TwoWayExample() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two_way_example, container, false);

        Resources res = getResources();

        subscribe = getNewStream(0);

        //find the view and attach the stream
        R5VideoView r5VideoView =(R5VideoView) view.findViewById(R.id.video);
        r5VideoView.attachStream(subscribe);

        subscribe.play(getString(R.string.stream1));

        publish = getNewStream(1);

        //find the view and attach the stream
        R5VideoView r5PublishView =(R5VideoView) view.findViewById(R.id.video2);

        publish.setView(r5PublishView);

        publish.publish(getString(R.string.stream1), R5Stream.RecordType.Live);

        return view;
    }

}
