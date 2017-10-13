package net.querz.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scheduler implements Runnable {
	private ExecutorService executor;
	private Revolver<Task> tasks = new Revolver<>();
	private Thread fred;

	public Scheduler() {
		executor = Executors.newCachedThreadPool();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdownNow()));
	}

	public Scheduler(ExecutorService executor) {
		this.executor = executor;
	}

	public ExecutorService getThreadpool() {
		return executor;
	}

	public long getThreadId() {
		return fred != null ? fred.getId() : 0;
	}

	public AsyncTask async(Runnable r) {
		return async(r, 0);
	}

	public AsyncTask async(Runnable r, long delay) {
		AsyncTask t = new AsyncTask(this, r, delay);
		t.start();
		return t;
	}

	public AsyncRepeatingTask repeatAsync(Runnable r, long interval) {
		return repeatAsync(r, 0, interval);
	}

	public AsyncRepeatingTask repeatAsync(Runnable r, long delay, long interval) {
		AsyncRepeatingTask t = new AsyncRepeatingTask(this, r, delay, interval);
		t.start();
		return t;
	}

	public SyncTask sync(Runnable r) {
		return sync(r, 0);
	}

	public SyncTask sync(Runnable r, long delay) {
		SyncTask t = new SyncTask(r, delay);
		tasks.add(t);
		tryStart();
		return t;
	}

	public SyncRepeatingTask repeatSync(Runnable r, long interval) {
		return repeatSync(r, 0, interval);
	}

	public SyncRepeatingTask repeatSync(Runnable r, long delay, long interval) {
		SyncRepeatingTask t = new SyncRepeatingTask(r, delay, interval);
		tasks.add(t);
		tryStart();
		return t;
	}

	private void tryStart() {
		if (fred == null || !fred.isAlive()) {
			fred = new Thread(this);
			fred.start();
		}
	}

	@Override
	public void run() {
		while (fred.isAlive() && tasks.size > 0) {
			if (tasks.current.value.isDone()) {
				tasks.remove();
			} else {
				tasks.current.value.execute();
			}
			tasks.next();
		}
	}

	private class Revolver<T> {
		int size = 0;
		Element<T> last;
		Element<T> first;
		Element<T> current;

		void next() {
			if (current != null) {
				current = current.next;
			}
		}

		void add(T t) {
			Element<T> e = new Element<>(t);
			if (size == 0) {
				last = first = current = e;
			}
			e.previous = last;
			e.next = first;
			last.next = e;
			last = e;
			size++;
		}

		void remove() {
			if (current == first) {
				first = first.next;
			}
			if (current == last) {
				last = last.previous;
			}
			if (current == last && current == first) {
				current = first = last = null;
				size = 0;
			} else if (current != null) {
				current.previous.next = current.next;
				current.next.previous = current.previous;
				size--;
			}
		}

		private class Element<V> {
			V value;
			Element<V> previous;
			Element<V> next;

			Element(V value) {
				this.value = value;
			}
		}
	}
}
