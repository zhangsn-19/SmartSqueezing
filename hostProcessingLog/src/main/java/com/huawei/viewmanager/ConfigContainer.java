package com.huawei.viewmanager;

import huawei.shared.FileOper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.huawei.hostprocessinglog.R;
import com.huawei.utility.FileManager;

public class ConfigContainer {
	protected FileManager fileManager;

	public ConfigContainer(FileManager file) {
		fileManager = file;
		fileManager.initLogFile();
	}

	public String loadConfigFile(Activity act) {
		String content = fileManager.readFile();
		if (content.trim().length() == 0) {
			content = genDefaultConfig();
			Toast.makeText(act, act.getString(R.string.no_config_file),
					Toast.LENGTH_LONG).show();
		}
		return content;
	}

	public String saveFile(String content, Context act) {
		String tip = act.getString(R.string.file_save_fail);
		try {
			String name = fileManager.saveFile(content);
			tip = name;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tip;
	}

	private String genItem(String name, String value) {
		return name + "=" + value;
	}

	private ArrayList<String> genComment() {
		ArrayList<String> content = new ArrayList<String>();
		content.add("------");
		content.add("------");
		content.add("#frame_log_type(inclusive): CHAR, BIN");
		content.add("#frame_log_timing(exclusive): CALC, GETFRAME");
		content.add("#frame_log_trigger(exclusive): MANUAL, AUTO");
		content.add("#log_level(inclusive): ERROR, INFO, DEBUG");
		content.add("#log_type: FILE");
		return content;
	}

	public String genDefaultConfig() {
		ArrayList<String> content = new ArrayList<String>();
		content.add(genItem("frame_log_enb", "1"));
		content.add(genItem("frame_log_type", "CHAR"));
		content.add(genItem("frame_log_timing", "CALC"));
		content.add(genItem("frame_log_trigger", "MANUAL"));
		content.add(genItem("#frame_log_trigger", "AUTO"));
		content.add(genItem("log_type", "FILE"));
		content.addAll(genComment());
		return FileOper.listToStr(content);
	}
}
