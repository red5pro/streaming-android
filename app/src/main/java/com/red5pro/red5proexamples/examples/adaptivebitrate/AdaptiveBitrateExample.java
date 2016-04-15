package com.red5pro.red5proexamples.examples.adaptivebitrate;


import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.red5pro.red5proexamples.R;
import com.red5pro.red5proexamples.examples.BaseExample;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.source.R5AdaptiveBitrateController;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdaptiveBitrateExample extends BaseExample {


    public AdaptiveBitrateExample() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adaptive_example, container, false);

        Resources res = getResources();

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //attach a camera video source
        cam = openFrontFacingCameraGingerbread();
        cam.setDisplayOrientation((cameraOrientation + 180)%360);

        R5Camera camera  = new R5Camera(cam, 320, 240);
        camera.setBitrate(res.getInteger(R.integer.highBitrate));
        camera.setOrientation(cameraOrientation);

        R5AdaptiveBitrateController adaptor = new R5AdaptiveBitrateController();
        adaptor.AttachStream(publish);

        publish.attachCamera(camera);

        //attach a microphone
        R5Microphone mic = new R5Microphone();

        publish.attachMic(mic);

        SurfaceView r5VideoView = (SurfaceView) view.findViewById(R.id.video2);

        publish.setView(r5VideoView);

        publish.publish(getStream1(), R5Stream.RecordType.Live);

        cam.startPreview();

        return view;
    }


}
