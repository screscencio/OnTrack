package br.com.oncast.ontrack.server.util.converter.mocks;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

@ConvertTo(ModelActionEntityMock.class)
public class ModelActionMock {
	private final String aString = "a string";

	public String getAString() {
		return aString;
	}
};