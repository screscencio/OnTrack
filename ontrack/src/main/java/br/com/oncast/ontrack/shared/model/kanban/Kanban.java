package br.com.oncast.ontrack.shared.model.kanban;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.progress.Progress;

public class Kanban {

	private final List<KanbanColumn> columns;
	private boolean isFixed;

	protected Kanban() {
		this.columns = new ArrayList<KanbanColumn>();
		isFixed = false;
	}

	public List<KanbanColumn> getColumns() {
		return new ArrayList<KanbanColumn>(columns);
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setFixed(final boolean bool) {
		isFixed = bool;
	}

	public KanbanColumn getColumnForDescription(final String description) {
		final String key = description.trim().isEmpty() ? Progress.DEFAULT_NOT_STARTED_NAME : description;
		for (final KanbanColumn column : columns) {
			if (column.getTitle().equals(key)) return column;
		}
		return null;
	}

	public void appendColumn(final KanbanColumn column) {
		if (getColumnForDescription(column.getTitle()) != null) return;
		columns.add(columns.size() - 1, column);
	}

	protected void addColumn(final KanbanColumn column) {
		columns.add(column);
	}

	protected void prependColumn(final KanbanColumn column) {
		columns.add(1, column);
	}

}
