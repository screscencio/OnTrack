package br.com.oncast.ontrack.server.services.threadSync;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SyncronizationServiceTest {

	private static final int SLEEP_INTERVAL = 50;
	private SyncronizationService syncronizationService;

	@Before
	public void setUp() {
		syncronizationService = new SyncronizationService();
	}

	@Test
	public void sameUUIDObjectReturnsSameLockObject() throws Exception {
		final SyncronizationService syncronizationService = new SyncronizationService();
		final UUID uuid = new UUID("1");

		final Object obj1 = syncronizationService.getSyncLockFor(uuid);
		final Object obj2 = syncronizationService.getSyncLockFor(uuid);

		assertEquals(obj1, obj2);
	}

	@Test
	public void uuidWithSameValueReturnsSameLockObject() throws Exception {
		final SyncronizationService syncronizationService = new SyncronizationService();
		final UUID uuid1 = new UUID("1");
		final UUID uuid2 = new UUID("1");

		final Object obj1 = syncronizationService.getSyncLockFor(uuid1);
		final Object obj2 = syncronizationService.getSyncLockFor(uuid2);

		assertEquals(obj1, obj2);
	}

	@Test
	public void runnablesWithSameIDCannotRunConcurrently() throws Exception {
		final UUID uuid1 = new UUID("1");

		final ExecutionMock mock1 = new ExecutionMock();
		final ExecutionMock mock2 = new ExecutionMock();

		final Thread thread1 = new TestThread(uuid1, mock1);
		final Thread thread2 = new TestThread(uuid1, mock2);

		thread1.start();

		Thread.sleep(SLEEP_INTERVAL);
		assertTrue(mock1.isLocked());

		thread2.start();
		Thread.sleep(SLEEP_INTERVAL);

		assertFalse(mock2.isLocked());

		assertTrue(mock1.isLocked());
		assertFalse(mock2.isLocked());

		mock1.unlock();
		Thread.sleep(SLEEP_INTERVAL);

		assertFalse(mock1.isLocked());
		assertTrue(mock2.isLocked());

		mock2.unlock();
		Thread.sleep(SLEEP_INTERVAL);

		assertFalse(mock1.isLocked());
		assertFalse(mock2.isLocked());
	}

	@Test
	public void runnablesWithDiferenteIDMustRunConcurrently() throws Exception {
		final ExecutionMock mock1 = new ExecutionMock();
		final ExecutionMock mock2 = new ExecutionMock();

		final Thread thread1 = new TestThread(new UUID("1"), mock1);
		final Thread thread2 = new TestThread(new UUID("2"), mock2);

		thread1.start();
		Thread.sleep(SLEEP_INTERVAL);
		assertTrue(mock1.isLocked());

		thread2.start();
		Thread.sleep(SLEEP_INTERVAL);
		assertTrue(mock2.isLocked());

		assertTrue(mock1.isLocked());
		assertTrue(mock2.isLocked());

		mock1.unlock();
		mock2.unlock();
		Thread.sleep(SLEEP_INTERVAL);

		assertFalse(mock1.isLocked());
		assertFalse(mock2.isLocked());
	}

	private void syncMethod(final UUID uuid, final ExecutionMock runnable) {
		final Object obj1 = syncronizationService.getSyncLockFor(uuid);

		synchronized (obj1) {
			runnable.run();
		}
	}

	class TestThread extends Thread {

		private final ExecutionMock runnable;
		private final UUID uuid;

		public TestThread(final UUID uuid, final ExecutionMock runnable) {
			this.uuid = uuid;
			this.runnable = runnable;
		}

		@Override
		public void run() {
			syncMethod(uuid, runnable);
		}
	}
}
