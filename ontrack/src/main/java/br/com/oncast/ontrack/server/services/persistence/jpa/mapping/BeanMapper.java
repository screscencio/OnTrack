package br.com.oncast.ontrack.server.services.persistence.jpa.mapping;

import java.lang.reflect.Field;

public class BeanMapper {

	public static Object map(final Object source) {
		final Object destination = findMappedClass(source);
		mapFields(source, destination);

		return destination;
	}

	private static Object findMappedClass(final Object source) {
		final MapTo annotation = source.getClass().getAnnotation(MapTo.class);

		if (annotation == null) throw new RuntimeException("The class of type " + source.getClass() + " must be annotated with " + MapTo.class
				+ " for being persisted.");

		try {
			return annotation.value().newInstance();
		}
		catch (final Exception e) {
			throw new RuntimeException("The instance of " + annotation.value() + " could not be created.", e);
		}
	}

	private static void mapFields(final Object source, final Object destination) {
		final Field[] fields = source.getClass().getDeclaredFields();
		for (final Field field : fields) {
			field.setAccessible(true);

			try {
				final Object value = TypeMapperFactory.getInstance().map(source, destination, field);

				final Field destinationField = destination.getClass().getDeclaredField(field.getName());
				destinationField.setAccessible(true);
				destinationField.set(destination, value);
			}
			catch (final Exception e) {
				throw new RuntimeException("There was not possible to populate the entity.", e);
			}
		}
	}
}
