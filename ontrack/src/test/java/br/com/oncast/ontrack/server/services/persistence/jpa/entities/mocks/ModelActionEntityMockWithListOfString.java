package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entities.ModelActionEntity;

public class ModelActionEntityMockWithListOfString implements ModelActionEntity {
	private String aUUID;
	private String aString;
	private final List<String> aStringList = new ArrayList<String>();

	public String getReferenceId() {
		return aUUID;
	}

	public String getAString() {
		return aString;
	}

	public List<String> getaStringList() {
		return aStringList;
	}
}