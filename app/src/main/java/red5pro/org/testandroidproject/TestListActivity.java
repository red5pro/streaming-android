package red5pro.org.testandroidproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Switch;

import junit.framework.Test;

import red5pro.org.testandroidproject.tests.*;


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
        implements TestListFragment.Callbacks {

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
                    .replace(R.id.test_detail_container, fragment, "test")
                    .addToBackStack(null)
                    .commit();

        } else {
            // In single-pane mode, replace the list with the fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.test_list_container, fragment, "test")
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
        super.onBackPressed();

        fragment = null;
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
