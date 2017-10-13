package net.querz.scheduler;

public abstract class Task {
	private long instantiationTime = System.currentTimeMillis();
	private long lastExecutionTime = 0;
	private long delay = 0;
	private boolean done = false;
	private Runnable runnable;
	private final Object lock = new Object();

	public Task(Runnable runnable, long delay) {
		this.runnable = runnable;
		this.delay = delay;
	}

	public final long getInstantiationTime() {
		return  instantiationTime;
	}

	public long getLastExecutionTIme() {
		return lastExecutionTime;
	}

	public long getDelay() {
		return delay;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone() {
		done = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	protected final Runnable getRunnable() {
		return runnable;
	}

	public boolean didRun() {
		return lastExecutionTime != 0;
	}

	public void join() {
		synchronized (lock) {
			while (!done) {
				try {
					lock.wait();
				} catch (InterruptedException ex) {
					break;
				}
			}
		}
	}

	public void execute() {
		lastExecutionTime = System.currentTimeMillis();
		runnable.run();
	}
}
