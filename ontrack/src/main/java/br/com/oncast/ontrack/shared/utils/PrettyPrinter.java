package br.com.oncast.ontrack.shared.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;

public class PrettyPrinter {

	public static String getSimpleNamesListString(final Collection<?> collection) {
		return mountListString(collection, new StringGetter() {
			@Override
			public String getString(final Object object) {
				return object.getClass().getSimpleName();
			}
		});
	}

	public static String getToStringListString(final Collection<?> collection) {
		return mountListString(collection, new StringGetter() {
			@Override
			public String getString(final Object object) {
				return object.toString();
			}
		});
	}

	public static String mountListString(final Collection<?> collection, final StringGetter getter) {
		final List<String> texts = new ArrayList<String>();
		for (final Object object : collection) {
			texts.add(getter.getString(object));
		}
		return "[" + Joiner.on(',').join(texts) + "]";
	}

	private interface StringGetter {
		String getString(Object object);
	}

}
