package br.com.oncast.ontrack.server.util.converter.mocks;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

import java.util.ArrayList;
import java.util.List;

@ConvertTo(ModelActionEntityMockWithListOfActions.class)
public class ModelActionMockWithListOfActions {
	private final String aString = "a string";

	private final List<ModelActionMockWithListOfActions> anActionList = new ArrayList<ModelActionMockWithListOfActions>();

	public List<ModelActionMockWithListOfActions> getAnActionList() {
		return anActionList;
	}

	public void addAction(final ModelActionMockWithListOfActions action) {
		anActionList.add(action);
	}

	public String getAString() {
		return aString;
	}
};