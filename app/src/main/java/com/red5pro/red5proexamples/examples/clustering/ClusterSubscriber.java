package com.red5pro.red5proexamples.examples.clustering;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.red5pro.red5proexamples.R;
import com.red5pro.red5proexamples.examples.BaseExample;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
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
 * Created by Andy Shaules on 11/23/2015.
 */
public class ClusterSubscriber extends BaseExample {
    R5Configuration config;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


       final View view = inflater.inflate(R.layout.fragment_fragment_roundrobin_example, container, false);

        if(subscribe == null) {
            //final EditText ed=(EditText)view.findViewById(R.id.iptext);
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

                            view.post(new Runnable(){
                                public void run(){

                                    TextView tv = (TextView)view.findViewById(R.id.iptext);
                                    tv.setText(toPost,null);
                                };
                            });

                        } else {
                            //Closes the connection.
                            response.getEntity().getContent().close();
                            throw new IOException(statusLine.getReasonPhrase());
                        }
                    }catch(Exception ioe){
                        ioe.printStackTrace();
                    }



                    //Create the configuration from the values.xml and the edge ip
                    config = new R5Configuration(R5StreamProtocol.RTSP, ip, res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
                    R5Connection connection = new R5Connection(config);

                    //setup a new stream using the connection
                    subscribe = new R5Stream(connection);

                    //show all logging
                    subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

                    //find the view and attach the stream
                    R5VideoView r5VideoView = (R5VideoView) view.findViewById(R.id.video);
                    r5VideoView.attachStream(subscribe);
                    r5VideoView.showDebugView(res.getBoolean(R.bool.debugView));

                    subscribe.play(getStream1());
                }
            }).start();


        }

        return view;

    }


}
