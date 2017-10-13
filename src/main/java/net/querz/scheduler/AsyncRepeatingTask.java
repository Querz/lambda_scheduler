package net.querz.scheduler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncRepeatingTask extends AsyncTask {
	private long interval;

	public AsyncRepeatingTask( Scheduler scheduler, Runnable runnable, long delay, long interval) {
		super(scheduler, runnable, delay);
		this.interval = interval;
	}

	@Override
	public void start() {
		future = scheduler.getThreadpool().submit(
			() -> {
				while (!isDone()) {
					try {
						Thread.sleep(didRun() ? interval : getDelay());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					execute();
				}
			}
		);
	}
}
