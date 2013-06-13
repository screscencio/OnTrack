package br.com.oncast.ontrack.shared.metrics;


public class MetricsTokenizer {

	public static String getClassSimpleName(final Object place) {
		return getSimpleName(place.getClass());
	}

	public static String getSimpleName(final Class<?> clazz) {
		final String[] split = clazz.toString().split("\\.");
		return split[split.length - 1];
	}

}
