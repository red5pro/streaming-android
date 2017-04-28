package red5pro.org.testandroidproject.tests.SubscribeReceiveSendTest;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;
import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 2/9/16.
 */
public class SubscribeReceiveSendTest extends SubscribeTest {


    public void onStreamSend(String map){

        final String m = map;
        Log.d("SubscribeReceive", "Got map: " + map);
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(SubscribeReceiveSendTest.this.

                        getActivity(),m,Toast

                        .LENGTH_SHORT).

                        show();
            }
        });

    }

}
