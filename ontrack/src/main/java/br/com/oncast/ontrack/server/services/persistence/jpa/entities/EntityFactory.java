package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class EntityFactory {
	final static List<ModelActionEntity> entities = new ArrayList<ModelActionEntity>();

	public static ModelActionEntity translate(final ModelAction modelAction) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void populateEntity(final ModelAction action, final ModelActionEntity actionEntity) {
		final Field[] fields = action.getClass().getDeclaredFields();
		for (final Field field : fields) {
			field.setAccessible(true);

			try {
				if (field.getGenericType() instanceof Collection) {
					final List<ModelAction> childList = new ArrayList<ModelAction>((List<ModelAction>) field.get(action));
					for (final ModelAction childAction : childList) {
						populateEntity(childAction, actionEntity.getClass().newInstance());
					}
					continue;
				}

				final Object value = field.getType().equals(UUID.class) ? field.get(action).toString() : field.get(action);
				final Field entityField = actionEntity.getClass().getDeclaredField(field.getName());
				entityField.setAccessible(true);
				entityField.set(actionEntity, value);

				entities.add(actionEntity);
			}
			catch (final Exception e) {
				throw new RuntimeException("There was not possible to populate the entity.", e);
			}
		}

	}

}
