package red5pro.org.testandroidproject.tests.SubscribeBandwidthTest;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;

import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;

/**
 * Created by davidHeimann on 2/10/16.
 */
public class SubscribeBandwidthTest extends SubscribeTest implements R5ConnectionListener {
    private View overlay;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscribe.setListener(this);

        overlay = new View( display.getContext() );
        FrameLayout.LayoutParams position = new FrameLayout.LayoutParams( display.getWidth(), display.getHeight() );
        position.setMargins(0, 0, 0, 0);
        overlay.setLayoutParams(position);
        overlay.setBackgroundColor(0xFF000000);
        overlay.postInvalidate();
        overlay.setAlpha( 0f );

        ((FrameLayout)display.getParent()).addView( overlay );
    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {
        if ( R5ConnectionEvent.NET_STATUS.value() == r5ConnectionEvent.value() ) {
            if( r5ConnectionEvent.message == "NetStream.Play.SufficientBW" ){
                overlay.setAlpha( 0f );
            }
            else if( r5ConnectionEvent.message == "NetStream.Play.InSufficientBW" ){
                overlay.setAlpha( 0.5f );
            }
        }
    }
}
