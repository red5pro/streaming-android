package red5pro.org.testandroidproject.tests.SubscribeSetVolumeTest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.red5pro.streaming.view.R5VideoView;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.SubscribeTest.SubscribeTest;

/**
 * Created by toddanderson on 08/30/2018.
 */
public class SubscribeSetVolumeTest extends SubscribeTest {

    private SeekBar slider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.subscribe_scrub_test, container, false);

        //find the view and attach the stream
        display = (R5VideoView) view.findViewById(R.id.videoView);
        slider = (SeekBar) view.findViewById(R.id.seekBar);
        slider.setProgress(100);

        Subscribe();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = i/100f;
                Log.d("Subscriber", ":setVolume=" + value);
                subscribe.audioController.setPlaybackGain(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
