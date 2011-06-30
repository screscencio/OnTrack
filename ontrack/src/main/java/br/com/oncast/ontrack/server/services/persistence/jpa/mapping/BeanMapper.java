package br.com.oncast.ontrack.server.services.persistence.jpa.mapping;

import java.lang.reflect.Field;

import br.com.oncast.ontrack.server.services.persistence.jpa.entities.ActionEntity;

public class BeanMapper {

	public static ActionEntity map(final Object action) {
		final ActionEntity entity = findMappedClass(action);
		populateEntity(action, entity);
		return entity;
	}

	private static ActionEntity findMappedClass(final Object action) {
		final MapTo annotation = action.getClass().getAnnotation(MapTo.class);

		if (annotation == null) throw new RuntimeException("The class of type " + action.getClass() + " must be annotated with " + MapTo.class
				+ " for being persisted.");

		try {
			return annotation.value().newInstance();
		}
		catch (final Exception e) {
			throw new RuntimeException("The instance of " + annotation.value() + " could not be created.", e);
		}
	}

	private static void populateEntity(final Object action, final Object entity) {
		final Field[] fields = action.getClass().getDeclaredFields();
		for (final Field field : fields) {
			field.setAccessible(true);

			try {
				final Object value = TypeMapperFactory.getInstance().map(action, entity, field);

				final Field entityField = entity.getClass().getDeclaredField(field.getName());
				entityField.setAccessible(true);
				entityField.set(entity, value);
			}
			catch (final Exception e) {
				throw new RuntimeException("There was not possible to populate the entity.", e);
			}
		}
	}
}
