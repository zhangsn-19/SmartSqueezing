package com.huawei.z.abandon;

import huawei.components.SharedMainActivity;
import huawei.utility.ActionDialog;
import huawei.utility.FunctionInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.hostprocessinglog.R;
import com.huawei.utility.FileManager;
import com.huawei.utility.TouchEventSwitchLayout;
import com.huawei.viewmanager.ConfigContainer;

public class ConfigActivity extends SharedMainActivity {
	private FileManager fileManager;
	private ConfigContainer config;
	private EditText editConfig;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_activity);
		hiddenStatusBar();

		fileManager = (FileManager) shardData.get("FileManager");
		config = new ConfigContainer(fileManager);

		setOnClickListener(R.id.configEditable, new Editable());
		setOnClickListener(R.id.configResetFile, new ResetConfig());
		setOnClickListener(R.id.configSaveFile, new SaveConfig());
		setOnClickListener(R.id.funcRebootPhone, new RebootPhone());
		setOnClickListener(R.id.funcRebootDaemon, new RebootAptouch());

		editConfig = (EditText) findViewById(R.id.configEdit);
		editConfig.setText(config.loadConfigFile(this));
		((TouchEventSwitchLayout) findViewById(R.id.configEditLayout)).disableTouch = true;
	}

	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}

	@Override
	public boolean onDoubleClickVolumeDownKey() {
		finish();
		return true;
	}

	class Editable extends FunctionInterface {
		@Override
		public void action() throws Exception {
			((TouchEventSwitchLayout) findViewById(R.id.configEditLayout)).disableTouch = false;
		}
	}

	class ResetConfig extends FunctionInterface {
		public void action() throws Exception {
			editConfig.setText(config.genDefaultConfig());
		}
	}

	class SaveConfig extends FunctionInterface {
		@Override
		public void action() {
			String tip = config.saveFile(editConfig.getText().toString(),
					ConfigActivity.this);
			Toast.makeText(ConfigActivity.this, tip, Toast.LENGTH_SHORT).show();
		}
	}

	class RebootAptouch extends FunctionInterface {
		@Override
		public void action() {
			ActionDialog dialog = new ActionDialog();
			dialog.setConfirm(new RebootAct()).show(ConfigActivity.this);
		}

		class RebootAct extends FunctionInterface {
			@Override
			public void action() {
				try {
					fileManager.getCmdOper().rebootAptouch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	class RebootPhone extends FunctionInterface {
		@Override
		public void action() {
			ActionDialog dialog = new ActionDialog();
			dialog.setConfirm(new RebootAct()).show(ConfigActivity.this);
		}

		class RebootAct extends FunctionInterface {
			@Override
			public void action() {
				try {
					fileManager.getCmdOper().reboot();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
