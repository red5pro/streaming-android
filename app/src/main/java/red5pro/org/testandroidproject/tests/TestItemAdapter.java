package red5pro.org.testandroidproject.tests;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import red5pro.org.testandroidproject.R;

public class TestItemAdapter extends ArrayAdapter<TestContent.TestItem> {
    public TestItemAdapter(Context context, List<TestContent.TestItem> tests) {
        super(context, 0, tests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TestContent.TestItem test = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.test_list_item, parent, false);
        }
        // Lookup view for data population
//        TextView title = (TextView) convertView.findViewWithTag("textfield");
        TextView title = (TextView) convertView.findViewById(R.id.txt);
        // Populate the data into the template view using the data object
        title.setText(test.content);
        title.setContentDescription(test.title);
        Resources res = title.getResources();
        // Return the completed view to render on   screen
        return convertView;
    }
}