package br.com.oncast.ontrack.shared.utils;

import br.com.oncast.ontrack.shared.model.action.UserAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;

public class PrettyPrinter {

	public static String getSimpleName(final Object object) {
		return getSimpleName(object.getClass());
	}

	public static String getSimpleName(final Class<?> clazz) {
		return clazz.getName().replaceAll(".*\\.", "");
	}

	public static String getSimpleNameForUserAction(final UserAction action) {
		return getSimpleName(action.getModelAction());
	}

	public static <T> String getSimpleNamesListString(final Collection<T> collection) {
		return mountListString(collection, new StringGetter<T>() {
			@Override
			public String getString(final T object) {
				return getSimpleName(object);
			}
		});
	}

	public static <T> String getToStringListString(final Collection<T> collection) {
		return mountListString(collection, new StringGetter<T>() {
			@Override
			public String getString(final T object) {
				return object.toString();
			}
		});
	}

	public static <T> String mountListString(final Collection<T> collection, final StringGetter<T> getter) {
		final List<String> texts = new ArrayList<String>();
		for (final T object : collection) {
			texts.add(getter.getString(object));
		}
		return "[" + Joiner.on(',').join(texts) + "]";
	}

	private interface StringGetter<T> {
		String getString(T object);
	}

	public static String getToStringListString(final Object... objects) {
		if (objects.length < 1) return "";
		return "[" + Joiner.on(',').join(objects) + "]";
	}

	public static String getSimpleNamesForUserAction(final List<UserAction> actionList) {
		return mountListString(actionList, new StringGetter<UserAction>() {
			@Override
			public String getString(final UserAction object) {
				return getSimpleName(object.getModelAction());
			}
		});
	}
}
