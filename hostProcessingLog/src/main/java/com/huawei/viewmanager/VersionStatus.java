package com.huawei.viewmanager;

import huawei.utility.AssistUI;
import huawei.utility.FunctionInterface;
import huawei.utility.HandlerContainer;
import huawei.utility.SuspendableThread;
import android.view.View;
import android.widget.TextView;

import com.huawei.hostprocessinglog.MainActivity;
import com.huawei.hostprocessinglog.R;
import com.huawei.utility.FileManager;

public class VersionStatus {
	private FileManager fileManager;
	private HandlerContainer handler = new HandlerContainer();
	private SuspendableThread thread;

	private TextView apTouchStatusView;
	private TextView seLinux;
	private String apTouchStatusText;

	public VersionStatus(MainActivity act, FileManager file) {
		fileManager = file;
		apTouchStatusView = (TextView) act.findViewById(R.id.apTouchStatus);
		seLinux = (TextView) act.findViewById(R.id.tv_selinux);
		startStatusUpdateThread();
	}

	class UpdateAlgoView extends FunctionInterface {
		@Override
		public void action() {
			apTouchStatusView.setText(apTouchStatusText);
		}
	}

	private void startStatusUpdateThread() {
		thread = new SuspendableThread(new FunctionInterface() {
			public void action() throws Exception {
				if (fileManager.isSeLinuxOpen()) {
					thread.setSuspend(true);
					apTouchStatusText = fileManager.getVersionInfoByProp();
				} else {
					apTouchStatusText = fileManager.setAPWorkingStatusInfo();
				}
				handler.sendCallbackMessage(new UpdateAlgoView());
				AssistUI.threadSleep(100);
			}
		});
		thread.start();
	}

	public void freshStatus() {
		if (fileManager.isSeLinuxOpen()) {
			seLinux.setVisibility(View.VISIBLE);
		} else {
			seLinux.setVisibility(View.INVISIBLE);
		}
	}

	public void onResume() {
		thread.setSuspend(false);
	}

	public void onPause() {
		thread.setSuspend(true);
	}
}
