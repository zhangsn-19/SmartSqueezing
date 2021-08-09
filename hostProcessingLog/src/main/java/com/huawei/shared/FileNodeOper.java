package huawei.shared;

import huawei.utility.AssistUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import android.util.Log;

public class FileNodeOper extends FileOper {
	protected final static String pathPrefix = "/sys/touchscreen/";
	protected final static String tpInfoNode = pathPrefix + "touch_chip_info";
	protected final static String gloveNode = pathPrefix + "touch_glove";
	protected final static String loglevel = pathPrefix + "loglevel";
	protected final static String wakeupNode = pathPrefix
			+ "easy_wakeup_gesture";
	protected final static String specialHardwareTest = pathPrefix
			+ "touch_special_hardware_test";
	protected final static String holsterNode = pathPrefix
			+ "touch_sensitivity";
	protected final static String fwUpdateNode = pathPrefix + "fw_update_sd";
	protected final static String tpResetNode = pathPrefix + "reset";
	protected final static String openPSNode = "sys/devices/platform/huawei_sensor/ps_enable";
	protected final static String setPSDelayNode = "sys/devices/platform/huawei_sensor/ps_setdelay";

	public FileNodeOper(String dir) {
		super(dir);
	}

	public FileNodeOper() {
		super();
	}

	public void resetTP() throws Exception {
		writeSysFile(tpResetNode, "1");
	}

	public void updateFirmWare(String name) throws Exception {
		File file = new File(ConstData.updateFWDir + name);
		if (!file.exists()) {
			Log.v(ConstData.logtag, "文件不存在！");
		}
		CopySdcardFile(ConstData.updateFWDir + name, ConstData.updateFWDir
				+ "synaptics.img");
		writeSysFile(fwUpdateNode, "1");

		file = new File(ConstData.updateFWDir + "synaptics.img");
		file.delete();
	}

	public void calibrateTP() throws Exception {
		ArrayList<String> data = readFile("/sys/touchscreen/calibrate");
		if (data == null || data.get(0).indexOf("success") < 0) {
			throw new Exception("TP校准失败" + data.get(0));
		}
	}

	public String getRoiDta() {
		ArrayList<String> data = readFile(pathPrefix + "roi_data_debug");
		StringBuilder sb = new StringBuilder();
		for (String str : data) {
			sb.append(str);
		}
		return sb.toString();
	}

	public String readRegister(String cmd) throws Exception {
		writeSysFile("/sys/touchscreen/touch_register_operation", cmd);
		ArrayList<String> data = readFile("/sys/touchscreen/touch_register_operation");
		if (data == null || data.get(0).trim().length() < 0) {
			throw new Exception("TP校准失败" + data.get(0));
		}
		return data.get(0).trim();
	}

	public void openSensor() throws Exception {
		writeSysFile(openPSNode, "1");
		writeSysFile(setPSDelayNode, "100");
	}

	public String specialHardwareTestResult() throws Exception {
		ArrayList<String> data = readFile(specialHardwareTest);
		StringBuilder sb = new StringBuilder();
		for (String str : data) {
			sb.append(str);
		}
		return sb.toString();
	}

	public void specialHardwareTest(boolean enable) throws Exception {
		String data;
		if (enable) {
			data = "1";
		} else {
			data = "0";
		}
		writeSysFile(specialHardwareTest, data);
	}

	public void setWakeupMode(boolean enable) throws Exception {
		String data;
		if (enable) {
			data = "4095";
		} else {
			data = "0";
		}
		writeSysFile(wakeupNode, data);
	}

	public void setGloveMode(boolean glove) throws Exception {
		String data;
		if (glove) {
			data = "1";
		} else {
			data = "0";
		}
		writeSysFile(gloveNode, data);
	}

	public void setHolsterMode(boolean holster) throws Exception {
		String data;
		if (holster) {
			data = "1";
		} else {
			data = "0";
		}
		writeSysFile(holsterNode, data);
	}

	public void setLoglevel(boolean log) throws Exception {
		String data;
		if (log) {
			data = "1";
		} else {
			data = "0";
		}
		writeSysFile(loglevel, data);
	}

	public boolean getLoglevel() throws Exception {
		ArrayList<String> data = readFile(loglevel);
		if (data == null || data.isEmpty()) {
			throw new Exception("loglevel, data is empty");
		}

		Scanner sc = new Scanner(data.get(0));
		int num = Integer.parseInt(sc.next());

		return num != 0;
	}

	public boolean getWakeupMode() throws Exception {
		ArrayList<String> data = readFile(wakeupNode);
		if (data == null || data.isEmpty()) {
			throw new Exception("getWakeupMode, data is empty");
		}

		String line = data.get(0).trim();
		Log.v(ConstData.logtag, "getWakeupMode" + line);
		return !line.startsWith("0x0000");
	}

	public boolean getGloveMode() throws Exception {
		ArrayList<String> data = readFile(gloveNode);
		if (data == null || data.isEmpty()) {
			throw new Exception("getGloveMode, data is empty");
		}

		Scanner sc = new Scanner(data.get(0));
		int num = Integer.parseInt(sc.next());

		return num == 1;
	}

	public String getTpInfo() {
		for (int i = 0; i < 3; ++i) {
			ArrayList<String> data = readFile(tpInfoNode);

			if (data != null && !data.isEmpty()) {
				return data.get(0);
			}
			AssistUI.threadSleep(500);
		}
		return "error";
	}

}
