package huawei.utility;

public class SuspendableThread extends Thread {
	private huawei.utility.FunctionInterface func;
	private boolean suspend = true;
	private String control = ""; // 只是需要一个对象而已，这个对象没有实际意义

	public SuspendableThread(huawei.utility.FunctionInterface fun) {
		func = fun;
	}

	public void setSuspend(boolean suspend) {
		if (!suspend) {
			synchronized (control) {
				control.notifyAll();
			}
		}
		this.suspend = suspend;
	}

	public void run() {
		while (true) {
			synchronized (control) {
				if (suspend) {
					try {
						control.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				func.action();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
