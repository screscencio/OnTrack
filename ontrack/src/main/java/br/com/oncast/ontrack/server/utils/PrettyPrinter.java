package br.com.oncast.ontrack.server.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;

public class PrettyPrinter {

	public static String getSimpleNamesListString(final List<?> list) {
		return createStringFromIterator(list.iterator());
	}

	public static String getSimpleNamesListString(final Set<?> set) {
		return createStringFromIterator(set.iterator());
	}

	private static String createStringFromIterator(final Iterator<?> iterator) {
		final List<String> names = new ArrayList<String>();
		while (iterator.hasNext()) {
			final Object item = iterator.next();
			names.add(item.getClass().getSimpleName());
		}
		return "[" + Joiner.on(',').join(names) + "]";
	}

}
