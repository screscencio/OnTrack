package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.MapTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@MapTo(ModelActionEntityMockWithListOfString.class)
public class ModelActionMockWithListOfString implements ModelAction {
	private final UUID aUUID = new UUID();
	private final String aString = "a string";
	private final List<String> aStringList = new ArrayList<String>();

	public ModelActionMockWithListOfString() {
		aStringList.add("1");
		aStringList.add("2");
		aStringList.add("3");
	}

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

	public List<String> getaStringList() {
		return aStringList;
	}
};