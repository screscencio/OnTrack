package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;

public class ModelActionEntityMockWithListOfActions extends ModelActionEntity {
	private String aUUID;
	private String aString;
	private final List<ModelActionEntityMockWithListOfActions> anActionList = new ArrayList<ModelActionEntityMockWithListOfActions>();

	public String getReferenceId() {
		return aUUID;
	}

	public String getAString() {
		return aString;
	}

	public List<ModelActionEntityMockWithListOfActions> getAnActionList() {
		return anActionList;
	}
}