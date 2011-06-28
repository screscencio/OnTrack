package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.MapTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@MapTo(ModelActionEntityMockWithListOfString.class)
public class ModelActionMockWithListOfActions implements ModelAction {
	private final UUID aUUID = new UUID();
	private final String aString = "a string";
	private final List<ModelActionMockWithListOfActions> anActionList = new ArrayList<ModelActionMockWithListOfActions>();

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {}

	@Override
	public UUID getReferenceId() {
		return aUUID;
	}

	public String getAString() {
		return aString;
	}

	public List<ModelActionMockWithListOfActions> getAnActionList() {
		return anActionList;
	}
};