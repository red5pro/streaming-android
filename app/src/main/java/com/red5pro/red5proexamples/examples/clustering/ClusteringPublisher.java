package com.red5pro.red5proexamples.examples.publish;


import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Looper;
import android.util.Log;
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
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClusteringPublisher extends BaseExample {


    public ClusteringPublisher() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final  View view = inflater.inflate(R.layout.fragment_publish_example, container, false);

        cam = openFrontFacingCameraGingerbread();
        cam.setDisplayOrientation(90);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                String ip = null;
                Resources res = getResources();
                try {


                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(new HttpGet("http://"+res.getString(R.string.domain)+":5080/cluster"));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        out.close();
                        //the return is the host ip and rtmp port. we are only interested in the host ip.
                        String[] bits = responseString.split(":");
                        final String toPost = ip = bits[0];
                        Log.i("cluster", "round robin ip: " + ip);


                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                }catch(Exception ioe){
                    ioe.printStackTrace();
                }
                //Create the configuration from the values.xml and the edge ip
                R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP, ip, res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
                R5Connection connection = new R5Connection(config);

                //setup a new stream using the connection
                publish = new R5Stream(connection);

                //show all logging
                publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

                //attach a camera video source


                R5Camera camera  = new R5Camera(cam, 320, 240);
                camera.setBitrate(res.getInteger(R.integer.bitrate));
                camera.setOrientation(-90);



                //attach a microphone
                R5Microphone mic = new R5Microphone();

                publish.attachMic(mic);

                SurfaceView r5VideoView =(SurfaceView) view.findViewById(R.id.video2);

                publish.setView(r5VideoView);

                publish.attachCamera(camera);

                publish.publish(getStream1(), R5Stream.RecordType.Live);


            }
        }).start();

        cam.startPreview();

        return view;
    }


}
