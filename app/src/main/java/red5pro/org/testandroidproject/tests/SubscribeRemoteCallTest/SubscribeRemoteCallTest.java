package red5pro.org.testandroidproject.tests.SubscribeRemoteCallTest;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import java.util.Hashtable;

import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;

/**
 * Created by davidHeimann on 4/26/16.
 */
public class SubscribeRemoteCallTest extends SubscribeTest {
    private TextView messageView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscribe.client = this;
    }

    public void whateverFunctionName( String message ){

        System.out.println("Recieved message from publisher: " + message);

        String[] parsedMessage = message.split(";");
        Hashtable<String, String> map = new Hashtable<String, String>();
        for (String s : parsedMessage) {
            String key = s.split("=")[0];
            String value = s.split("=")[1];
            System.out.println("Received key: " + key + "; with value: " + value);

            map.put(key,value);
        }

        final Hashtable<String, String> mapFinal = map;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (messageView == null) {
                    messageView = new TextView(display.getContext());
                    ((FrameLayout) display.getParent()).addView(messageView);
                    messageView.setBackgroundColor(Color.LTGRAY);
                }

                if (mapFinal.containsKey("message")) {
                    messageView.setText(mapFinal.get("message"));
                }

                FrameLayout.LayoutParams position = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (mapFinal.containsKey("touchX")) {
                    position.leftMargin = (int) (Float.parseFloat(mapFinal.get("touchX")) * display.getWidth());
                }
                if (mapFinal.containsKey("touchY")) {
                    position.topMargin = (int) (Float.parseFloat(mapFinal.get("touchY")) * display.getHeight());
                }
                messageView.setLayoutParams(position);
            }
        });
    }
}
