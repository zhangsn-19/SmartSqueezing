
package com.huawei.hostprocessinglog;
import com.huawei.utility.THPDiffData;

import huawei.shared.CapData;
import huawei.shared.CmdOper;
import huawei.utility.ActionDialog;
import huawei.utility.DiffCanvasDraw;
import huawei.utility.FunctionInterface;
import huawei.utility.SuspendableThread;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CapDataActivity extends THPActivity {
	Handler myHandler1 = new Handler();
	private boolean isDebugRaw = false;
	private DiffCanvasDraw mydraw;
	private SuspendableThread thread = null;
	int z=0;
	int touchx=0;
	int touchy=0;
	int frame_num=0;
	int[][][] data_arr_list = new int[15][][];//
//	spot[] spots=new spot[100];

	int spotnum=1;

	private int[][] getDataArray() {
		CapData data;
		if (isDebugRaw) {
			data = new CapData(CmdOper.getRawData());
		} else {
			THPDiffData diff = new THPDiffData(CmdOper.getDiffData());
			data = diff.getData();
		}
		int[][] array = data.getDataArray();
		return data.getDataArray();
	}

    //FileOutputStream out1 = null;

	PrintWriter out = null;
    String FILENAME = "/sdcard/capacity.txt";
	PrintWriter out2 = null;
	String FILENAME2 = "/sdcard/touch.txt";
	int startflag=0;



	public void button(float x,float y){

		if(x>235&&x<470&&y>2100) {//
			//mydraw.record=1;
			startflag=1;
			out.print("start\n");
			out.print("current step:"+mydraw.step+"\n");
			out.flush();
		}
		else if(x>470&&x<705&&y>2100){//
			Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
			//mydraw.record=0;
			startflag=0;
			out.print("fail\n");
			out.flush();
		}
		else if(x>705&&x<940&&y>2100){//�ύ ��һ��
			//mydraw.record=0;
			startflag=0;
			mydraw.step +=1;
			out.print("end\n");
			out.flush();
		}
		else if(x>470&&x<705&&y<170){
			if(mydraw.ifdrawnum==0){
				mydraw.ifdrawnum=1;
			}
			else if(mydraw.ifdrawnum==1){
				mydraw.ifdrawnum=0;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		//x 280 820 top 2100
		float corx,cory;
		corx=event.getX();
		cory=event.getY();
		mydraw.xx=corx;
		mydraw.yy=cory;

		long temp=System.currentTimeMillis();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				button(corx,cory);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:

				if(startflag==1) {
					out2.print(temp + "" + "\n");
					out2.print(corx + "" + "\n");
					out2.print(cory + "" + "\n");
				}
				break;

			case MotionEvent.ACTION_UP:

				out2.flush();
				break;

			case MotionEvent.ACTION_POINTER_UP:

				break;

			case MotionEvent.ACTION_MOVE:

				if(startflag==1) {
					out2.print(temp + "" + "\n");
					out2.print(corx + "" + "\n");
					out2.print(cory + "" + "\n");
				}
				break;
		}
		mydraw.invalidate();
		return true;
	}


	public void writeToApp(int[][] data){
		String[][] arrdata=new String[37][19];
		z++;//
		long temp=System.currentTimeMillis();

		try {
			String num =z+"第几次刷";
			out.print(num);
			out.print('\n');
			for (int ii = 0; ii < data.length-1; ii++) {
				for (int jj = 0; jj < data[ii].length-1; jj++) {
					out.print(data[ii][jj]);
					out.print(' ');
				}
				out.print('\n');
			}

			out.print(temp);
			out.print('\n');
			out.print('\n');
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	long lastTime = 0;

	private void startStatusUpdateThread() {
		thread = new SuspendableThread(new FunctionInterface() {
			public void action() throws Exception {
				try {
					//Thread.sleep(50);
					try {
						mydraw.updateData(getDataArray(), isDebugRaw);

						writeToApp(getDataArray());


					} catch (Exception e) {
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				myHandler1.post(new Runnable() {
					public void run() {
						mydraw.invalidate();
					}
				});
			}
		});
		thread.start();

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!m_isPermissive) {
			return;
		}

		try {
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FILENAME, false)), true);

			out2 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FILENAME2, false)), true);

			Log.v("YX", "Error: " + "success!!!!");
		} catch(Exception e) {
			Log.v("YX", "Error: " + e.getMessage());
		}


		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.capdata_activity);
		hiddenStatusBar();
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.capdata_layout);
		isDebugRaw = CmdOper.isDebugRaw();
		mydraw = new DiffCanvasDraw(this, getDataArray());
		linearLayout.addView(mydraw);

		mydraw.pw = out2;

		startStatusUpdateThread();


		if (isDebugRaw) {
			mydraw.rawBase = 32768;
			comfirmRawDebug();
		}
	}


	private void comfirmRawDebug() {
		ActionDialog dialog = new ActionDialog();
		dialog.setTitle("" + mydraw.rawBase + "");
		dialog.show(this);
	}

	public void onResume() {
		super.onResume();
		isDebugRaw = CmdOper.isDebugRaw();
		if (m_isPermissive) {
			thread.setSuspend(false);
		}
	}

	public void onPause() {
		super.onPause();
		if (m_isPermissive) {
			thread.setSuspend(true);
		}
	}

	@Override
	public boolean onVolumeUpKey() {
		finish();
		return super.onVolumeUpKey();
	}

	@Override
	public boolean onVolumeDownKey() {
		finish();
		return super.onVolumeDownKey();
	}

	@Override
	public void onDestroy() {
		if (out != null) {
			out.flush();
			out.close();
		}
		if (out2 != null) {
			out2.flush();
			out2.close();
		}
		super.onDestroy();
	}
}
