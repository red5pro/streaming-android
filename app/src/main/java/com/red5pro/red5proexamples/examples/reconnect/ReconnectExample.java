package com.red5pro.red5proexamples.examples.reconnect;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
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

/**

 */
public class ReconnectExample extends BaseExample implements R5ConnectionListener {

    Thread retryThread;

    public ReconnectExample() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reconnect_example, container, false);

        if(subscribe == null) {

            Resources res = getResources();

            //Create the configuration from the values.xml
            R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP, res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
            R5Connection connection = new R5Connection(config);

            //setup a new stream using the connection
            subscribe = new R5Stream(connection);

            subscribe.client = this;
            subscribe.setListener(this);

            //show all logging
            subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

            //find the view and attach the stream
            R5VideoView r5VideoView = (R5VideoView) view.findViewById(R.id.video);
            r5VideoView.attachStream(subscribe);

            subscribe.play(getStream1());

        }

        return view;
    }

    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
        
        if ( r5ConnectionEvent == R5ConnectionEvent.CLOSE )
        {
            retryThread = new Thread(new Runnable() {
                @Override
                public void run() {
                if(!Thread.interrupted() && subscribe != null){

                    try{
                        Thread.sleep(8000);

                        subscribe.stop();
                        subscribe.play(getStream1());
                    }catch(Exception e){
                        System.out.println("failed to reconnect");
                    }
                }
                }
            });
            retryThread.start();
        }
    }

}
