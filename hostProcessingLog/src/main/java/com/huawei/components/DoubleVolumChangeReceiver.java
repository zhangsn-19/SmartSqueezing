package huawei.components;

import huawei.shared.ConstData;
import huawei.utility.MultiClick;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public abstract class DoubleVolumChangeReceiver extends BroadcastReceiver {
	private static MultiClick multiClick = new MultiClick(2, 500);
	private static ArrayList<Long> times = new ArrayList<Long>(4);

	@Override
	// A BroadcastReceiver object is only valid for the duration of the call to
	// onReceive(Context, Intent). Once your code returns from this function,
	// the system considers the object to be finished and no longer active.
	/******************/
	// In particular, you may not show a dialog or bind to a service from within
	// a BroadcastReceiver. For the former, you should instead use the
	// NotificationManager API. For the latter, you can use
	// Context.startService() to send a command to the service.
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		Log.v(ConstData.logtag, "WakeupReceiver receive braodcast: " + action);

		try {
			handleVolumeKey(context, intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void doubleClickVolumDown(Context context);

	/*
	 * android系统各种音量的获取与设置：http://www.cnblogs.com/maxinliang/archive/2013/07/02/
	 * 3167212.html 分为多种类型。特别需要注意的是，音量变化的广播，不一定是按键产生的，也可能是音量滑块产生的。
	 */
	private void printAllVolume(Context context) {
		AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int volumeSTREAM_SYSTEM = mAudioManager
				.getStreamVolume(AudioManager.STREAM_SYSTEM);
		int volumeSTREAM_VOICE_CALL = mAudioManager
				.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		int volumeSTREAM_MUSIC = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		int volumeSTREAM_RING = mAudioManager
				.getStreamVolume(AudioManager.STREAM_RING);
		int volumeSTREAM_ALARM = mAudioManager
				.getStreamVolume(AudioManager.STREAM_ALARM);

		Log.v(ConstData.logtag, "STREAM_SYSTEM: " + volumeSTREAM_SYSTEM
				+ ", STREAM_VOICE_CALL: " + volumeSTREAM_VOICE_CALL
				+ ", STREAM_MUSIC: " + volumeSTREAM_MUSIC + " STREAM_RING: "
				+ volumeSTREAM_RING + ", STREAM_ALARM: " + volumeSTREAM_ALARM);
	}

	private void abandonCode(Context context) {
		boolean abandon = true;
		if (!abandon) {
			long time = System.currentTimeMillis();
			times.add(time);
			Log.v(ConstData.logtag, "" + times.size());
			if (times.size() >= 4) {
				long before = times.get(0);
				long end = times.get(3);
				if (end - before < 350) {
					doubleClickVolumDown(context);
				}
				times.remove(0);
				times.remove(0);
			}
		}
	}

	private void handleVolumeKey(Context context, Intent intent) {
		multiClick.click();
		if (multiClick.getMultiClick()) {
			Toast.makeText(context, "响应音量双击，读取容值，正在加载数据...", Toast.LENGTH_SHORT)
					.show();
			doubleClickVolumDown(context);
		}
		Log.v(ConstData.logtag,
				"handleVolumeKey, " + System.currentTimeMillis());
		printAllVolume(context);
		abandonCode(context);
	}

}
