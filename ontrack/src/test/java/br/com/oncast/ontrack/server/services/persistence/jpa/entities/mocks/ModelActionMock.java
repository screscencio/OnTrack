package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.MapTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@MapTo(ModelActionEntityMock.class)
public class ModelActionMock implements ModelAction {
	private final UUID aUUID = new UUID();
	private final String aString = "a string";

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		return null;
	}

	@Override
	public UUID getReferenceId() {
		return aUUID;
	}

	public String getAString() {
		return aString;
	}
};