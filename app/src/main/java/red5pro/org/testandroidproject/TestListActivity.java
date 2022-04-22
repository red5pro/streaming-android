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
package red5pro.org.testandroidproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Message;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import red5pro.org.testandroidproject.tests.*;
import red5pro.org.testandroidproject.tests.ParamTable.ParamTable;
import red5pro.org.testandroidproject.tests.PublishTest.PublishTest;


/**
 * An activity representing a list of Tests. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TestDetailFragment} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TestListFragment} and the item details
 * (if present) is a {@link TestDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link TestListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class TestListActivity extends Activity
        implements TestListFragment.Callbacks, PublishTestListener, ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private TestDetailFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        if (findViewById(R.id.test_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((TestListFragment) getFragmentManager()
                    .findFragmentById(R.id.test_list))
                    .setActivateOnItemClick(true);
        }
        else {
            TestListFragment frag = new TestListFragment();

            getFragmentManager().beginTransaction()
                    .replace(R.id.test_list_container, frag)
                    .commit();
        }

        // TODO: If exposing deep links into your app, handle intents here.

        //onItemSelected("0");

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PermissionChecker.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {

                getFragmentManager().beginTransaction().addToBackStack(null).commit();
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.CAMERA,
                                android.Manifest.permission.RECORD_AUDIO,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, 1337);
            }
        }

        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}

	@Override
	public void onAddConnectionParams () {
		try {
			getFragmentManager().beginTransaction()
					.replace(R.id.test_list_container, new ParamTable())
					.addToBackStack(null)
					.commit();
//			mIsParamsFragmentActive = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Callback method from {@link TestListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {


        int _id = Integer.parseInt(id);

        TestContent.SetTestItem( _id );

        Bundle arguments = new Bundle();
        arguments.putString(TestDetailFragment.ARG_ITEM_ID, id);

        if( fragment != null )
            onBackPressed();

        try {
            String className = TestContent.ITEMS.get( _id ).className;
            Class testClass = Class.forName( "red5pro.org.testandroidproject.tests." + className + "." + className );
            fragment = (TestDetailFragment)testClass.getConstructors()[0].newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            fragment = new TestDetailFragment();
        }
        fragment.setArguments(arguments);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            getFragmentManager().beginTransaction()
                    .replace(R.id.test_detail_container, fragment)
                    .addToBackStack(null)
                    .commit();

        } else {
            // In single-pane mode, replace the list with the fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.test_list_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        if(fragment == null || fragment.shouldClean()) {

            if (fragment != null && fragment.isPublisherTest()) {
                PublishTest pFragment = (PublishTest)fragment;
                pFragment.stopPublish(this);
            }
            fragment = null;

            super.onBackPressed();
        }
    }

    private AlertDialog bufferDialog;
    private boolean requiresBufferDialog = false;
    private final int BUFFEREVT = 1000;
    private Handler bufferHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BUFFEREVT:
                    if (!requiresBufferDialog) return;

                    bufferDialog = new AlertDialog.Builder(TestListActivity.this).create();
                    bufferDialog.setTitle("Alert");
                    bufferDialog.setMessage("Publisher Is Finishing Broadcast.\nPlease wait to start another broadcast.");
                    bufferDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener()

                            {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }

                    );
                    bufferDialog.show();
            }
        }
    };

    @Override
    public void onPublishFlushBufferStart() {
        // show alert.
        requiresBufferDialog = true;
        Message msg = bufferHandler.obtainMessage(BUFFEREVT);
        bufferHandler.sendMessageDelayed(msg, 500);
    }

    @Override
    public void onPublishFlushBufferComplete() {
        requiresBufferDialog = false;
        if (bufferDialog != null) {
            bufferDialog.dismiss();
            bufferDialog = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        Log.d("TestListActivity", "config changed.");
        Fragment test = getFragmentManager().findFragmentByTag("test");
        if (test != null) {
            test.onConfigurationChanged(config);
        }

        super.onConfigurationChanged(config);
    }
}
