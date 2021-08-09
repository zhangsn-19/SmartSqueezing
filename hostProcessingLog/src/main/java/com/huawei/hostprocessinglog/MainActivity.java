package com.huawei.hostprocessinglog;

import huawei.shared.CmdOper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ToggleButton;

import com.huawei.utility.FileManager;
import com.huawei.viewmanager.LoggingSwitch;
import com.huawei.viewmanager.VersionStatus;

public class MainActivity extends THPActivity {
	private FileManager fileManager = new FileManager();
	private LoggingSwitch logger;
	private VersionStatus version;
	private boolean isHostProcessing = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hiddenStatusBar();
		CmdOper.initTpTool();
		isHostProcessing = CmdOper.isHostProcessing();

		if (isHostProcessing) {
			setContentView(R.layout.activity_main);
			version = new VersionStatus(this, fileManager);
			logger = new LoggingSwitch(
					(ToggleButton) findViewById(R.id.logSwitch));
			shardData.put("FileManager", fileManager);
		} else {
			setContentView(R.layout.empty_layout);
			notHostProcessing();
		}
	}

	private void notHostProcessing() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton(getString(R.string.notHostProcessing),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.show();
	}

	@Override
	protected void onResume() {
		if (isHostProcessing) {
			freshStatus();
			version.onResume();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (isHostProcessing) {
			version.onPause();
		}
		super.onPause();
	}

	private void freshStatus() {
		logger.setStatus(logger.isLogging());
		version.freshStatus();
	}

	@Override
	public boolean onVolumeUpKey() {
		if (isHostProcessing) {
			Intent intent = new Intent();
			intent.setClass(this, LogCmdActivity.class);
			startActivity(intent);
		}
		return true;
	}

	@Override
	public boolean onVolumeDownKey() {
		if (isHostProcessing) {
			Intent intent = new Intent();
			intent.setClass(this, CapDataActivity.class);
			startActivity(intent);
		}
		return true;
	}

	

}
