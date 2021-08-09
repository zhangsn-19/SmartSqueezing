package com.huawei.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huawei.hostprocessinglog.MainActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.v("lance", "FaceDetectReceiver receive braodcast: " + action);
		/* �ڽ��յ������㲥ʱ���ٵ��� MainActivity��ʾ���� */
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Intent i = new Intent(context, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
