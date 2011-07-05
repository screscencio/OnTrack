package br.com.oncast.ontrack.server.util.converter.mocks;

import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;

@ConvertTo(ModelActionMock.class)
public class ModelActionEntityMock {
	private String aString;

	public String getAString() {
		return aString;
	}
}