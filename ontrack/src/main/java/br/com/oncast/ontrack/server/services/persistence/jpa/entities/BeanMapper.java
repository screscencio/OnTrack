package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.MapTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class BeanMapper {

	public static ModelActionEntity map(final ModelAction modelAction) {
		final ModelActionEntity entity = findMappedClass(modelAction);
		populateEntity(modelAction, entity);
		return entity;
	}

	private static ModelActionEntity findMappedClass(final ModelAction modelAction) {
		final MapTo annotation = modelAction.getClass().getAnnotation(MapTo.class);

		if (annotation == null) throw new RuntimeException("The class of type " + modelAction.getClass() + " must be annotated with " + MapTo.class
				+ " for being persisted.");

		try {
			return annotation.value().newInstance();
		}
		catch (final Exception e) {
			throw new RuntimeException("The instance of " + annotation.value() + " could not be created.", e);
		}
	}

	private static void populateEntity(final ModelAction action, final ModelActionEntity actionEntity) {
		final Field[] fields = action.getClass().getDeclaredFields();
		for (final Field field : fields) {
			field.setAccessible(true);

			try {
				// if (field.getType().equals(List.class)) {
				// Type type = field.getGenericType();
				// if (type instanceof ParameterizedType) {
				// final ParameterizedType pt = (ParameterizedType) type;
				//
				// final List<ModelAction> childList = new ArrayList<ModelAction>((List<ModelAction>) field.get(action));
				// for (final ModelAction childAction : childList) {
				// populateEntity(childAction, actionEntity.getClass().newInstance());
				// }
				// continue;
				// }
				// }

				// FIXME Extract implementation for UUID
				final Object value = field.getType().equals(UUID.class) ? field.get(action).toString() : field.get(action);
				final Field entityField = actionEntity.getClass().getDeclaredField(field.getName());
				entityField.setAccessible(true);
				entityField.set(actionEntity, value);
			}
			catch (final Exception e) {
				throw new RuntimeException("There was not possible to populate the entity.", e);
			}
		}

	}

}
