package net.querz.scheduler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncTask extends Task {
	Scheduler scheduler;
	Future<?> future;

	public AsyncTask(Scheduler scheduler, Runnable runnable, long delay) {
		super(runnable, delay);
		this.scheduler = scheduler;
	}

	@Override
	public void join() {
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		future = scheduler.getThreadpool().submit(
			() -> {
				try {
					Thread.sleep(getDelay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				execute();
				setDone();
			}
		);
	}

	public Future<?> getFuture() {
		return future;
	}
}
