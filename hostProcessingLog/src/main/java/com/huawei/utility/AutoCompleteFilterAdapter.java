package com.huawei.utility;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.huawei.hostprocessinglog.R;
import com.huawei.z.abandon.RenameActivity;

public class AutoCompleteFilterAdapter extends BaseAdapter implements
		Filterable {
	private LayoutInflater mInflater = null;
	private int mResource;
	private ArrayList<String> mValues;
	private AutoCompleteTextView autoCompleteEdit;

	public static void associatedAutoCompleteTextView(final RenameActivity act,
			final InsertAutoCompleteTextView autoCompleteEdit) {
		AutoCompleteFilterAdapter adapter = new AutoCompleteFilterAdapter(act,
				autoCompleteEdit, R.layout.names_listitem,
				new ArrayList<String>());
		autoCompleteEdit.setAdapter(adapter);
	}

	public AutoCompleteFilterAdapter(RenameActivity act,
			AutoCompleteTextView edit, int namesListitem, List<String> list) {
		autoCompleteEdit = edit;
		mInflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = namesListitem;
		mValues = new ArrayList<String>(list);
	}

	@Override
	public int getCount() {
		return mValues.size();
	}

	@Override
	public Object getItem(int position) {
		return mValues.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource);
	}

	@Override
	public Filter getFilter() {
		return mfilter;
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View view;
		TextView text;

		if (convertView == null) {
			view = mInflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}

		try {
			text = (TextView) view;
		} catch (ClassCastException e) {
			throw new IllegalStateException(
					"ArrayAdapter requires the resource ID to be a TextView", e);
		}

		String item = (String) getItem(position);
		if (item instanceof CharSequence) {
			text.setText((CharSequence) item);
		} else {
			text.setText(item.toString());
		}

		return view;
	}

	private Filter mfilter = new Filter() {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			ArrayList<String> values = new ArrayList<String>();

			values.add("" + getSelectionStart());
			values.add("this");
			values.add("is");
			values.add("waiting");
			values.add("to");
			values.add("implement");

			results.values = values;
			results.count = values.size();
			return results;
		}

		private int getSelectionStart() {
			return autoCompleteEdit.getSelectionStart();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mValues = (ArrayList<String>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	};
}
