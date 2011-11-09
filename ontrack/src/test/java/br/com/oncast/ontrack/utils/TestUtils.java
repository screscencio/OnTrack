package br.com.oncast.ontrack.utils;

import java.lang.reflect.Method;

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

	@SuppressWarnings("unchecked")
	public static <T> T call(final Object instance, final String methodName, final Object... args) throws Exception {
		final Method method = instance.getClass().getDeclaredMethod(methodName, getClassesFrom(args));
		method.setAccessible(true);
		return (T) method.invoke(instance, args);
	}

	private static Class<?>[] getClassesFrom(final Object[] args) {
		if (args.length == 0) return null;

		final Class<?>[] classes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			classes[i] = args[i].getClass();
		}

		return classes;
	}
}
