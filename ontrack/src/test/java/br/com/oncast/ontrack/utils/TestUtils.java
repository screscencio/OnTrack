package br.com.oncast.ontrack.utils;


public class TestUtils {

	private static final int SLEEP_TIME = 2;
	public static final float TOLERATED_FLOAT_DIFFERENCE = 0.01f;

	public static void sleep() {
		try {
			Thread.sleep(SLEEP_TIME);
		}
		catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
