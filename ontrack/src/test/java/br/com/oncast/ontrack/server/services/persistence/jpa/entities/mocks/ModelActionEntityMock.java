package br.com.oncast.ontrack.server.services.persistence.jpa.entities.mocks;

import br.com.oncast.ontrack.server.util.converter.annotations.MapTo;

@MapTo(ModelActionMock.class)
public class ModelActionEntityMock {
	private String aString;

	public String getAString() {
		return aString;
	}
}