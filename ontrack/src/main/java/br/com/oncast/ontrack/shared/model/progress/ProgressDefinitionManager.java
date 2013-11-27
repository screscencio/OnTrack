package br.com.oncast.ontrack.shared.model.progress;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;

import java.util.ArrayList;
import java.util.List;

public class ProgressDefinitionManager {

	private static ProgressDefinitionManager instance;
	private final List<String> progressDefinitionList = new ArrayList<String>();

	public static ProgressDefinitionManager getInstance() {
		if (instance == null) instance = new ProgressDefinitionManager();
		return instance;
	}

	private ProgressDefinitionManager() {}

	public void onProgressDefinition(final String description) {
		if (description == null) return;

		for (final String definition : progressDefinitionList)
			if (definition.equalsIgnoreCase(description)) return;

		progressDefinitionList.add(description);
	}

	public List<String> getProgressDefinitions() {
		return progressDefinitionList;
	}

	public void populate() {
		progressDefinitionList.clear();
		populateFromProgressState();
	}

	private void populateFromProgressState() {
		for (final ProgressState state : ProgressState.values())
			this.onProgressDefinition(state.getDescription());
	}

}
