package br.com.oncast.ontrack.server.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class PrettyPrinter {

	public static String getSimpleNamesListString(final List<?> actionList) {
		final List<String> names = new ArrayList<String>();
		for (final Object action : actionList) {
			names.add(action.getClass().getSimpleName());
		}
		return "[" + Joiner.on(',').join(names) + "]";
	}
}
