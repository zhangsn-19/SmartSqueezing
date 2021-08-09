package huawei.components;

import huawei.shared.ConstData;
import huawei.utility.AssistUI;
import huawei.utility.FunctionInterface;
import huawei.utility.MultiClick;

import java.util.HashMap;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public abstract class SharedMainActivity extends Activity {
	protected static final HashMap<String, Object> shardData = new HashMap();

	protected void setOnClickListener(int id, FunctionInterface fun) {
		View view = findViewById(id);
		AssistUI.setOnClickListener(view, fun);
	}

	protected void hiddenStatusBar() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public boolean onDoubleClickVolumeDownKey() {
		return true;
	}

	public boolean onDoubleClickVolumeUpKey() {
		return true;
	};

	public boolean onVolumeUpKey() {
		return true;
	};

	public boolean onVolumeDownKey() {
		return true;
	};

	public boolean onBackKey() {
		Toast.makeText(this, "please press home to quit", Toast.LENGTH_SHORT)
				.show();
		return true;
	};

	private MultiClick upclick = new MultiClick(2, 300);
	private MultiClick downclick = new MultiClick(2, 300);

	@Override
	public final boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			upclick.click();
			if (upclick.getMultiClick()) {
				onDoubleClickVolumeUpKey();
			}

			if (onVolumeUpKey()) {
				return true; // 直接返会屏蔽掉父类里关于音量的显示框。
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			downclick.click();
			if (downclick.getMultiClick()) {
				onDoubleClickVolumeDownKey();
			}

			if (onVolumeDownKey()) {
				return true; // 直接返会屏蔽掉父类里关于音量的显示框。
			}
			break;
		case KeyEvent.KEYCODE_BACK: // 屏蔽返回键
			if (onBackKey()) {
				return true; // 直接返会屏蔽掉父类里关于音量的显示框。
			}
			Log.v(ConstData.logtag, "按下返回键！");
			break;
		case KeyEvent.KEYCODE_HOME:
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void creatMenu(Menu menu) {
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		creatMenu(menu);
		return true;
	}
}
