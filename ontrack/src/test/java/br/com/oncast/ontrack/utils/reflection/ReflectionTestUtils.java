package br.com.oncast.ontrack.utils.reflection;

import java.lang.reflect.Field;

public class ReflectionTestUtils {
	@SuppressWarnings("unchecked")
	public static <T> T set(final Object subject, final String fieldName, final Object value) throws Exception {
		final Field field = subject.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(subject, value);
		field.setAccessible(false);
		return (T) subject;
	}
}
