package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import br.com.oncast.ontrack.server.services.persistence.jpa.entities.ModelActionEntity;

public class ModelActionEntityMock implements ModelActionEntity {
	private String aUUID;
	private String aString;

	public String getReferenceId() {
		return aUUID;
	}

	public String getAString() {
		return aString;
	}
}