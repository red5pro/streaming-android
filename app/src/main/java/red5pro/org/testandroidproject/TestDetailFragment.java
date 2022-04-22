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

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.red5pro.streaming.R5Stream;

import red5pro.org.testandroidproject.tests.TestContent;

/**
 * A fragment representing a single Test detail screen.
 * This fragment is contained in a {@link TestListActivity}
 */
public class TestDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private TestContent mItem;

	/**
	 * A dummy implementation of the {@link TestListFragment.Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static TestListFragment.Callbacks sDummyCallbacks = new TestListFragment.Callbacks() {
		@Override
		public void onItemSelected(String id) {}
		@Override
		public void onAddConnectionParams() {}
	};
	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	protected TestListFragment.Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TestDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.



            //mItem = TestContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            //Load the Actual test for this TestItem using the Class property of the XML file


        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_detail, container, false);

        // Show the dummy content as text in a TextView.

        //POPULATE THE VIEW!!!


        return rootView;
    }

    public Boolean isPublisherTest () {
        return false;
    }
    public Boolean shouldClean() {
        return true;
    }
    public R5Stream.RecordType getPublishRecordType () {
        String type = TestContent.GetPropertyString("record_mode");
        if (type.equals("Record")) {
            return R5Stream.RecordType.Record;
        } else if (type.equals("Append")) {
            return R5Stream.RecordType.Append;
        }
        return R5Stream.RecordType.Live;
    }

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof TestListFragment.Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (TestListFragment.Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}
}
