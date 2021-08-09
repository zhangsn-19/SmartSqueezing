package huawei.utility;

import huawei.utility.FunctionInterface;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AssistUI {
	private static ProgressDialog pd;

	public static void editDialog(final Context main_act, String defaultStr,
			final FunctionInterface fun) {
		final EditText inputServer = new EditText(main_act);
		inputServer.setText(defaultStr);
		AlertDialog.Builder builder = new AlertDialog.Builder(main_act);
		builder.setIcon(android.R.drawable.ic_dialog_info).setView(inputServer);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				progressDialog(main_act, fun, false);
			}
		});
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				progressDialog(main_act, fun, false);
			}
		});
		builder.show();
	}

	public static void progressDialog(final Context main_act,
			final FunctionInterface fun, final boolean showTip) {
		pd = ProgressDialog.show(main_act, "提示", "正在执行操作");
		Thread thread = new Thread() {
			public void run() {
				String tip = "工作完成";
				long pretime = System.currentTimeMillis();
				try {
					fun.action();
				} catch (Exception e) {
					tip = "操作异常";
					e.printStackTrace();
				}
				long postime = System.currentTimeMillis();
				if (postime - pretime < 500) {
					threadSleep(500);
				}
				if (showTip) {
					Toast.makeText(main_act, tip, Toast.LENGTH_SHORT).show();
				}
				pd.dismiss();
			}
		};
		thread.start();
	}

	public static void threadSleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setOnClickListener(View view, final FunctionInterface fun) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					fun.action();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void insterStrToEditText(EditText edit, CharSequence text) {
		int index = edit.getSelectionStart();
		Editable editable = edit.getEditableText();
		if (index < 0 || index >= editable.length()) {
			editable.append(text);
		} else {
			editable.insert(index, text);
		}
	}
}
