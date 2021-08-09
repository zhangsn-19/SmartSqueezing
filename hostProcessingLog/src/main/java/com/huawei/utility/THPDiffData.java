package com.huawei.utility;

import huawei.shared.CapData;

public class THPDiffData {
	private static final String tag = "LineData=";
	private CapData m_data;

	private String[] splitString(String dataStr) {
		return dataStr.split(tag);
	}

	private int[][] mergeArray(int[][] diffarray, int[][] linediffarray) {
		int dim1 = diffarray[0].length;
		int dim2 = diffarray.length;
		int[][] data = new int[dim2 + 1][dim1 + 1];

		for (int i = 0; i < dim2; i++) {
			for (int j = 0; j < dim1; j++) {
				data[i][j] = diffarray[i][j];
			}
		}

		for (int i = 0; i < dim1; i++) {
			data[dim2][i] = linediffarray[0][i];
		}
		for (int i = 0; i < dim2; i++) {
			data[i][dim1] = linediffarray[0][i + dim1];
		}

		data[dim2][dim1] = 0;
		return data;
	}

	private int[][] parse_diff_data(String dataStr) {
		String[] data = splitString(dataStr);

		CapData diff = new CapData(data[0]);
		CapData linediff = new CapData(data[1]);

		return mergeArray(diff.getDataArray(), linediff.getDataArray());
	}

	public THPDiffData(String dataStr) {
		if (dataStr.contains(tag)) {
			int[][] data = parse_diff_data(dataStr);
			m_data = new CapData(data);
		} else {
			m_data = new CapData(dataStr);
		}
	}

	public CapData getData() {
		return m_data;
	}
}
