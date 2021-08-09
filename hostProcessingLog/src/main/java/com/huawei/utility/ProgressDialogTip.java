package huawei.utility;

import huawei.shared.ConstData;
import huawei.shared.FileNodeCapOper;
import huawei.shared.FileOper;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ProgressDialogTip {
	private ProgressDialog pd;
	private Context act;
	private FileNodeCapOper fileOperate;

	public ProgressDialogTip(Context act, FileNodeCapOper fileOpe) {
		this.act = act;
		fileOperate = fileOpe;
	}

	// Handler中可以调用UI组件
	private Handler toastTip = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (pd != null) {
				pd.dismiss();
				pd = null;
				String tipInfo = (String) msg.obj;
				if (tipInfo != null) {
					Toast.makeText(act.getApplicationContext(), tipInfo,
							Toast.LENGTH_SHORT).show();
				}
			}
			switch (msg.what) {
			case 1:
				break;
			}
		}
	};

	public void catRawdata() {
		pd = ProgressDialog.show(act, "提示", "正在抓容值，请等待，谢谢！");
		Thread thread = new Thread() {
			public void run() {
				String msgText = "成功读取容值";
				Intent intent = null;
				try {
					huawei.utility.AssistUI.threadSleep(500);
					String dir = fileOperate.setSaveDir("HuaweiTP");
					String name = fileOperate.saveCapData(null, null);
					File f = new File(dir + File.separator + name);

					msgText = "抓取容值：" + name;

					intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.fromFile(f), "text/plain");
					try {
						act.startActivity(intent);
					} catch (Exception e) {
						msgText = "文件预览失败";
					}
				} catch (Exception e) {
					e.printStackTrace();
					msgText += "容值读取失败";
				}

				Message message = toastTip.obtainMessage(1, msgText);
				toastTip.sendMessage(message);
				Log.v(ConstData.logtag,
						"ProgressDialogTip: send message successfully");
			}
		};
		thread.start();
	}

	public void doCalibrate() {
		pd = ProgressDialog.show(act, "提示", "正在进行校准，请耐心等待，谢谢！");
		Thread thread = new Thread() {
			public void run() {
				String msgText = "校准成功";
				try {
					fileOperate.calibrateTP();
				} catch (Exception e) {
					msgText = "校准失败！！！！" + e.getMessage();
				}

				Message message = toastTip.obtainMessage(1, msgText);
				toastTip.sendMessage(message);
				Log.v(ConstData.logtag, "消息发送成功！");
			}
		};
		thread.start();
	}

	public void doUpdateFW(final String name, final Handler handler) {
		pd = ProgressDialog.show(act, "提示", "正在升级固件，请耐心等待，谢谢！");
		Thread thread = new Thread() {
			public void run() {
				String msg = "升级成功";

				try {
					fileOperate.updateFirmWare(name);
				} catch (Exception e) {
					msg = "升级失败！！！！";
				}

				pd.dismiss();
				pd = null;
				Message message = handler.obtainMessage(1, msg);
				handler.sendMessage(message);
			}
		};
		thread.start();
	}
}
