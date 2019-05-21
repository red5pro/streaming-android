package red5pro.org.testandroidproject.tests.PublishCustomMicTest;

import com.red5pro.streaming.source.R5Microphone;

import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;

/**
 * Created by davidHeimann on 12/22/17.
 */

public class PublishCustomMicTest extends PublishTest {

    @Override
    protected void attachMic() {

        R5Microphone mic = new gainWobbleMic();
        publish.attachMic(mic);
    }

    public class gainWobbleMic extends R5Microphone {

        private float gain = 1.0f;
        private int mod = 1;
        private double lastTime = 0;

        @Override
        public void processData(byte[] samples, double streamtimeMill) {

            modifyGain(streamtimeMill - lastTime);
            lastTime = streamtimeMill;

            int s;
            for(int i = 0; i < samples.length; i++){

                 s = (int) (samples[i] * gain);
                 samples[i] = (byte) Math.min(s, 0xff);
            }

            super.processData(samples, streamtimeMill);
        }

        private void modifyGain(double time){
            //causes the gain to increase to double volume and decrease to 0 volume, then back
            gain += mod * (time/2000);
            if( gain >= 2 || gain <= 0 ){
                System.out.println("gain at: " + gain);
                gain = Math.max(2.0f * mod, 0.0f);
                mod *= -1;
            }
        }
    }
}
