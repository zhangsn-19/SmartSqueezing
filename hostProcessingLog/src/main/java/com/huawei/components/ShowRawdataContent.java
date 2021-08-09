package huawei.components;

import huawei.shared.ConstData;
import huawei.shared.FileOper;
import huawei.shared.R;

import java.io.File;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShowRawdataContent extends huawei.components.MainActivity {
	private EditText fileName;
	private String filePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_main);

		Intent i = getIntent();
		filePath = i.getStringExtra(ConstData.DATA_FLAG);

		showContent();
		setRenameFunction();
	}

	private void setRenameFunction() {
		fileName = (EditText) findViewById(R.id.newFileName);
		Button bnt = (Button) findViewById(R.id.rename);

		bnt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String info;
				String newName = fileName.getText().toString().trim();
				if (newName.length() != 0) {
					if (!newName.contains(".")) {
						newName = newName + ".txt";
					}

					int end = filePath.lastIndexOf(File.separator);
					String curName = filePath.substring(end, filePath.length());

					File from = new File(filePath);
					File to = new File(filePath.replace(curName, newName));

					if (from.renameTo(to)) {
						info = "??????" + filePath.replace(curName, newName);
					} else {
						info = "??????????????????????????synaptics...?????????????";
					}

				} else {
					info = "??????????";
				}
				Toast.makeText(ShowRawdataContent.this, info,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showContent() {
		FileOper fileOper = new FileOper();
		String content = fileOper.readFileAsString(filePath);

		WebView webView = (WebView) findViewById(R.id.webView);
		WebSettings settings = webView.getSettings();
		// settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);

		String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/courbd.ttf\")}body {font-family: MyFont;font-size: xx-small;text-align: justify;}</style></head><body>";
		String pas = "</body></html>";
		String myHtmlString = pish + content + pas;
		webView.loadDataWithBaseURL(null, myHtmlString, "text/html", "UTF-8",
				null);
	}
}
