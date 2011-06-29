package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.MapTo;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class BeanMapper {

	public static ModelActionEntity map(final Object childAction) {
		final ModelActionEntity entity = findMappedClass(childAction);
		populateEntity(childAction, entity);
		return entity;
	}

	private static ModelActionEntity findMappedClass(final Object childAction) {
		final MapTo annotation = childAction.getClass().getAnnotation(MapTo.class);

		if (annotation == null) throw new RuntimeException("The class of type " + childAction.getClass() + " must be annotated with " + MapTo.class
				+ " for being persisted.");

		try {
			return annotation.value().newInstance();
		}
		catch (final Exception e) {
			throw new RuntimeException("The instance of " + annotation.value() + " could not be created.", e);
		}
	}

	private static void populateEntity(final Object childAction, final ModelActionEntity actionEntity) {
		final Field[] fields = childAction.getClass().getDeclaredFields();
		for (final Field field : fields) {
			field.setAccessible(true);

			try {
				if (field.getType().equals(List.class)) {
					new ListMapper().map(childAction, actionEntity, field);
				}
				else if (field.getType().equals(UUID.class)) {
					// FIXME Extract implementation for UUID
					final Object value = field.get(childAction).toString();
					final Field entityField = actionEntity.getClass().getDeclaredField(field.getName());
					entityField.setAccessible(true);
					entityField.set(actionEntity, value);
				}
				else {
					final Object value = field.get(childAction);
					final Field entityField = actionEntity.getClass().getDeclaredField(field.getName());
					entityField.setAccessible(true);
					entityField.set(actionEntity, value);
				}
			}
			catch (final Exception e) {
				throw new RuntimeException("There was not possible to populate the entity.", e);
			}
		}
	}
}
