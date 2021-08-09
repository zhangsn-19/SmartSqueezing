package com.huawei.hostprocessinglog;

import huawei.shared.CmdOper;
import huawei.utility.ActionDialog;
import huawei.utility.FunctionInterface;
import huawei.utility.SuspendableThread;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.huawei.viewmanager.LoggingSwitch;

public class LogCmdActivity extends THPActivity {
	private long lastTriggerFailTime = 0;
	private LoggingSwitch logger = new LoggingSwitch(null);
	private TextView logstatus;
	private TextView loglist;
	private SuspendableThread thread;
	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logcmd_activity);
		hiddenStatusBar();
		logstatus = (TextView) findViewById(R.id.cmd_excute_result);
		loglist = (TextView) findViewById(R.id.thplogs);
		if (m_isPermissive) {
			startStatusUpdateThread();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
		if (m_isPermissive) {
			thread.setSuspend(false);
		}
		Log.i("thpapk", "on resum");
	}

	public void onPause() {
		super.onPause();
		if (m_isPermissive) {
			thread.setSuspend(true);
		}
	}

	@Override
	public boolean onVolumeUpKey() {
		if (!m_isPermissive) {
			return true;
		}
		String str = "";
		if (logger.isLogging() == true) {
			str = logger.logOff();
			lastTriggerFailTime = 0;
		} else {
			long curTime = System.currentTimeMillis();
			if (curTime - lastTriggerFailTime < 5 * 1000) {
				logger.logEnforce();
				str = "this part could not be recognized due to system error";
			} else {
				str = logger.logOn();
			}
			lastTriggerFailTime = 0;
			if (logger.isLogging() == false) {
				lastTriggerFailTime = curTime;
			}
		}
		updateView();
		logstatus.setText(str);
		return true;
	}

	@Override
	public boolean onVolumeDownKey() {
		comfirmCacheLog();
		return true;
	}

	private void updateView() {
		if (!m_isPermissive) {
			return;
		}
		logstatus.setText(logger.getLogStatus());
		loglist.setText(CmdOper.getThpLogList());
	}

	private void comfirmCacheLog() {
		if (!m_isPermissive) {
			return;
		}
		ActionDialog dialog = new ActionDialog();
		dialog.setTitle("start to dumplog:");
		dialog.setConfirm(new FunctionInterface() {
			@Override
			public void action() throws Exception {
				CmdOper.cacheAptouchLog();
				updateView();
			}
		});
		dialog.setCancel(new FunctionInterface() {
			@Override
			public void action() throws Exception {
			}
		});
		dialog.show(this);
	}

	private void startStatusUpdateThread() {
		thread = new SuspendableThread(new FunctionInterface() {
			public void action() throws Exception {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.post(new Runnable() {
					public void run() {
						updateView();
					}
				});
			}
		});
		thread.start();
	}
}
