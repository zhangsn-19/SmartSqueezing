package huawei.shared;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import huawei.components.*;

import android.util.Log;

public class CapData {
	public static final int CAP_RAWDATA_DELTEDATA = 1;
	public static final int CAP_RAWDATA = 2;
	public static final int CAP_DELTEDATA = 3;
	public static final int CAP_HOST = 4;

	private int dataType;
	private String firstline;
	ArrayList<String> rawdata = new ArrayList<String>();
	ArrayList<String> deltedata = new ArrayList<String>();
	int[][] rawdataArray = null;
	int[][] deltedataArray = null;

	public CapData(ArrayList<String> dataTmp, int dataTypeTmp) throws Exception {
		dataType = dataTypeTmp;
		initStrData(dataTmp);
		initArrayData();
		formateStrData();
	}

	public CapData(String dataTmp) {
		dataType = CAP_RAWDATA;
		initStrDataHost(dataTmp);
		try {
			initArrayData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CapData(int[][] data) {
		dataType = CAP_RAWDATA;
		rawdataArray = data;
	}

	public boolean passTest() {
		return !firstline.contains("F");
	}

	private void initArrayData() throws Exception {
		if (rawdata.size() > 0) {
			rawdataArray = strDataToArray(rawdata);
		}

		if (deltedata.size() > 0) {
			deltedataArray = strDataToArray(deltedata);
		}
	}

	public int[][] getDataArray() {
		return getDataArray(dataType);
	}

	public int[][] getDataArray(int dataType) {
		if (dataType == CAP_RAWDATA) {
			return rawdataArray;
		} else if (dataType == CAP_DELTEDATA) {
			return deltedataArray;
		} else {
			return null;
		}
	}

	public void swapTopDown() {
		swapTopDown(rawdataArray);
		swapTopDown(deltedataArray);
	}

	public void swapLeftRight() {
		swapLeftRight(rawdataArray);
		swapLeftRight(deltedataArray);
	}

	public void transpose() {
		rawdataArray = rotated90(rawdataArray);
		deltedataArray = rotated90(deltedataArray);
	}

	public ArrayList<String> getDataStrList() {
		return getDataStrList(dataType);
	}

	public ArrayList<String> getDataStrList(int dataType) {
		if (dataType == CAP_RAWDATA) {
			return rawdata;
		} else if (dataType == CAP_DELTEDATA) {
			return deltedata;
		} else {
			return null;
		}
	}

	public String getDataStr() {
		return getDataStr(dataType);
	}

	public String getDataStr(int dataType) {
		ArrayList<String> data = getDataStrList(dataType);
		return strListToStr(data);
	}

	public static String strListToStr(ArrayList<String> data) {
		StringBuilder sb = new StringBuilder();
		for (String str : data) {
			sb.append(str);
			sb.append("\n");
		}
		return sb.toString();
	}

	// Pattern.compile("\\+?-?[0-9]+.?[0-9]*.*"); begin with float number
	private boolean isIntNums(String str) {
		str = str.trim();
		Pattern pattern = Pattern.compile("[[\\+-]?[0-9]+[,]]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			Log.v(huawei.shared.ConstData.logtag, "not match num: " + str);
			return false;
		}
	}

	private void initStrData(ArrayList<String> rawStrData) {
		ArrayList<String> cleanData = new ArrayList<String>();
		firstline = rawStrData.get(0);
		try {
			int num = rawStrData.size();
			int i = 0;
			while (i < num && !isIntNums(rawStrData.get(i))) {
				i++;
			}
			while (i < num && isIntNums(rawStrData.get(i))) {
				cleanData.add(rawStrData.get(i));
				i++;
			}

			if (dataType == CAP_RAWDATA_DELTEDATA) {
				rawdata.addAll(cleanData);
			} else {
				deltedata.addAll(cleanData);
			}

			cleanData.clear();

			while (i < num && !isIntNums(rawStrData.get(i))) {
				i++;
			}
			while (i < num && isIntNums(rawStrData.get(i))) {
				cleanData.add(rawStrData.get(i));
				i++;
			}
			deltedata.addAll(cleanData);
		} catch (Exception e) {
			Log.e(huawei.shared.ConstData.logtag, "CapData, initData error!");
			e.printStackTrace();
		}
	}

	private void initStrDataHost(String dataTmp) {
		String[] data = dataTmp.split("\n");
		for (String d : data) {
			rawdata.add(d);
		}
	}

	private void formateStrData() {
		if (rawdataArray != null) {
			rawdata = arrayToStringList(rawdataArray);
		}
		if (deltedataArray != null) {
			deltedata = arrayToStringList(deltedataArray);
		}
	}

	private int[][] strDataToArray(ArrayList<String> strData) throws Exception {
		int[][] rawdata = null;

		if (strData == null || strData.size() <= 0) {
			Log.e(huawei.shared.ConstData.logtag, "strDataToArray error: strData is null");
			return rawdata;
		}

		rawdata = new int[strData.size()][];
		int i = -1;
		for (String lineStr : strData) {
			++i;
			String[] line = null;
			try {
				line = lineStr.replace(",", " ").replace("\t", " ").trim()
						.split(" +");
				rawdata[i] = new int[line.length];
				for (int j = 0; j < line.length; ++j) {
					rawdata[i][j] = Integer.parseInt(line[j]);
				}
			} catch (Exception e) {
				rawdata = null;
				Log.e(huawei.shared.ConstData.logtag, "translate str to int error: "
						+ lineStr);
				throw e;
			}
		}
		return rawdata;
	}

	public String getFirstLine() {
		return firstline;
	}

	public void print_Array(int[][] array) {
		if (array == null) {
			System.out.println("容值数组为空");
			return;
		}
		for (int[] item : array) {
			for (int t : item) {
				System.out.print(t + ",");
			}
			System.out.println();
		}
	}

	private void swapTopDown(int[][] data) {
		if (data == null) {
			return;
		}
		int left = 0;
		int right = data.length - 1;
		while (left < right) {
			int[] tmp = data[left];
			data[left] = data[right];
			data[right] = tmp;
			left++;
			right--;
		}
	}

	private void swapLeftRight(int[][] data) {
		if (data == null) {
			return;
		}
		for (int[] line : data) {
			int left = 0;
			int right = line.length - 1;
			while (left < right) {
				int tmp = line[left];
				line[left] = line[right];
				line[right] = tmp;
				left++;
				right--;
			}
		}
	}

	private static int[][] rotated90(int[][] raw) {
		if (raw == null) {
			return raw;
		}
		if (raw.length > 0 && raw.length < raw[0].length) {
			int[][] trans = new int[raw[0].length][raw.length];

			for (int i = 0; i < raw.length; ++i) {
				for (int j = 0; j < raw[0].length; j++) {
					trans[j][i] = raw[i][j];
				}
			}

			int i = 0;
			int j = trans.length - 1;
			int[] tmp = null;
			while (i < j) {
				tmp = trans[i];
				trans[i] = trans[j];
				trans[j] = tmp;
				i++;
				j--;
			}

			raw = trans;
		}
		return raw;
	}

	public static ArrayList<String> arrayToStringList(int[][] data) {
		return arrayToStringList(data, false);
	}

	public static ArrayList<String> arrayToStringList(int[][] data,
			boolean rotated) {
		ArrayList<String> result = new ArrayList<String>();

		if (rotated) {
			data = rotated90(data);
		}

		for (int[] line : data) {
			StringBuilder sb = new StringBuilder();
			for (int d : line) {
				sb.append(String.format("%3d,", d));
			}
			result.add(sb.toString());
		}
		return result;
	}

	public static void setAbs(int[][] data) {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				data[i][j] = Math.abs(data[i][j]);
			}
		}
	}

	public static int[][] get_diff(int[][] first, int[][] second) {
		if (first == null || second == null || first.length <= 0) {
			return null;
		}

		int rowNum = first.length;
		int colNum = first[0].length;
		int[][] result = new int[rowNum][colNum];

		try {
			for (int i = 0; i < rowNum; i++) {
				for (int j = 0; j < colNum; j++) {
					result[i][j] = first[i][j] - second[i][j];
				}
			}

		} catch (Exception e) {
			result = null;
		}

		return result;
	}

	public int[] getBound(int[][] data) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int[] line : data) {
			for (int n : line) {
				if (n < min) {
					min = n;
				}
				if (n > max) {
					max = n;
				}
			}
		}
		return new int[] { min, max };
	}

	public int[] getCenterBound(int[][] data, int start, int length) {
		int colNum = data[0].length;
		int[][] centerDiff = new int[length][colNum];
		System.arraycopy(data, start, centerDiff, 0, length);
		return getBound(centerDiff);
	}
}
