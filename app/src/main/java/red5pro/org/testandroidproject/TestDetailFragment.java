package red5pro.org.testandroidproject;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.red5pro.streaming.event.R5StreamEvent;
import com.red5pro.streaming.event.R5StreamListener;

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
}
