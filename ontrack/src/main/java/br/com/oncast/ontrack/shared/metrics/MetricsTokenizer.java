package br.com.oncast.ontrack.shared.metrics;

import com.google.gwt.place.shared.Place;

public class MetricsTokenizer {

	public static String forPlace(final Place place) {
		return forPlace(place.getClass());
	}

	public static String forPlace(final Class<? extends Place> clazz) {
		final String[] split = clazz.toString().split("\\.");
		return split[split.length - 1];
	}

}
