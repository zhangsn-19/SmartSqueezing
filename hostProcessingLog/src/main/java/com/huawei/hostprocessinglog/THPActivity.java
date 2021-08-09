package com.huawei.hostprocessinglog;

import android.os.Bundle;
import huawei.components.SharedMainActivity;
import huawei.shared.CmdOper;
import huawei.utility.ActionDialog;
import huawei.utility.FunctionInterface;

public class THPActivity extends SharedMainActivity {
	protected boolean m_isPermissive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hiddenStatusBar();
		CmdOper.initTpTool();
		m_isPermissive = isPermissive();

		if (!m_isPermissive) {
			comfirmSetPermissive();
		}
	}

	public boolean isPermissive() {
		String enforce = "";
		try {
			enforce = CmdOper.getEnforce();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enforce.contains("Permissive");
	}

	private void comfirmSetPermissive() {
		ActionDialog dialog = new ActionDialog();
		dialog.setTitle("«Î÷¥––√¸¡Óπÿ±’SeLinux:\nadb shell setenforce 0");
		dialog.setConfirm(new FunctionInterface() {
			@Override
			public void action() throws Exception {
				finish();
			}
		});
		dialog.show(this);
	}
}
