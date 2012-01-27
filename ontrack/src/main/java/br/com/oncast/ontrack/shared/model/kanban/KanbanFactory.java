package br.com.oncast.ontrack.shared.model.kanban;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

// FIXME LOBO Test this class
public class KanbanFactory {

	public static Kanban createFor(final Release release) {
		return new Kanban(createColumnsForRelease(release));
	}

	private static Map<String, KanbanColumn> createColumnsForRelease(final Release release) {
		// FIXME BESEN
		final List<Scope> scopes = release.getScopeList();
		final Map<String, KanbanColumn> map = new HashMap<String, KanbanColumn>();
		columnForDescription(ProgressState.NOT_STARTED.getDescription(), map);
		columnForDescription(ProgressState.DONE.getDescription(), map);
		for (final Scope scope : scopes)
			columnForDescription(scope.getProgress().getDescription(), map);
		return map;
	}

	private static KanbanColumn columnForDescription(final String description, final Map<String, KanbanColumn> columnMap) {
		final String key = description.trim().isEmpty() ? Progress.DEFAULT_NOT_STARTED_NAME : description;
		if (!columnMap.containsKey(key)) columnMap.put(key, new KanbanColumn(key));
		return columnMap.get(key);
	}
}
