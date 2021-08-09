package com.huawei.z.abandon;

import huawei.components.SharedMainActivity;
import huawei.utility.ActionDialog;
import huawei.utility.AssistUI;
import huawei.utility.FunctionInterface;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.huawei.hostprocessinglog.R;
import com.huawei.utility.AutoCompleteFilterAdapter;
import com.huawei.utility.FileManager;
import com.huawei.utility.HostProcDBHelper;
import com.huawei.utility.InsertAutoCompleteTextView;

public class RenameActivity extends SharedMainActivity {
	private InsertAutoCompleteTextView autoCompleteEdit;
	private FileManager fileManager;
	private String[][] quickItems = new String[][] {
			new String[] { "TOUCH", "ENV", "+", "_", "x" },
			new String[] { "ACT", "STATIC", "TAP", "MOVE", "[]" },
			new String[] { "CHG", "V", "W", "SINE", "SQUARE" },
			new String[] { "WATER", "SD", "WET", "TEMP", "ASC" },
			new String[] { "PRPT", "GLOVE", "THICK", "THIN", "DEC" }, };

	public AutoCompleteTextView getInputTextView() {
		return autoCompleteEdit;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_rename);
		fileManager = (FileManager) shardData.get("FileManager");
		fileManager.updateExistsFiles();

		autoCompleteEdit = (InsertAutoCompleteTextView) findViewById(R.id.fileNameEdit);
		autoCompleteEdit.addTextChangedListener(new FileNameCheck());
		autoCompleteEdit.setText(HostProcDBHelper
				.getLatestRecord(RenameActivity.this));

		AutoCompleteFilterAdapter.associatedAutoCompleteTextView(this,
				autoCompleteEdit);

		setOnClickListener(R.id.inputTipBnt, new FunctionInterface() {
			public void action() throws Exception {
				autoCompleteEdit.performFiltering();
			}
		});

		setOnClickListener(R.id.renameBnt, new FunctionInterface() {
			public void action() throws Exception {
				FunctionInterface fun = new MoveLogging();
				moveFiles(fun, true);
			}
		});
		setOnClickListener(R.id.cancelRenameBnt, new FunctionInterface() {
			public void action() {
				FunctionInterface fun = new MoveLogging();
				moveFiles(fun, false);
			}
		});

		setOnClickListener(R.id.deleteEdigCursorLeft, new FunctionInterface() {
			public void action() throws Exception {
				deleteAlfaInEditText(true);
			}
		});
		setOnClickListener(R.id.deleteEdigCursorRight, new FunctionInterface() {
			public void action() throws Exception {
				deleteAlfaInEditText(false);
			}
		});

		setOnClickListener(R.id.moveEdigCursorLeft, new FunctionInterface() {
			public void action() throws Exception {
				moveCursorInEditText(true);
			}
		});
		setOnClickListener(R.id.moveEdigCursorRight, new FunctionInterface() {
			public void action() throws Exception {
				moveCursorInEditText(false);
			}
		});

		setQuickTextView(R.id.quickInputLayout);
	}

	private void setQuickTextView(int id) {
		TableLayout lay = (TableLayout) findViewById(id);
		for (String[] strs : quickItems) {
			TableRow row = new TableRow(this);
			for (String str : strs) {
				Button tv = new SharpFixedButton(this, str);
				row.addView(tv);
			}
			lay.addView(row);
		}
	}

	class SharpFixedButton extends Button {
		public SharpFixedButton(Context context, String str) {
			super(context);
			setWidth(220);
			setText(str);
			if (str.length() > 5) {
				setTextSize(10);
			}
			final String insterStr = str;
			AssistUI.setOnClickListener(this, new FunctionInterface() {
				@Override
				public void action() throws Exception {
					AssistUI.insterStrToEditText(autoCompleteEdit, insterStr);
					if (insterStr.contains("[")) {
						moveCursorInEditText(true);
					}
				}
			});
		}
	}

	private void deleteAlfaInEditText(boolean left) {
		int index = autoCompleteEdit.getSelectionStart();
		Editable editable = autoCompleteEdit.getEditableText();
		if (left) {
			index--;
		}
		if (index >= 0 && index < editable.length()) {
			editable.delete(index, index + 1);
		}
	}

	private void moveCursorInEditText(boolean left) {
		int index = autoCompleteEdit.getSelectionStart();
		if (left) {
			index--;
		} else {
			index++;
		}
		Selection.setSelection(autoCompleteEdit.getEditableText(), index);
	}

	class FileNameCheck implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (fileManager.isFileNameExists(s.toString())) {
				autoCompleteEdit.setTextColor(Color.RED);
			} else {
				autoCompleteEdit.setTextColor(Color.BLACK);
			}
		}
	}

	private String dirName;

	private void moveFiles(FunctionInterface fun, boolean confirm) {
		dirName = "discard";
		if (confirm) {
			dirName = autoCompleteEdit.getText().toString();
		}

		ActionDialog dialog = new ActionDialog();
		dialog.setConfirm(fun).show(RenameActivity.this);
	}

	class MoveLogging extends FunctionInterface {
		@Override
		public void action() {
			String pre = HostProcDBHelper.getLatestRecord(RenameActivity.this);
			if (!pre.equals(dirName)) {
				HostProcDBHelper.insertRecord(RenameActivity.this, dirName);
			}

			String path = fileManager.setTargetPath(dirName);
			fileManager.saveVersion();
			fileManager.moveFile();

			String retPathStr = getString(R.string.save_file_to) + path;
			if (dirName.equals("discard")) {
				retPathStr = getString(R.string.operate_cancel);
			}

			Toast.makeText(RenameActivity.this, retPathStr, Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}
}
