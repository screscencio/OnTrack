package br.com.oncast.ontrack.shared.model.kanban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Kanban {
	private static final String DEFAULT_NOT_STARTED_NAME = "Not Started";

	private final Release release;

	public Kanban(final Release release) {
		this.release = release;
	}

	public List<KanbanColumn> getColumns() {
		final List<Scope> scopes = release.getScopeList();
		final Map<String, KanbanColumn> columnMap = new HashMap<String, KanbanColumn>();
		getFromMap(columnMap, ProgressState.NOT_STARTED.getDescription());
		getFromMap(columnMap, ProgressState.DONE.getDescription());
		for (final Scope scope : scopes)
			getFromMap(columnMap, scope.getProgress().getDescription()).addScope(scope);
		return new ArrayList<KanbanColumn>(columnMap.values());
	}

	private KanbanColumn getFromMap(final Map<String, KanbanColumn> columnMap, final String description) {
		final String key = description.trim().isEmpty() ? DEFAULT_NOT_STARTED_NAME : description;
		if (!columnMap.containsKey(key)) columnMap.put(key, new KanbanColumn(key));
		return columnMap.get(key);
	}

}
