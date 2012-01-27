package br.com.oncast.ontrack.shared.model.kanban;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.shared.model.progress.Progress;

public class Kanban {

	private final Map<String, KanbanColumn> columnMap;

	public Kanban(final Map<String, KanbanColumn> columnMap) {
		this.columnMap = columnMap;
	}

	public List<KanbanColumn> getColumns() {
		return new ArrayList<KanbanColumn>(columnMap.values());
	}

	public KanbanColumn columnForDescription(final String description) {
		final String key = description.trim().isEmpty() ? Progress.DEFAULT_NOT_STARTED_NAME : description;
		return columnMap.get(key);
	}
}
