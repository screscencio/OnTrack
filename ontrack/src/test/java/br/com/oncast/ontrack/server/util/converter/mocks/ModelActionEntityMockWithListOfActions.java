package br.com.oncast.ontrack.server.util.converter.mocks;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertTo;

@ConvertTo(ModelActionMockWithListOfActions.class)
public class ModelActionEntityMockWithListOfActions {
	private String aString;

	private List<ModelActionEntityMockWithListOfActions> anActionList = null;

	public ModelActionEntityMockWithListOfActions() {
		anActionList = new ArrayList<ModelActionEntityMockWithListOfActions>();
	}

	public List<ModelActionEntityMockWithListOfActions> getAnActionList() {
		return anActionList;
	}

	public String getAString() {
		return aString;
	}
}