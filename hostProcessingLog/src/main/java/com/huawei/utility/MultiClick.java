package huawei.utility;

import java.util.ArrayList;

public class MultiClick {
	private long MIN_INTERVEL = 150;
	private long INTERVEL = 200;
	private int NUM = -1;
	private ArrayList<Long> numbers = new ArrayList<Long>();

	public MultiClick(int num, int interval) {
		NUM = num;
		INTERVEL = interval;
	}

	public void click() {
		long lastClick = 0;
		long curClick = System.currentTimeMillis();

		if (numbers.size() > 0) {
			lastClick = numbers.get(numbers.size() - 1);
		}

		if (curClick - lastClick > INTERVEL) {
			numbers.clear();
		}

		if (curClick - lastClick < MIN_INTERVEL && curClick - lastClick > 0) {
			numbers.set(numbers.size() - 1, curClick);
		} else {
			numbers.add(curClick);
		}
	}

	public long getInterval() {
		return INTERVEL;
	}

	public boolean getMultiClick() {
		if (numbers.size() >= NUM) {
			numbers.clear();
			return true;
		} else {
			return false;
		}
	}

	public void clear() {
		numbers.clear();
	}
}
