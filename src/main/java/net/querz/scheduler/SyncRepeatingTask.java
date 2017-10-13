package net.querz.scheduler;

public class SyncRepeatingTask extends Task {
	private long interval;

	public SyncRepeatingTask(Runnable runnable, long delay, long interval) {
		super(runnable, delay);
		this.interval = interval;
	}

	public long getInterval() {
		return interval;
	}

	@Override
	public void execute() {
		if (!didRun() && System.currentTimeMillis() - getInstantiationTime() >= getDelay()
				|| didRun() && System.currentTimeMillis() - getLastExecutionTIme() >= interval) {
			super.execute();
		}
	}
}
