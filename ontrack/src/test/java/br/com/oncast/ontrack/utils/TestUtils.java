package br.com.oncast.ontrack.utils;


public class TestUtils {

	private static final long SLEEP_TIME = 2;
	public static final float TOLERATED_FLOAT_DIFFERENCE = 0f;

	public static void sleep() {
		try {
			Thread.sleep(SLEEP_TIME);
		}
		catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
