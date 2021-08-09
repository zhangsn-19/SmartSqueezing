package huawei.utility;

import android.os.Handler;
import android.os.Message;

public class HandlerContainer extends Handler {
	public static final int FUNCTION = 1;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case FUNCTION:
			FunctionInterface fun = (FunctionInterface) msg.obj;
			try {
				fun.action();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public void sendCallbackMessage(FunctionInterface fun) {
		Message message = obtainMessage(FUNCTION, fun);
		sendMessage(message);
	}

}
