package br.com.oncast.ontrack.server.services.threadSync;

public class ExecutionMock implements Runnable {

	private boolean lock = false;

	@Override
	public void run() {
		lock = true;
		while (lock) {
			try {
				Thread.sleep(100);
			}
			catch (final InterruptedException e) {}
		}

		lock = false;
	}

	public boolean isLocked() {
		return lock;
	}

	public void unlock() {
		lock = false;
	}

}
