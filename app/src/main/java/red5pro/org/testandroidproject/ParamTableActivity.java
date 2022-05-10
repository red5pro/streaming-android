package red5pro.org.testandroidproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class ParamTableActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_param_table);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
//			Bundle arguments = new Bundle();
//			arguments.putString(TestDetailFragment.ARG_ITEM_ID,
//					getIntent().getStringExtra(TestDetailFragment.ARG_ITEM_ID));
		}
	}
}
