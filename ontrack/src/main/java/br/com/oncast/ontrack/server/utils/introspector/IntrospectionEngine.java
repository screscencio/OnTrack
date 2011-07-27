package br.com.oncast.ontrack.server.utils.introspector;

import java.lang.reflect.Field;

public class IntrospectionEngine {

	public static void introspectThroughDeclaredFields(final Object object, final Introspector<Field> introspector) throws IntrospectionException {
		try {
			final Field[] fields = object.getClass().getDeclaredFields();
			for (final Field field : fields) {
				introspector.introspect(field);
			}
		}
		catch (final Exception e) {
			throw new IntrospectionException("An exception was found while introspecting " + object.getClass().getName() + ".", e);
		}
	}

	public static Object getFieldValue(final Object instance, final Field field) throws IntrospectionException {
		Object fieldValue;
		try {
			final boolean fieldAccessibility = field.isAccessible();
			if (!fieldAccessibility) field.setAccessible(true);

			fieldValue = field.get(instance);

			field.setAccessible(fieldAccessibility);
		}
		catch (final IllegalArgumentException e) {
			throw new IntrospectionException("Error while accessing the " + instance.getClass().getName() + "'s field " + field.getName()
					+ " while trying to 'get' its value.", e);
		}
		catch (final IllegalAccessException e) {
			throw new IntrospectionException("The " + instance.getClass().getName() + "'s field " + field.getName()
					+ " could not be accessed while trying to 'get' its value.", e);
		}
		return fieldValue;
	}

	public static void setFieldValue(final Object instance, final Field field, final Object value) throws IntrospectionException {
		try {
			final boolean fieldAccessibility = field.isAccessible();
			if (!fieldAccessibility) field.setAccessible(true);

			field.set(instance, value);

			field.setAccessible(fieldAccessibility);
		}
		catch (final IllegalArgumentException e) {
			throw new IntrospectionException("Error while accessing the " + instance.getClass().getName() + "'s field " + field.getName()
					+ " while trying to 'set' its value.", e);
		}
		catch (final IllegalAccessException e) {
			throw new IntrospectionException("The " + instance.getClass().getName() + "'s field " + field.getName()
					+ " could not be accessed while trying to 'set' its value.", e);
		}
	}
}
