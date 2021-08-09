package com.huawei.utility;

import huawei.utility.AssistUI;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class InsertAutoCompleteTextView extends AutoCompleteTextView {
	public InsertAutoCompleteTextView(Context context) {
		super(context);
	}

	public InsertAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InsertAutoCompleteTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void performFiltering(CharSequence text, int keyCode) {
	}

	@Override
	protected void replaceText(CharSequence text) {
		AssistUI.insterStrToEditText(this, text);
	}

	@Override
	public boolean enoughToFilter() {
		return true;
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		// by doing this the list will show at the beginning
		if (focused) {
			// performFiltering(getText(), 0);
		}
	}

	public void performFiltering() {
		super.performFiltering(getText(), 0);
	}
}
