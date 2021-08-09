package huawei.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

public class FileOper {
	private String fileSaveDir = "";

	// 基于多线程考虑，不能使用全局变量
	// protected static ArrayList<String> data;

	public FileOper() {
		this(ConstData.sdcardfiledir);
	}

	public FileOper(String dir) {
		setSaveDir(dir);
	}

	public String getSaveDir() {
		return fileSaveDir;
	}

	public String setSaveDir(String dir) {
		if (!dir.contains(File.separator)) {
			dir = ConstData.sdcardfiledir + dir + File.separator;
		}
		fileSaveDir = dir;
		return getSaveDir();
	}

	public boolean createDir(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			return file.mkdirs();
		}
		return false;
	}

	public File fileTouch(String filepath) throws IOException {
		File file = new File(filepath);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	public static String listToStr(ArrayList<String> data) {
		if (data == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (String str : data) {
			if (!str.endsWith("\n")) {
				str += "\n";
			}
			sb.append(str);
		}
		return sb.toString();
	}

	public static String readFileAsString(String readFilePath) {
		ArrayList<String> data = readFile(readFilePath);
		return listToStr(data);
	}

	public static ArrayList<String> readFile(String readFilePath) {
		ArrayList<String> data = new ArrayList<String>();
		File inputFile = new File(readFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			Log.e(ConstData.logtag, "File " + readFilePath + " exist = "
					+ inputFile.exists() + " canread = " + inputFile.canRead());
			return data;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(readFilePath), "UTF-8"));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				tempString = tempString.trim();
				if (tempString.length() > 0) {
					data.add(tempString);
				}
			}
			Log.v(ConstData.logtag,
					"ReadSysFile line number is: " + data.size());
		} catch (Exception e) {
			data.clear();
			Log.e(ConstData.logtag, "readSysFile: file read error!");
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}

	protected void writeSysFile(String writeFilePath, String data)
			throws Exception {
		File writeFile = new File(writeFilePath);
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(writeFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(data);
		} catch (Exception e) {
			Log.e("lance", "write failed: " + writeFilePath + " " + data);
			e.printStackTrace();
			throw e;
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 文件拷贝
	// 要复制的目录下的所有非子目录(文件夹)文件拷贝
	public int CopySdcardFile(String fromFile, String toFile) {
		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public String saveHtmlFile(ArrayList<String> data, String name) {
		ArrayList<String> outData = new ArrayList<String>();
		outData.add("<html><body>");
		for (String line : data) {
			outData.add(line + "<br/>");
		}
		outData.add("</body></html>");
		return saveFile(outData, name, true);
	}

	public String getTimeStr(String formate) {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(formate);
		return formatter.format(currentTime);
	}

	public String getTimeStr() {
		return getTimeStr("yyyy-MM-dd-HH-mm-ss");
	}

	public String getFileName(String firstline) {
		firstline = firstline.replace(":", "_");
		String name = String.format("%s_%s_%03d.txt", firstline, getTimeStr(),
				System.currentTimeMillis() % 1000);
		return name;
	}

	public String saveTextFile(ArrayList<String> data, String name) {
		return saveFile(data, name, true);
	}

	public String saveFile(ArrayList<String> data, String name, boolean append) {
		String content = listToStr(data);
		return saveFile(content, name, append);
	}

	public String saveFile(String data, String name, boolean append) {
		BufferedWriter writer = null;
		try {
			String filepath = name;
			if (!filepath.contains(File.separator)) {
				filepath = fileSaveDir + File.separator + name;
			}
			filepath = filepath.replace(File.separator + File.separator,
					File.separator);
			createDir(fileSaveDir);
			File file = fileTouch(filepath);
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, append), "UTF-8"));
			if (!data.endsWith("\n")) {
				data += "\n";
			}
			writer.write(data);
			writer.flush();
			System.out.println("Write file: " + name + "done");
		} catch (Exception e) {
			System.out.println("Write file: " + name + "error");
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (writer != null) {
			return name;
		} else {
			return null;
		}
	}
}
