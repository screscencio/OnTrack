package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class EntityFactoryTest {

	@Test
	public void translateTest() throws Exception {
		final ModelAction modelAction = createModelAction();
		final List<ModelActionEntity> entities = EntityFactory.translate(modelAction);

		assertEquals(1, entities.size());

		final ModelActionEntity entity = entities.get(0);
	}

	// FIXME Create this test using classes annotated with @PersistentEntityFor
	private ModelAction createModelAction() {
		return new ModelAction() {

			private final UUID referenceId = new UUID();

			@Override
			public void rollback(final ProjectContext context) throws UnableToCompleteActionException {}

			@Override
			public UUID getReferenceId() {
				return null;
			}

			@Override
			public void execute(final ProjectContext context) throws UnableToCompleteActionException {}
		};
	}
}
