package net.querz.scheduler.test;

import junit.framework.TestCase;
import net.querz.scheduler.AsyncRepeatingTask;
import net.querz.scheduler.Scheduler;
import net.querz.scheduler.SyncRepeatingTask;
import net.querz.scheduler.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Test extends TestCase {

	private Map<String, Integer> called;
	private Scheduler scheduler;
	private Thread main;
	private int c1, c2;
	private SyncRepeatingTask repeatSync;
	private AsyncRepeatingTask repeatAsync;

	public void setUp() {
		called = new HashMap<>();
		scheduler = new Scheduler();
		main = Thread.currentThread();
		c1 = c2 = 0;
	}

	public void test() throws ExecutionException, InterruptedException {
		Task t = scheduler.sync(() -> t1("t1"));
		t.join();
		assertTrue(called.containsKey("t1") && called.get("t1") == 1);

		t = scheduler.async(() -> t2("t2"));
		t.join();
		assertTrue(called.containsKey("t2") && called.get("t2") == 1);

		repeatSync = scheduler.repeatSync(() -> t3("t3"), 1);
		repeatSync.join();
		assertTrue(called.containsKey("t3") && called.get("t3") == 100);

		repeatAsync = scheduler.repeatAsync(() -> t4("t4"), 1);
		repeatAsync.join();
		assertTrue(called.containsKey("t4") && called.get("t4") == 100);
	}

	private void t1(String s) {
		put(s);
		assertEquals(scheduler.getThreadId(), Thread.currentThread().getId());
		assertNotSame(main.getId(), Thread.currentThread().getId());
	}

	private void t2(String s) {
		put(s);
		assertNotSame(scheduler.getThreadId(), Thread.currentThread().getId());
		assertNotSame(main.getId(), Thread.currentThread().getId());
	}

	private void t3(String s) {
		put(s);
		c2++;
		assertEquals(scheduler.getThreadId(), Thread.currentThread().getId());
		assertNotSame(main.getId(), Thread.currentThread().getId());
		if (c2 == 100) {
			repeatSync.setDone();
		}
	}

	private void t4(String s) {
		put(s);
		c1++;
		assertNotSame(scheduler.getThreadId(), Thread.currentThread().getId());
		assertNotSame(main.getId(), Thread.currentThread().getId());
		if (c1 == 100) {
			repeatAsync.setDone();
		}
	}

	private void put(String s) {
		if (called.containsKey(s)) {
			called.put(s, called.get(s) + 1);
		} else {
			called.put(s, 1);
		}
	}
}
