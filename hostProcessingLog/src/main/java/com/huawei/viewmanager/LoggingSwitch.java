package com.huawei.viewmanager;

import huawei.shared.CmdOper;
import android.widget.ToggleButton;

public class LoggingSwitch {
	private ToggleButton logSwitch;

	public LoggingSwitch(ToggleButton button) {
		logSwitch = button;
	}

	/*
	private void saveLogToSDCard() {
		Intent intent = new Intent();
		intent.setClass(main_act, RenameActivity.class);
		main_act.startActivity(intent);

	}*/

	public boolean isLogging() {
		return CmdOper.get_savedata_mode();
	}

	public String getLogStatus() {
		if (CmdOper.get_savedata_mode()) {
			return "log is running";
		} else {
			return "log has been stoped";
		}
	}

	public void setStatus(boolean status) {
		if (logSwitch != null) {
			logSwitch.setChecked(status);
		}
	}

	public String logEnforce() {
		String str;
		try {
			str = CmdOper.enforceAptouchLog();
		} catch (Exception e) {
			str = "execute fail!";
			e.printStackTrace();
		}
		return str;
	}

	public String logOn() {
		String str;
		try {
			str = CmdOper.startAptouchLog();
		} catch (Exception e) {
			str = "execute fail!";
			e.printStackTrace();
		}
		return str;
	}

	public String logOff() {
		String str;
		try {
			str = CmdOper.endAptouchLog();
		} catch (Exception e) {
			str = "execute fail!";
			e.printStackTrace();
		}
		return str;
	}
}
