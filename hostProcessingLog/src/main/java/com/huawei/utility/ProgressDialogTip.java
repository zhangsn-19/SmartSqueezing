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

	// Handler�п��Ե���UI���
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
		pd = ProgressDialog.show(act, "��ʾ", "����ץ��ֵ����ȴ���лл��");
		Thread thread = new Thread() {
			public void run() {
				String msgText = "�ɹ���ȡ��ֵ";
				Intent intent = null;
				try {
					huawei.utility.AssistUI.threadSleep(500);
					String dir = fileOperate.setSaveDir("HuaweiTP");
					String name = fileOperate.saveCapData(null, null);
					File f = new File(dir + File.separator + name);

					msgText = "ץȡ��ֵ��" + name;

					intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.fromFile(f), "text/plain");
					try {
						act.startActivity(intent);
					} catch (Exception e) {
						msgText = "�ļ�Ԥ��ʧ��";
					}
				} catch (Exception e) {
					e.printStackTrace();
					msgText += "��ֵ��ȡʧ��";
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
		pd = ProgressDialog.show(act, "��ʾ", "���ڽ���У׼�������ĵȴ���лл��");
		Thread thread = new Thread() {
			public void run() {
				String msgText = "У׼�ɹ�";
				try {
					fileOperate.calibrateTP();
				} catch (Exception e) {
					msgText = "У׼ʧ�ܣ�������" + e.getMessage();
				}

				Message message = toastTip.obtainMessage(1, msgText);
				toastTip.sendMessage(message);
				Log.v(ConstData.logtag, "��Ϣ���ͳɹ���");
			}
		};
		thread.start();
	}

	public void doUpdateFW(final String name, final Handler handler) {
		pd = ProgressDialog.show(act, "��ʾ", "���������̼��������ĵȴ���лл��");
		Thread thread = new Thread() {
			public void run() {
				String msg = "�����ɹ�";

				try {
					fileOperate.updateFirmWare(name);
				} catch (Exception e) {
					msg = "����ʧ�ܣ�������";
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
