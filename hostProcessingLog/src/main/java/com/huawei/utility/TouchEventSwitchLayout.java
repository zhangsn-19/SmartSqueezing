package com.huawei.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class TouchEventSwitchLayout extends LinearLayout {
	public boolean disableTouch = false;

	public TouchEventSwitchLayout(Context context) {
		super(context);
	}

	public TouchEventSwitchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// if you want to interrupt all touch, make the return value true;
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (disableTouch) {
			System.out.println("disable touch");
			return true;
		} else {
			return super.dispatchTouchEvent(ev);
		}
	}
}
