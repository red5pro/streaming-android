package com.red5pro.red5proexamples.examples.streamimage;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.red5pro.red5proexamples.R;
import com.red5pro.red5proexamples.examples.BaseExample;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5RemoteCallContainer;
import com.red5pro.streaming.source.R5AdaptiveBitrateController;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 */
public class StreamImageExample extends BaseExample {


    public StreamImageExample() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_streamimage_example, container, false);

        Resources res = getResources();

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        subscribe = new R5Stream(connection);

        //show all logging
        subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        R5VideoView r5VideoView = (R5VideoView) view.findViewById(R.id.video);
        r5VideoView.attachStream(subscribe);
        r5VideoView.showDebugView(res.getBoolean(R.bool.debugView));

        r5VideoView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {

                ImageView iv = (ImageView) view.findViewById(R.id.screenshot);
                Bitmap b = subscribe.getStreamImage();
                iv.setImageBitmap(b);

                return true;
            }
        });

        subscribe.play(getStream1());

        return view;
    }


}
