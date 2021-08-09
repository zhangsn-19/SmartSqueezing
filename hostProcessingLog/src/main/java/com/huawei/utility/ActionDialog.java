package huawei.utility;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import huawei.utility.FunctionInterface;

public class ActionDialog {
	private String title = "Do you want to excute?";
	private FunctionInterface confirm = null;
	private FunctionInterface cancel = null;

	public ActionDialog setTitle(String str) {
		title = str;
		return this;
	}

	public ActionDialog setConfirm(FunctionInterface fun) {
		confirm = fun;
		return this;
	}

	public ActionDialog setCancel(FunctionInterface fun) {
		cancel = fun;
		return this;
	}

	public void show(Context context) {
		Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		if (confirm != null) {
			alert.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							try {
								confirm.action();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}

		if (cancel != null) {
			alert.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							try {
								cancel.action();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}
		alert.show();
	}
}
