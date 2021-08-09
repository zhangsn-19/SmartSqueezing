package huawei.shared;

import java.io.File;
import java.util.ArrayList;

public class FileNodeCapOper extends FileNodeOper {
	private static String rawdataFile = "/proc/touchscreen/rawdata";
	private static String rawdataDebug = pathPrefix + "touch_rawdata_debug";

	public FileNodeCapOper() {
		super();
		initClass();
	}

	public FileNodeCapOper(String dir) {
		super(dir);
		initClass();
	}

	private void initClass() {
		File inputFile = new File(rawdataFile);
		if (!inputFile.exists() || !inputFile.canRead()) {
			rawdataFile = "/proc/touchscreen/tp_capacitance_data";
		}
	}

	private ArrayList<String> getMultiCapData(String type, int num)
			throws Exception {
		while (true) {
			try {
				readFile(gloveNode);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		writeSysFile(pathPrefix + "touch_rawdata_debug", type);
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < num; ++i) {
			ArrayList<String> data = readFile(rawdataDebug);
			CapData capData = new CapData(data, CapData.CAP_DELTEDATA);
			ArrayList<String> line = capData.getDataStrList();
			result.add("");
			result.add(String.format("array: %d, line num: %d, time: %s", i,
					line.size(), getTimeStr("HH-mm-ss-SSS")));
			result.addAll(line);
			// AssistUI.threadSleep(12);
		}
		return result;
	}

	public ArrayList<String> getMultiCapDiffData(int num) throws Exception {
		return getMultiCapData("0", num);
	}

	public ArrayList<String> getMultiCapRawData(int num) throws Exception {
		return getMultiCapData("1", num);
	}

	public int[][] getCapDeltaData() throws Exception {
		ArrayList<String> data = readFile(rawdataFile);
		CapData capData = new CapData(data, CapData.CAP_RAWDATA_DELTEDATA);
		return capData.getDataArray(CapData.CAP_DELTEDATA);
	}

	public CapData getCapData() throws Exception {
		ArrayList<String> data = readFile(rawdataFile);
		CapData capData = new CapData(data, CapData.CAP_RAWDATA_DELTEDATA);
		return capData;
	}

	public String saveCapData(CapData data, String name) throws Exception {
		if (data == null) {
			data = getCapData();
		}

		ArrayList<String> fileContent = new ArrayList<String>();
		String result = data.getFirstLine();
		try {
			fileContent.add(result);
			fileContent.add("");

			fileContent.add("noisedata");
			fileContent.addAll(data.getDataStrList(CapData.CAP_DELTEDATA));
			fileContent.add("");

			fileContent.add("rawdata");
			fileContent.addAll(data.getDataStrList(CapData.CAP_RAWDATA));
			fileContent.add("");

			fileContent.add("end");

			if (name == null || name.length() == 0) {
				return saveTextFile(fileContent, getFileName(result));
			} else {
				return saveTextFile(fileContent, name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "null";
		}
	}

}
