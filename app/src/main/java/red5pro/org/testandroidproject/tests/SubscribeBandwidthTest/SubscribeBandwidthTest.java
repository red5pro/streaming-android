//
// Copyright © 2015 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
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
public class SubscribeBandwidthTest extends SubscribeTest {
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
        super.onConnectionEvent(r5ConnectionEvent);
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
