package com.huawei.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class TouchEventDisableLayout extends LinearLayout {
	public TouchEventDisableLayout(Context context) {
		super(context);
	}

	public TouchEventDisableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// if you want to interrupt all touch, make the return value true;
	public boolean dispatchTouchEvent(MotionEvent ev) {
		System.out.println("disable touch");
		return true;
	}
}
