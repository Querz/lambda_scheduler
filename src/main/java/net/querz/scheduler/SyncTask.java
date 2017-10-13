package net.querz.scheduler;

public class SyncTask extends Task {
	public SyncTask(Runnable runnable, long delay) {
		super(runnable, delay);
	}

	@Override
	public void execute() {
		if (System.currentTimeMillis() - getInstantiationTime() >= getDelay()) {
			super.execute();
			setDone();
		}
	}
}
