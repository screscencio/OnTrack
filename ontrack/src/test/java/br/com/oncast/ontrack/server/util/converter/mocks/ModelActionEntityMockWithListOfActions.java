package br.com.oncast.ontrack.server.util.converter.mocks;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

import java.util.ArrayList;
import java.util.List;

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