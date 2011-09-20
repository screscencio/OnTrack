package br.com.oncast.ontrack.utils;

import java.util.List;

public class StringUtils {
	public static String join(final List<String> strings, final String delimiter) {
		final StringBuilder sb = new StringBuilder();

		sb.append(strings.get(0));
		for (final String s : strings.subList(1, strings.size())) {
			sb.append(delimiter).append(s);
		}

		return sb.toString();
	}
}
