package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.annotations.MapTo;

@MapTo(ModelActionMock.class)
public class ModelActionEntityMock extends ModelActionEntity {
	private String aUUID;
	private String aString;

	public String getReferenceId() {
		return aUUID;
	}

	public String getAString() {
		return aString;
	}
}