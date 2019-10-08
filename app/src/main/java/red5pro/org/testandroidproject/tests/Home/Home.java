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
package red5pro.org.testandroidproject.tests.Home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.red5pro.streaming.R5Stream;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.TestDetailFragment;
import red5pro.org.testandroidproject.tests.TestContent;

/**
 * Created by davidHeimann on 3/10/16.
 */
public class Home extends TestDetailFragment {
    EditText licenseText;
    EditText hostText;
    EditText stream1Text;
    EditText stream2Text;
    Button swapButton;
    CheckBox debugCheck;
    CheckBox videoCheck;
    CheckBox audioCheck;
    RadioButton liveMode;
    RadioButton recordMode;
    RadioButton appendMode;
    RadioGroup radioRecordMode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_screen, container, false);

        licenseText = (EditText)rootView.findViewById(R.id.licenseText);
        hostText = (EditText)rootView.findViewById(R.id.hostText);
        stream1Text = (EditText)rootView.findViewById(R.id.stream1Text);
        stream2Text = (EditText)rootView.findViewById(R.id.stream2Text);

        licenseText.setText(TestContent.GetPropertyString("license_key"));
        hostText.setText(TestContent.GetPropertyString("host"));
        stream1Text.setText(TestContent.GetPropertyString("stream1"));
        stream2Text.setText(TestContent.GetPropertyString("stream2"));

        radioRecordMode = (RadioGroup) rootView.findViewById(R.id.radioRecordMode);
        liveMode = (RadioButton)rootView.findViewById(R.id.radioModeLive);
        recordMode = (RadioButton)rootView.findViewById(R.id.radioModeRecord);
        appendMode = (RadioButton)rootView.findViewById(R.id.radioModeAppend);

        licenseText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TestContent.SetPropertyString("license_key", licenseText.getText().toString());
            }
        });

        hostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TestContent.SetPropertyString("host", hostText.getText().toString());
            }
        });
        stream1Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                TestContent.SetPropertyString( "stream1", stream1Text.getText().toString() );
            }
        });
        stream2Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                TestContent.SetPropertyString( "stream2", stream2Text.getText().toString() );
            }
        });

        swapButton = (Button)rootView.findViewById(R.id.swap_btn);
        swapButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    String s1 = TestContent.GetPropertyString("stream1");
                    String s2 = TestContent.GetPropertyString("stream2");

                    stream1Text.setText(s2);
                    stream2Text.setText(s1);
                }
                return true;
            }
        });

        debugCheck = (CheckBox)rootView.findViewById(R.id.debugCheck);
        videoCheck = (CheckBox)rootView.findViewById(R.id.videoCheck);
        audioCheck = (CheckBox)rootView.findViewById(R.id.audioCheck);

        debugCheck.setChecked((TestContent.GetPropertyString("debug_view").equals("true")));
        videoCheck.setChecked((TestContent.GetPropertyString("video_on").equals("true")));
        audioCheck.setChecked((TestContent.GetPropertyString("audio_on").equals("true")));

        debugCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) TestContent.SetPropertyString("debug_view", "true");
                else TestContent.SetPropertyString("debug_view", "false");
            }
        });
        videoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) TestContent.SetPropertyString("video_on", "true");
                else TestContent.SetPropertyString("video_on", "false");
            }
        });
        audioCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) TestContent.SetPropertyString("audio_on", "true");
                else TestContent.SetPropertyString("audio_on", "false");
            }
        });

        liveMode.setChecked((TestContent.GetPropertyString("record_mode").equals("Live")));
        recordMode.setChecked((TestContent.GetPropertyString("record_mode").equals("Record")));
        appendMode.setChecked((TestContent.GetPropertyString("record_mode").equals("Append")));

        radioRecordMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == liveMode.getId()) {
                    String v = String.valueOf(R5Stream.RecordType.Live);
                    TestContent.SetPropertyString( "record_mode", v);
                } else if (checkedId == recordMode.getId()) {
                    TestContent.SetPropertyString( "record_mode", String.valueOf(R5Stream.RecordType.Record));
                } else if(checkedId == appendMode.getId()) {
                    TestContent.SetPropertyString( "record_mode", String.valueOf(R5Stream.RecordType.Append));
                }
            }
        });

        return  rootView;
    }
}
