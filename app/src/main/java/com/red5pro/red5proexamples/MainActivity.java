package com.red5pro.red5proexamples;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.red5pro.red5proexamples.examples.BaseExample;
import com.red5pro.red5proexamples.examples.adaptivebitrate.AdaptiveBitrateExample;
import com.red5pro.red5proexamples.examples.adaptivebitrate.AdaptiveBitrateExample;
import com.red5pro.red5proexamples.examples.clustering.ClusterSubscriber;

import com.red5pro.red5proexamples.examples.publish.PublishExample;
import com.red5pro.red5proexamples.examples.reconnect.ReconnectExample;
import com.red5pro.red5proexamples.examples.streamimage.StreamImageExample;
import com.red5pro.red5proexamples.examples.streamsend.StreamSendExample;
import com.red5pro.red5proexamples.examples.subscribe.SubscribeExample;
import com.red5pro.red5proexamples.examples.twoway.TwoWayExample;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Switch swapper = (Switch)rootView.findViewById(R.id.Swap);
            swapper.setChecked(BaseExample.swapped);

            swapper.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    BaseExample.swapped = isChecked;
                }
            });

            rootView.findViewById(R.id.Subscribe).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, (Fragment) new SubscribeExample(), "subscribe_frag");
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            rootView.findViewById(R.id.Adaptive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, (Fragment) new AdaptiveBitrateExample(), "adaptive_frag");
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            rootView.findViewById(R.id.Publish).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, (Fragment) new PublishExample(), "publish_frag");
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            rootView.findViewById(R.id.SteamSend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, (Fragment) new StreamSendExample(), "streamsend_frag");
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            rootView.findViewById(R.id.Reconnect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, (Fragment) new ReconnectExample(), "reconnect_frag");
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            rootView.findViewById(R.id.TwoWay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, (Fragment) new TwoWayExample(), "twoway_frag");
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });

            rootView.findViewById(R.id.RoundRobin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, (Fragment) new ClusterSubscriber(), "cluster_frag");
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

          rootView.findViewById(R.id.StreamImage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                   transaction.replace(R.id.container, (Fragment) new StreamImageExample(), "streamimage_frag");
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            return rootView;
        }
    }


}
