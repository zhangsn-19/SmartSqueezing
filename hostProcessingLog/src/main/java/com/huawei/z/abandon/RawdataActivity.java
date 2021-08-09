package com.huawei.z.abandon;

import huawei.components.SharedMainActivity;
import huawei.shared.CmdOper;
import huawei.utility.AssistUI;
import huawei.utility.FunctionInterface;
import huawei.utility.HandlerContainer;
import huawei.utility.SuspendableThread;

import java.util.ArrayList;

import com.huawei.hostprocessinglog.CapDataActivity;
import com.huawei.hostprocessinglog.R;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RawdataActivity extends SharedMainActivity {
	private SuspendableThread thread;
	private HandlerContainer handler = new HandlerContainer();
	// CapdataAdapter adapter = new CapdataAdapter()
	//private TextView roidata;
	private TextView diffdata;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rawdatashow_activity);
		hiddenStatusBar();

		// roidata = (TextView) findViewById(R.id.roidata);
		diffdata = (TextView) findViewById(R.id.diffdata);

		// GridView gview = (GridView) findViewById(R.id.diffdata);
		// gview.setAdapter(adapter);
		// gview.setVerticalScrollBarEnabled(false);

		startStatusUpdateThread();
	}

	class UpdateAlgoView extends FunctionInterface {
		@Override
		public void action() {
			// String s1 = native_get_roidata_str();
			String s2 = CmdOper.getDiffData();

			// roidata.setText(s1);
			diffdata.setText(s2);
			// System.out.println(System.currentTimeMillis());

			// adapter.updateData(s2);
			// adapter.notifyDataSetChanged();

		}
	}

	private void startStatusUpdateThread() {
		thread = new SuspendableThread(new FunctionInterface() {
			public void action() throws Exception {
				handler.sendCallbackMessage(new UpdateAlgoView());
				// AssistUI.threadSleep(100);
				AssistUI.threadSleep(227);
			}
		});
		thread.start();
	}

	@Override
	protected void onResume() {
		thread.setSuspend(false);
		super.onResume();
	}

	@Override
	protected void onPause() {
		thread.setSuspend(true);
		super.onPause();
	}

	@Override
	public boolean onVolumeUpKey() {
		Intent intent = new Intent();
		intent.setClass(this, CapDataActivity.class);
		startActivity(intent);
		return super.onVolumeUpKey();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// handler.sendCallbackMessage(new UpdateAlgoView());
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// handler.sendCallbackMessage(new UpdateAlgoView());
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		return false;
	}

	public class CapdataAdapter extends BaseAdapter {
		private ArrayList<String> list;

		public CapdataAdapter() {
			list = new ArrayList<String>();
		}

		public void updateData(String content) {
			list.clear();
			String[] strs = content.split("[\\t\\n]");
			for (String str : strs) {
				str = str.trim();
				if (str.length() > 0) {
					list.add(str);
				}
			}

			String[][] data = new String[16][28];
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 28; j++) {
					data[i][j] = list.get(i * 28 + j);
				}
			}
			list.clear();

			String[][] result = new String[28][16];
			for (int i = 0; i < 28; i++) {
				for (int j = 0; j < 16; j++) {
					result[i][j] = data[j][i];
				}
			}

			for (int i = 0; i < 28; i++) {
				for (int j = 0; j < 16; j++) {
					String s = result[i][j];
					list.add(s);
				}
			}
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(getApplicationContext());
			tv.setText(list.get(position));
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(7);
			tv.setHeight(68);
			int a = Integer.parseInt(list.get(position));
			tv.setBackgroundColor(valuetoColor(a));
			return tv;
		}

		private int valuetoColor(int val) {
			int MaxVal = 3000;
			int MinVal = -500;
			int BaseVal = 0;

			if (val >= BaseVal) {
				if (val > MaxVal) {
					val = MaxVal;
				}
				val = ((val - BaseVal) * 128) / (MaxVal - (BaseVal + 1));
				val += 127;
			} else {
				if (val < MinVal) {
					val = MinVal;
				}
				val = ((val - MinVal) * 128) / (BaseVal - (MinVal + 1));
			}
			int r = val;
			int g = val > 127 ? 2 * (255 - val) : 2 * val;
			int b = 255 - val;
			return Color.rgb(r, g, b);
		}
	}
}
