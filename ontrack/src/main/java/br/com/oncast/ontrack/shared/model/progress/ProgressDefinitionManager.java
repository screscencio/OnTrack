package br.com.oncast.ontrack.shared.model.progress;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProgressDefinitionManager {

	private static ProgressDefinitionManager instance;
	private final Set<String> progressDefinitionSet = new HashSet<String>();

	public static ProgressDefinitionManager getInstance() {
		if (instance == null) instance = new ProgressDefinitionManager();
		return instance;
	}

	private ProgressDefinitionManager() {}

	public void onProgressDefinition(final String description) {
		if (description == null || description.isEmpty()) return;
		for (final String definition : progressDefinitionSet)
			if (definition.equalsIgnoreCase(description)) return;

		progressDefinitionSet.add(description);
	}

	public Set<String> getProgressDefinitions() {
		return progressDefinitionSet;
	}

	public void populate(final Project project) {
		progressDefinitionSet.clear();
		populateFromProgressState();
		populateFromScope(project.getProjectScope());
	}

	private void populateFromProgressState() {
		for (final ProgressState state : ProgressState.values())
			this.onProgressDefinition(state.getDescription());
	}

	private void populateFromScope(final Scope scope) {
		this.onProgressDefinition(scope.getProgress().getDescription());
		for (final Scope scopeChild : scope.getChildren())
			populateFromScope(scopeChild);
	}
}
