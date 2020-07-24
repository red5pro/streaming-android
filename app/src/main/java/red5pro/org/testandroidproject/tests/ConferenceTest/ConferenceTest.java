package red5pro.org.testandroidproject.tests.ConferenceTest;

import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5SharedObject;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.media.R5AudioController;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.view.R5VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;
import red5pro.org.testandroidproject.tests.TestContent;

public class ConferenceTest extends PublishTest {

    public LinearLayout rootView = null;
    public RelativeLayout nameRoot, muteRoot;
    public EditText streamNameTxt, roomNameTxt;
    public Button startBtn, videoMuteBtn, audioMuteBtn;
    public Switch clearSwitch;
    public View previewBlinder;
    public ArrayList<LinearLayout> rows = new ArrayList<>();
    public ArrayList<StreamPackage> streams = new ArrayList<>();
    public ArrayList<String> subQueue = null;
    public R5Configuration config = null;
    R5SharedObject roomSO;

    public String pubName, roomName;
    public int onColor = 0xFF00CC88, offColor = 0xFFFF0000; //ARGB
    public boolean lastRowPadding = false, muteLock = true, videoOn = true, audioOn = true, clear = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View baseView = inflater.inflate(R.layout.conference_test, container, false);;

        rootView = baseView.findViewById(R.id.grid_root);
        nameRoot = baseView.findViewById(R.id.name_base);
        muteRoot = baseView.findViewById(R.id.mute_base);
        streamNameTxt = nameRoot.findViewById(R.id.publish_text);
        roomNameTxt = nameRoot.findViewById(R.id.room_text);
        startBtn = nameRoot.findViewById(R.id.publish_btn);
        videoMuteBtn = muteRoot.findViewById(R.id.video_mute);
        audioMuteBtn = muteRoot.findViewById(R.id.audio_mute);
        clearSwitch = nameRoot.findViewById(R.id.clear_switch);

        startBtn.setOnTouchListener(startTouch);
        nameRoot.setOnTouchListener(rootTouch);
        muteRoot.setOnTouchListener(rootTouch);
        videoMuteBtn.setOnTouchListener(videoTouch);
        audioMuteBtn.setOnTouchListener(audioTouch);

        setConfig();

        streamNameTxt.setText( String.format("android-%04d", (int)(Math.random()*9999)) );
        roomNameTxt.setText("red5pro");

        return baseView;
    }

    // v BUTTONS v

    View.OnTouchListener startTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                startBtn.setVisibility(View.GONE);

                pubName = streamNameTxt.getText().toString();
                streamNameTxt.setVisibility(View.GONE);

                roomName = roomNameTxt.getText().toString();
                roomNameTxt.setVisibility(View.GONE);

                clear = clearSwitch.isChecked();
                clearSwitch.setChecked(false);
                clearSwitch.setVisibility(View.GONE);

                nameRoot.setAlpha(0.0f);
                muteRoot.setAlpha(0.0f);

                publish();
                return true;
            }
            return false;
        }
    };

    View.OnTouchListener rootTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (muteRoot.getAlpha() < 0.5f) {
                    muteRoot.setAlpha(1.0f);
                } else {
                    muteRoot.setAlpha(0.0f);
                }
                return true;
            }
            return false;
        }
    };

    View.OnTouchListener videoTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(videoOn){
                    videoMuteBtn.setBackgroundColor(offColor);
                    videoMuteBtn.setText("Video: Off");
                    videoOn = false;
                } else {
                    videoMuteBtn.setBackgroundColor(onColor);
                    videoMuteBtn.setText("Video: On");
                    videoOn = true;
                }
                if(previewBlinder != null){
                    previewBlinder.setAlpha(videoOn ? 0.0f : 0.5f);
                }
                if(!muteLock) streams.get(0).stream.restrainVideo(!videoOn);
                return true;
            }
            return false;
        }
    };

    View.OnTouchListener audioTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(audioOn){
                    audioMuteBtn.setBackgroundColor(offColor);
                    audioMuteBtn.setText("Audio: Off");
                    audioOn = false;
                } else {
                    audioMuteBtn.setBackgroundColor(onColor);
                    audioMuteBtn.setText("Audio: On");
                    audioOn = true;
                }
                if(!muteLock) streams.get(0).stream.restrainAudio(!audioOn);
                return true;
            }
            return false;
        }
    };

    // ^ BUTTONS ^
    // v STREAMS v

    public void setConfig() {
        String b = getActivity().getPackageName();

        config = new R5Configuration(R5StreamProtocol.RTSP,
                TestContent.GetPropertyString("host"),
                TestContent.GetPropertyInt("port"),
                TestContent.GetPropertyString("context"),
                TestContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(TestContent.GetPropertyString("license_key"));
        config.setBundleID(b);
    }

    @Override
    public void publish() {
        StreamPackage pack = new StreamPackage();
        streams.add(pack);

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);
        pack.stream = publish;
        pack.view = makeNewView();
        pack.name = pubName;

        previewBlinder = new View(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        previewBlinder.setLayoutParams(params);
        previewBlinder.setBackgroundColor(0xFFAAAAAA);
        previewBlinder.setAlpha(videoOn ? 0.0f : 0.5f);
        pack.view.addView(previewBlinder);

        AddTag(pack.view, pubName);

        AddNewView(pack.view);

        pack.stream.audioController.sampleRate =  TestContent.GetPropertyInt("sample_rate");

        //show all logging
        pack.stream.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //attach a microphone
        attachMic();

        pack.view.attachStream(pack.stream);
//        pack.view.showDebugView(TestContent.GetPropertyBool("debug_view"));

        //attach a camera video source
        attachCamera();

        pack.stream.setListener(getConnectionListener(pubName, true));
        pack.stream.publish(pubName, getPublishRecordType());

        cam.startPreview();

        publish = null;
    }

    public void Subscribe(String toName) {
        StreamPackage pack = new StreamPackage();
        streams.add(pack);

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        pack.stream = new R5Stream(connection);

        //Some devices can't handle rapid reuse of the audio controller, and will crash
        //Recreation of the controller assures that the example will always be stable
        pack.stream.audioController = new R5AudioController();
        pack.stream.audioController.sampleRate = TestContent.GetPropertyInt("sample_rate");

        pack.stream.client = this;
        pack.stream.setListener(getConnectionListener(toName));

        //show all logging
        pack.stream.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        pack.name = toName;
        pack.stream.play(toName, TestContent.GetPropertyBool("hwAccel_on"));
    }

    public R5ConnectionListener getConnectionListener(String streamName) { return getConnectionListener(streamName, false); }
    public R5ConnectionListener getConnectionListener(final String streamName, final boolean isPublish) {

        return new R5ConnectionListener() {
            String name = streamName;
            boolean pub = isPublish;
            boolean dead = false;
            boolean calledForNextInQueue = pub;
            @Override
            public void onConnectionEvent(R5ConnectionEvent event) {
                if (dead) {
                    return;
                }
                System.out.println("Event for " + (pub? "Publisher" : "Stream: " + name) + " is: " + event.name() + " - " + event.message);

                if (!calledForNextInQueue && event.name().equals(R5ConnectionEvent.CONNECTED.name()) && subQueue != null) {
                    nextSubInQueue();
                    calledForNextInQueue = true;
                }

                if (event.name().equals(R5ConnectionEvent.START_STREAMING.name())) {
                    if(pub) {
                        if (videoOn && audioOn) {
                            muteLock = false;
                            connectSO();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(500);
                                    } catch (Exception e) {
                                        return;
                                    }
                                    if (streams != null && streams.size() > 0) {
                                        muteLock = false;
                                        R5Stream stream = streams.get(0).stream;
                                        if (!audioOn) stream.restrainAudio(true);
                                        if (!videoOn) stream.restrainVideo(true);
                                        connectSO();
                                    }
                                }
                            }).start();
                        }
                    }
                    else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newViewForPack(packFromName(name));
                            }
                        });
                    }
                }

                if(event.name().equals(R5ConnectionEvent.ERROR.name()) || event.name().equals(R5ConnectionEvent.DISCONNECTED.name())
                    || event.name().equals(R5ConnectionEvent.CLOSE.name())) {
                    dead = true;
                    if (pub) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().onBackPressed();
                            }
                        });
                    } else {
                        clearByName(name);
                        if(!calledForNextInQueue && subQueue != null){
                            nextSubInQueue();
                        }
                    }
                }
            }
        };
    }

    public void nextSubInQueue(){
        if(subQueue.size() > 0){
            final String nextSub = subQueue.get(0);
            subQueue.remove(0);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Subscribe(nextSub);
                }
            });
        } else {
            subQueue = null;
        }
    }

    // ^ STREAMS ^
    // v SHARED OBJECT v

    public void connectSO(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(100);
                }catch (Exception e){
                    return;
                }
                if (streams == null || streams.size() < 1) { //we've stopped
                    return;
                }
                roomSO = new R5SharedObject(roomName, streams.get(0).stream.connection);
                roomSO.client = ConferenceTest.this;
            }
        }).start();
    }

    //callback for remote object connection - remote object now available
    public void onSharedObjectConnect(JSONObject objectValue) {
        try {
            JSONObject data = roomSO.getData();
            String streamsString = data.has("streams") ? (String) data.get("streams") : "";
            if(streamsString.isEmpty() || clear){
                streamsString = pubName;
                clear = false;
            } else {
                stringToQueue(streamsString);
                streamsString += "," + pubName;
            }
            roomSO.setProperty("streams", streamsString);
        } catch (JSONException e) {}
    }

    //Called whenever a property of the shared object is changed
    public void onUpdateProperty(JSONObject propertyInfo){
        try {
            String streamsString = propertyInfo.getString("streams");
            stringToQueue(streamsString);
        } catch (JSONException e) {}
    }

    public void stringToQueue(String incoming){
        boolean startQueue = false;
        if(subQueue == null){
            subQueue = new ArrayList<>();
            startQueue = true;
        }
        String[] split = incoming.split(",");
        for (String s : split) {
            boolean found = false;
            if(packFromName(s) != null){
                found = true;
            }
            for (String queueS : subQueue) {
                if(s.equals(queueS)) {
                    found = true;
                    break;
                }
            }
            if(!found)
                subQueue.add(s);
        }
        if(startQueue){
            if(subQueue.size() < 1){
                subQueue = null;
            } else {
                nextSubInQueue();
            }
        }

        ArrayList<String> allActiveNames = new ArrayList<>();
        for (StreamPackage pack : streams) {
            allActiveNames.add(pack.name);
        }
        allActiveNames.addAll(subQueue);
        for (String s : split) {
            allActiveNames.remove(s);
        }
        allActiveNames.remove(pubName);

        for (String s : allActiveNames) {
            clearByName(s);
        }
    }

    public StreamPackage packFromName(String nameIn){
        for (StreamPackage pack : streams) {
            if (pack.name.equals(nameIn)) {
                return pack;
            }
        }
        return null;
    }

    public void clearByName(String clearName) {
        StreamPackage targetPack = packFromName(clearName);

        if(targetPack == null){
            return;
        }

        final StreamPackage foundPack = targetPack;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                foundPack.stream.setListener(null);
                foundPack.stream.stop();
                streams.remove(foundPack);
                if(foundPack.view != null) {
                    removeView(foundPack.view);
                }
            }
        });
    }

    // ^ SHARED OBJECT ^
    // v VIEWS V

    public void AddTag(R5VideoView r5View, String tagName) {
        TextView tag = new TextView(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                                        FrameLayout.LayoutParams.WRAP_CONTENT);
        tag.setLayoutParams(params);
        if(Build.VERSION.SDK_INT >= 17) {
            tag.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        tag.setText(tagName);
        r5View.addView(tag);
    }

    public void newViewForPack(StreamPackage pack){
        if(pack.view != null){
            return;
        }

        pack.view = makeNewView();
        AddTag(pack.view, pack.name);
        pack.view.attachStream(pack.stream);
//        pack.view.showDebugView(TestContent.GetPropertyBool("debug_view"));

        AddNewView(pack.view);
    }

    private float targetRatio = 1.0f;
    public void AddNewView(R5VideoView r5View) {
        int rowCount = rows.size();
        LinearLayout currentRow;
        int i = 0;
        if (rowCount < 1) {
            currentRow = makeNewRow();
            rows.add(currentRow);
            rootView.addView(currentRow);
            currentRow.addView(r5View);
        }
        else if (rowCount > 1 && rows.get(0).getChildCount() != rows.get(rowCount-1).getChildCount() - (lastRowPadding ? 2 : 0)) {
            do{
                i++;
                currentRow = rows.get(i);
            } while (currentRow.getChildCount() == rows.get(0).getChildCount() && i < rowCount - 1);
            if (currentRow == rows.get(rowCount-1) && lastRowPadding) {
                currentRow.addView(r5View, currentRow.getChildCount()-2);
            } else {
                currentRow.addView(r5View);
            }
        }
        else {
            //need new row/column, decide which is best by calculating which would make the views
            //closer to a target aspect ratio (1:1)
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            int columnCount = rows.get(0).getChildCount();
            float newRowRatio = (width/((float)columnCount)) / (height/(rowCount + 1.0f));
            float newColumnRatio = (width/(columnCount + 1.0f)) / (height/((float)rowCount));

            if (Math.abs(newColumnRatio - targetRatio) < Math.abs(newRowRatio - targetRatio)) {
                currentRow = rows.get(0);
                currentRow.addView(r5View);
            }
            else {
                currentRow = makeNewRow();
                rows.add(currentRow);
                rootView.addView(currentRow);
                currentRow.addView(r5View);
            }
        }
        padLastRow();
    }

    public void removeView(R5VideoView r5View) {
        ((LinearLayout)r5View.getParent()).removeView(r5View);

        int maxColumns = 0, rowCount = rows.size(), count, i;
        for (i = 0; i < rowCount; i++) {
            count = rows.get(i).getChildCount();
            if(i == rowCount - 1 && lastRowPadding){
                count -= 2;
            }
            if(count > maxColumns)
                maxColumns = count;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        float lessRowRatio = (width/((float)maxColumns)) / (height/(rowCount - 1.0f));
        float lessColumnRatio = (width/(maxColumns - 1.0f)) / (height/((float)rowCount));

        LinearLayout currentRow, neighbor;
        View floatingView = null;
        //last added was?
        if (Math.abs(lessColumnRatio - targetRatio) < Math.abs(lessRowRatio - targetRatio)) {
            //column: start from bottom, fill to "maxColumn"-1 take from last of row up
            for (i = rowCount-1; i > 0; i--) {
                currentRow = rows.get(i);
                if (currentRow.getChildCount() < maxColumns -1) {
                   neighbor = rows.get(i-1);
                   floatingView = neighbor.getChildAt(neighbor.getChildCount()-1);
                   neighbor.removeView(floatingView);
                   if (i == rowCount - 1 && lastRowPadding) {
                       currentRow.addView(floatingView, 1);
                   } else {
                       currentRow.addView(floatingView, 0);
                   }
                }
            }
        }
        else  {
            //row: start from top, fill all to "maxColumn" taking from first on the next row
            for (i = 0; i < rowCount-1; i++) {
                currentRow = rows.get(i);
                if (currentRow.getChildCount() < maxColumns) {
                    neighbor = rows.get(i+1);
                    if (i == rowCount-2 && lastRowPadding) {
                        if(neighbor.getChildCount() > (lastRowPadding? 2 : 0)) {
                            floatingView = neighbor.getChildAt(1);
                        }
                    } else {
                        floatingView = neighbor.getChildAt(0);
                    }
                    if(floatingView != null) {
                        neighbor.removeView(floatingView);
                        currentRow.addView(floatingView);
                    }
                }
            }
            //delete last row if needed
            currentRow = rows.get(rowCount-1);
            if(currentRow.getChildCount() < (lastRowPadding? 3: 1)){
                rows.remove(rowCount-1);
                rootView.removeView(currentRow);
                lastRowPadding = false;
            }
        }
        padLastRow();
    }

    public void padLastRow(){
        if(rows == null || rows.size() < 1){
            return;
        }

        LinearLayout lastRow = rows.get(rows.size() - 1);
        if(rows.size() < 2){
            if(lastRowPadding && lastRow.getChildCount() > streams.size()){
                lastRow.removeViewAt(lastRow.getChildCount()-1);
                lastRow.removeViewAt(0);
            }
            lastRowPadding = false;
            return;
        }

        int neighborRowCount = rows.get(rows.size() - 2).getChildCount();
        int lastRowCount = lastRow.getChildCount() - (lastRowPadding ? 2 : 0);
        if (lastRowCount < neighborRowCount) {
            if(!lastRowPadding){
                View pad = new View(getActivity());
                lastRow.addView(pad, 0);
                pad = new View(getActivity());
                lastRow.addView(pad);
            }
            float weight = (neighborRowCount - lastRowCount) / 2.0f;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = weight;
            lastRow.getChildAt(0).setLayoutParams(params);
            lastRow.getChildAt(lastRow.getChildCount() - 1 ).setLayoutParams(params);
            lastRowPadding = true;
        }
        else if (lastRowPadding) {
            lastRow.removeViewAt(lastRowCount + 1);
            lastRow.removeViewAt(0);
            lastRowPadding = false;
        }
    }

    public LinearLayout makeNewRow() {
        LinearLayout row = new LinearLayout(getActivity());
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        row.setLayoutParams(params);
        return row;
    }

    public R5VideoView makeNewView() {
        R5VideoView r5view = new R5VideoView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        r5view.setLayoutParams(params);
        return r5view;
    }

    // ^ VIEWS ^

    @Override
    public void onStop() {
        if(roomSO != null){
            try {
                String streamsString = (String) roomSO.getData().get("streams");
                String[] streamsList = streamsString.split(",");
                StringBuilder builder = null;

                for (String s : streamsList) {
                    if(!s.equals(pubName)){
                        if(builder == null){
                            builder = new StringBuilder();
                        }
                        else {
                            builder.append(",");
                        }
                        builder.append(s);
                    }
                }
                roomSO.setProperty("streams", builder == null ? "" : builder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            roomSO.client = null;
            roomSO.close();
        }
        if(streams != null && streams.size() > 0){
            StreamPackage pack = streams.get(0);

            pack.view.attachStream(null);
            pack.stream.setListener(null);
            pack.stream.stop();

            if(pack.stream.getVideoSource() != null) {
                Camera c = ((R5Camera) pack.stream.getVideoSource()).getCamera();
                c.stopPreview();
                c.release();
            }

            for (int i = 1; i < streams.size(); i++) {
                pack = streams.get(i);
                pack.view.attachStream(null);
                pack.stream.stop();
            }
            streams.clear();
            streams = null;
        }
        if (subQueue != null) {
            subQueue.clear();
            subQueue = null;
        }

        super.onStop();
    }

    public static class StreamPackage {
        public R5Stream stream = null;
        public R5VideoView view = null;
        public String name = null;
    }
}
