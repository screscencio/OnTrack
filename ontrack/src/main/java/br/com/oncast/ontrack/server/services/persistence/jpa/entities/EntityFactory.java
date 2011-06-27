package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertFatherAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class EntityFactory {
	final static List<ModelActionEntity> entities = new ArrayList<ModelActionEntity>();

	public static List<ModelActionEntity> translate(final ModelAction action) {

		// FIXME Make the instantiation of model action entities generic using annotation in model action class
		if (action instanceof ScopeInsertChildAction) {
			final ScopeInsertChildActionEntity actionEntity = new ScopeInsertChildActionEntity();
			populateEntity(action, actionEntity);
			return entities;
		}
		else if (action instanceof ScopeInsertFatherAction) {
			final ScopeInsertFatherActionEntity modelAction = new ScopeInsertFatherActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}
		else if (action instanceof ScopeInsertSiblingDownAction) {
			final ScopeInsertSiblingDownActionEntity modelAction = new ScopeInsertSiblingDownActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}
		else if (action instanceof ScopeInsertSiblingUpAction) {
			final ScopeInsertSiblingUpActionEntity modelAction = new ScopeInsertSiblingUpActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}
		else if (action instanceof ScopeMoveUpAction) {
			final ScopeMoveUpActionEntity modelAction = new ScopeMoveUpActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}
		else if (action instanceof ScopeMoveDownAction) {
			final ScopeMoveDownActionEntity modelAction = new ScopeMoveDownActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}
		else if (action instanceof ScopeMoveLeftAction) {
			final ScopeMoveLeftActionEntity modelAction = new ScopeMoveLeftActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}
		else if (action instanceof ScopeMoveRightAction) {
			final ScopeMoveRightActionEntity modelAction = new ScopeMoveRightActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}
		else if (action instanceof ScopeUpdateAction) {
			final ScopeUpdateActionEntity modelAction = new ScopeUpdateActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}
		else if (action instanceof ScopeRemoveAction) {
			final ScopeRemoveActionEntity modelAction = new ScopeRemoveActionEntity();
			populateEntity(action, modelAction);
			return entities;
		}

		// TODO Throw expecific exception when action cannot be mapped.
		throw new RuntimeException("Unable to map action");
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
