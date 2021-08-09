package com.huawei.utility;

import huawei.shared.CmdOper;
import huawei.shared.FileOper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class FileManager {
	private CmdOper cmdOper = new CmdOper();;
	private String internalPath = "/data/aptouch/";
	private String sdcardPath = "/mnt/sdcard/HuaweiTP/";
	private String targetPath = sdcardPath;
	public String fileName;
	private HashSet<String> existsFiles = new HashSet<String>();
	private String configFilePath = internalPath + "config.txt";

	private String[] versionLists = new String[] { "thp.ver_algo",
			"thp.ver_dae", "thp.ver_hal" };
	private String enforce = "";

	public FileManager() {
		cmdOper.createDir(internalPath);
	}

	public void initLogFile() {
		cmdOper.createDir(internalPath);
	}

	public void updateExistsFiles() {
		existsFiles.clear();
		File f = new File(sdcardPath);
		if (!f.exists()) {
			return;
		}
		File[] files = f.listFiles();// 列出所有文件
		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				if (name.endsWith(".raw")) {
					existsFiles.add(name);
				}
			}
		}
	}

	public boolean isFileNameExists(String name) {
		name += ".raw";
		return existsFiles.contains(name);
	}

	public String setTargetPath(String dirName) {
		fileName = dirName;
		targetPath = sdcardPath + dirName + "/";
		cmdOper.createDir(targetPath);
		return targetPath;
	}

	public String readFile() {
		String content = FileOper.readFileAsString(configFilePath);
		return content;
	}

	public String saveFile(String data) {
		String name = cmdOper.saveFile(data, configFilePath, false);
		return name;
	}

	private File[] getFileList() {
		File dir = new File(internalPath);
		if (!dir.exists()) {
			return null;
		}
		File[] files = dir.listFiles();// 列出所有文件

		return files;// file.getName()
	}

	public String moveFile() {
		File[] files = getFileList();

		if (files == null) {
			return "目录不存在";
		}

		for (File file : files) {
			String name = file.getName();
			int ret = cmdOper.CopySdcardFile(internalPath + name, targetPath
					+ name);
			if (ret != 0) {
				return "文件移动失败：" + name;
			}
		}
		return targetPath;
	}

	public String saveVersion() {
		ArrayList<String> data = new ArrayList<String>();
		String name = cmdOper.saveFile(data, targetPath + "version.txt", false);
		return name;
	}

	public String getSnNo() {
		String snNo = "";
		try {
			snNo = CmdOper.getSnNo();
		} catch (Exception e) {
			snNo = "read sn no error";
			e.printStackTrace();
		}
		return snNo;
	}

	private ArrayList<String> getStatusInfo(String[] lists) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			for (String str : lists) {
				data.add(str + ": ->" + CmdOper.getProp(str));
			}
		} catch (Exception e) {
			data.add("error!");
			e.printStackTrace();
		}
		return data;
	}

	private String getStatusWithCmds() {
		String data = "error!";
		try {
			data = CmdOper.exeHostCmd("status");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public boolean isSeLinuxOpen() {
		try {
			enforce = CmdOper.getEnforce();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enforce.contains("Enforcing");
	}

	public String getAPStatusInfo() {
		ArrayList<String> data = new ArrayList<String>();
		return FileOper.listToStr(data);
	}

	public String setAPWorkingStatusInfo() {
		return getStatusWithCmds();
	}

	public String getVersionInfoByCmd() {
		String data = "error!";
		try {
			data = CmdOper.exeHostCmd("version");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public String getVersionInfoByProp() {
		ArrayList<String> data = getStatusInfo(versionLists);
		return FileOper.listToStr(data);
	}

	public CmdOper getCmdOper() {
		return cmdOper;
	}
}
