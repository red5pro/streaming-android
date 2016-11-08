package red5pro.org.testandroidproject.tests.SubscribeCluster;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import org.apache.http.message.BasicHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 3/16/16.
 */
public class SubscribeCluster extends SubscribeTest {
    protected String edgeIP = "";
    protected TextView edgeShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscribe_test, container, false);

        display = (R5VideoView) view.findViewById(R.id.videoView);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(new HttpGet("http://" + TestContent.GetPropertyString("host") + ":5080/cluster"));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        out.close();
                        //the return is the host ip and rtmp port. we are only interested in the host ip.
                        String[] bits = responseString.split(":");
                        edgeIP = bits[0];
                        Log.i("cluster", "round robin ip: " + edgeIP);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                beginSubscribe();
                            }
                        });

                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }

    protected void beginSubscribe(){

        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                edgeIP,
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

        subscribe.play(TestContent.GetPropertyString("stream1"));

        edgeShow = new TextView(display.getContext());
        FrameLayout.LayoutParams position = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        edgeShow.setLayoutParams(position);

        ((FrameLayout)display.getParent()).addView(edgeShow);

        edgeShow.setText("Connected to: " + edgeIP, TextView.BufferType.NORMAL);
        edgeShow.setBackgroundColor(Color.LTGRAY);
    }
}
