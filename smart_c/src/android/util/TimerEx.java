package android.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerEx {
	private Timer mTimer;
	private TimerTask mTask;

	public void schedule(TimerTask task, Date when, long period) {
		mTimer.schedule(task, when, period);
	}

	public void cancel() {
		if (mTimer != null) {
			mTask.cancel();
			mTask = null;
			mTimer.cancel();
			mTimer = null;
		}	
	}

	public void schedule(TimerTask task, Date when) {
		cancel();
		
		mTask = task;
		mTimer = new Timer();

		mTimer.schedule(task, when);
	}

	public void schedule(TimerTask task, long delay, long period) {
		cancel();
		
		mTask = task;
		mTimer = new Timer();
		mTimer.schedule(task, delay, period);
	}

	public void schedule(TimerTask task, long delay) {
		cancel();
	
		mTask = task;
		mTimer = new Timer();
		mTimer.schedule(task, delay);
	}

	public void scheduleAtFixedRate(TimerTask task, Date when, long period) {
		cancel();
		
		mTask = task;
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(task, when, period);
	}

	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		cancel();
		
		mTask = task;
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(task, delay, period);
	}
}
