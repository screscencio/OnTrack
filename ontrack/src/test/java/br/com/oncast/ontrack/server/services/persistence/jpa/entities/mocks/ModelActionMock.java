package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import br.com.oncast.ontrack.server.util.converter.annotations.MapTo;

@MapTo(ModelActionEntityMock.class)
public class ModelActionMock {
	private final String aString = "a string";

	public String getAString() {
		return aString;
	}
};