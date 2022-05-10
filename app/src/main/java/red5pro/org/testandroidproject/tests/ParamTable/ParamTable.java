package red5pro.org.testandroidproject.tests.ParamTable;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import red5pro.org.testandroidproject.R;
import red5pro.org.testandroidproject.tests.TestContent;

public class ParamTable extends Fragment {

	TableLayout table;
	Button addButton;
	LayoutInflater inflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_param_table, container, false);

		table = (TableLayout) rootView.findViewById(R.id.param_table);
		addButton = (Button) rootView.findViewById(R.id.param_add_btn);

		addButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onAddTouch(event);
			}
		});

		inflateTableWithConnectionParams(TestContent.connectionParams);

		return rootView;

	}

	private void serializeConnectionParams (ViewGroup group) {
		TestContent.connectionParams.clear();
		int length = group.getChildCount();
		for (int i = 0; i < length; i++) {
			View view = group.getChildAt(i);
			if (view instanceof TableRow) {
				TableRow row = (TableRow) view;
				EditText nameField = (EditText) row.findViewById(R.id.name_field);
				EditText valueField = (EditText) row.findViewById(R.id.value_field);
				String name = nameField.getText().toString();
				String value = valueField.getText().toString();
				if (!name.isEmpty() && !value.isEmpty()) {
					TestContent.connectionParams.put(name, value);
				}
			}
		}
	}

	private void inflateTableWithConnectionParams (HashMap<String, String> params) {
		Iterator iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapElement = (Map.Entry)iterator.next();
			addFilledEntry((String)mapElement.getKey(), (String)mapElement.getValue());
		}
	}

	private void addFilledEntry (String name, String value) {
		final View row = inflater.inflate(R.layout.param_table_row_editable, null,false);
		EditText nameField = (EditText) row.findViewById(R.id.name_field);
		EditText valueField = (EditText) row.findViewById(R.id.value_field);
		Button removeButton = (Button) row.findViewById(R.id.remove_button);
		removeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				table.removeView(row);
			}
		});
		nameField.setText(name);
		valueField.setText(value);
		table.addView(row);
	}

	private void addEmptyEntry () {
		final View row = inflater.inflate(R.layout.param_table_row_editable, null,false);
		Button removeButton = (Button) row.findViewById(R.id.remove_button);
		removeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				table.removeView(row);
			}
		});
		table.addView(row);
	}

	private boolean onAddTouch(MotionEvent e ) {
		if( e.getAction() == MotionEvent.ACTION_DOWN ) {
			addEmptyEntry();
		}
		return true;
	}

	@Override
	public void onDestroy () {
		serializeConnectionParams(table);
		super.onDestroy();
	}

}
