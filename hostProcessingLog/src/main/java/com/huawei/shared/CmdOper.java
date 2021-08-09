package huawei.shared;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import android.util.Log;

public class CmdOper extends FileOper {
	private static String m_tptool = "tpd";
	private static final String prop_save_data = "persist.aptouch_save_data";

	public static String setHostHolster(boolean on) throws Exception {
		return setHostFeature("holster", on);
	}

	public static boolean get_savedata_mode() {
		boolean ret = false;
		try {
			Scanner sc = new Scanner(getHostFeature("logtofile"));
			ret = sc.nextInt() != 0;
		} catch (Exception e) {
		}
		return ret;
	}

	public static void cacheAptouchLog() {
		try {
			exeHostCmd("cachelog");
			exeHostCmd("dumplog");
		} catch (Exception e) {
		}
	}

	public static String startAptouchLog() throws Exception {
		return setHostFeature("logtofile", true);
	}

	public static String enforceAptouchLog() throws Exception {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("logtofile");
		cmds.add("enforce");
		return exeHostCmd(cmds);
	}

	public static String endAptouchLog() throws Exception {
		return setHostFeature("logtofile", false);
	}

	public static String getHostFeature(String cmd) throws Exception {
		return exeHostCmd(cmd);
	}

	public static String exeHostCmd(String cmd) {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add(cmd);
		try {
			return exeHostCmd(cmds);
		} catch (Exception e) {
			return "error";
		}
	}

	private static String setHostFeature(String cmd, boolean on)
			throws Exception {
		String parm = "off";
		if (on) {
			parm = "on";
		}

		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add(cmd);
		cmds.add(parm);
		return exeHostCmd(cmds);
	}

	public static String getThpLogList() {
		return exeHostCmd("listlog");
	}

	public static String getDiffData() {
		return exeHostCmd("diffdata");
	}

	public static String getRawData() {
		return exeHostCmd("rawdata");
	}

	private static String exeHostCmd(ArrayList<String> cmds) throws Exception {
		cmds.add(0, m_tptool);
		return executeCmd(cmds);
	}

	public static void setEnforce0() throws Exception {
		executeCmd(new String[] { "setenforce", "0" });
	}

	static public String getEnforce() throws Exception {
		String sb = executeCmd(new String[] { "getenforce" });
		String result = sb.toString().trim();
		return result;
	}

	public void reboot() throws Exception {
		executeCmd(new String[] { "reboot" });
	}

	private int getDaemonPid() throws Exception {
		String str = executeCmd(new String[] { "ps",
				"/system/bin/aptouch_daemon" });
		String line = str.substring(str.indexOf("root") + 4);
		Scanner sc = new Scanner(line.trim());
		return sc.nextInt();
	}

	public void rebootAptouch() throws Exception {
		// StringBuilder str = executeCmd(new String[] {
		// "/system/bin/aptouch_daemon" });
		int pid = getDaemonPid();
		executeCmdRoot("kill " + pid);
		executeCmdRoot("/system/bin/aptouch_daemon");
	}

	public static void initTpTool() {
		File file = new File("vendor/bin/" + m_tptool);
		if (!file.exists()) {
			m_tptool = "tptool";
			file = new File("vendor/bin/" + m_tptool);
			if (!file.exists()) {
				m_tptool = "aptouch_daemon_debug";
			}
		}
	}

	public static boolean isHostProcessing() {
		boolean isthp = false;
		try {
			File file = new File("dev/thp");
			if (!file.exists()) {
				file = new File("dev/jdi_bu21150");
			}
			isthp = file.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isthp;
	}

	public static boolean isDebugRaw() {
		String str = "";
		try {
			str = getProp("thp.debug.raw");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.contains("1");
//		return false;
	}

	public static String getSnNo() throws Exception {
		return getProp("ro.serialno");
	}

	public static void setProp(String prop, String value) throws Exception {
		executeCmd(new String[] { "setprop", prop, value });
	}

	public static String getProp(String prop) throws Exception {
		String sb = executeCmd(new String[] { "getprop", prop });
		String value = sb.trim();
		Log.i(ConstData.logtag, "getprop " + prop + " is " + value);
		return value;
	}

	public static void releaseTouch() throws Exception {
		/*
		 * http://stackoverflow.com/questions/14928197/how-to-simulate-touch-from
		 * -background-service-with-sendevent-or-other-way Instrumentation
		 * m_Instrumentation = new Instrumentation();
		 * m_Instrumentation.sendPointerSync(MotionEvent.obtain(
		 * SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
		 * MotionEvent.ACTION_UP, 0, 0, 0));
		 */
		executeCmdRoot("sendevent /dev/input/event5 1 330 0;sendevent /dev/input/event5 0 0 0");
	}

	protected static void executeCmdRoot(String cmd) throws Exception {
		Process process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		os.writeBytes(cmd + "\n");
		os.writeBytes("exit\n");
		os.flush();
		os.close();
		process.waitFor();
	}

	protected static String executeCmd(ArrayList<String> parms)
			throws Exception {
		String[] commmd = parms.toArray(new String[parms.size()]);
		return executeCmd(commmd);
	}

	protected static String executeCmd(String[] commmd) throws Exception {
		String line = "";
		StringBuilder sb = new StringBuilder(line);

		StringBuilder cmdstr = new StringBuilder();
		for (String s : commmd) {
			cmdstr.append(" " + s);
		}
		// Log.d("lengxuntai", "" + cmdstr);
		// start the ls command running
		// String[] args = new String[]{"sh", "-c", command};
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(commmd); // 这句话就是shell与高级语言间的调用,
												// 如果有参数的话可以用另外一个被重载的exec方法,
												// 实际上这样执行时启动了一个子进程,它没有父进程的控制台;也就看不到输出,所以我们需要用输出流来得到shell执行后的输出
		InputStream inputstream = proc.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		// read the ls output
		while ((line = bufferedreader.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}

		// 使用exec执行不会等执行成功以后才返回,它会立即返回,使用wairFor()可以等待命令执行完成以后才返回.
		if (proc.waitFor() != 0) {
			System.out.println("exit value = " + proc.exitValue());
		}
		return sb.toString();
	}
}
