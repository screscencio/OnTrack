package br.com.oncast.ontrack.server.services.persistence.jpa.entities;

import static org.junit.Assert.fail;

import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.MappedPersistentEntity;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class EntityFactoryTest {

	@Test
	public void translateTest() throws Exception {
		final ModelAction modelAction = new ModelActionMock();
		final ModelActionEntity entitiy = EntityFactory.translate(modelAction);

		fail();
	}

	@MappedPersistentEntity(ModelActionEntityMock.class)
	class ModelActionMock implements ModelAction {
		private final UUID referenceId = new UUID();

		@Override
		public void rollback(final ProjectContext context) throws UnableToCompleteActionException {}

		@Override
		public UUID getReferenceId() {
			return referenceId;
		}

		@Override
		public void execute(final ProjectContext context) throws UnableToCompleteActionException {}
	};

	class ModelActionEntityMock implements ModelActionEntity {
		private String referenceId;

		public String getReferenceId() {
			return referenceId;
		}
	}
}
