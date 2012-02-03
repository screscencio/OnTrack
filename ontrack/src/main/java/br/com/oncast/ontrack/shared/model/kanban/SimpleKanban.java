package br.com.oncast.ontrack.shared.model.kanban;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;

public class SimpleKanban implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static final ArrayList<String> STATIC_COLUMNS = new ArrayList<String>();

	static {
		STATIC_COLUMNS.add(ProgressState.NOT_STARTED.getDescription());
		STATIC_COLUMNS.add(ProgressState.DONE.getDescription());
	}

	private List<KanbanColumn> columns = null;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected SimpleKanban() {
		this.columns = new ArrayList<KanbanColumn>();
		columns.add(new KanbanColumn(Progress.DEFAULT_NOT_STARTED_NAME));
		columns.add(new KanbanColumn(ProgressState.DONE.getDescription()));
	}

	public List<KanbanColumn> getColumns() {
		return new ArrayList<KanbanColumn>(columns);
	}

	public KanbanColumn getColumnForDescription(final String description) {
		final String key = description.trim().isEmpty() ? Progress.DEFAULT_NOT_STARTED_NAME : description;
		for (final KanbanColumn column : columns) {
			if (column.getTitle().equals(key)) return column;
		}
		return null;
	}

	public void appendColumn(final String columnDescription) {
		if (getColumnForDescription(columnDescription) != null) return;
		columns.add(columns.size() - 1, new KanbanColumn(columnDescription));
	}

	protected void prependColumn(final String columnDescription) {
		if (getColumnForDescription(columnDescription) != null) return;
		columns.add(1, new KanbanColumn(columnDescription));
	}

	public void moveColumn(final String columnDescription, final int requestedIndex) {
		if (requestedIndex == 0 || requestedIndex == columns.size() - 1) throw new RuntimeException("Cannot move to the requested index");
		if (isStaticColumn(columnDescription)) throw new RuntimeException(
				"Cannot move fixed column");

		final KanbanColumn kanbanColumn = getColumnForDescription(columnDescription);
		if (!columns.remove(kanbanColumn)) throw new RuntimeException("Column not found");
		columns.add(requestedIndex, kanbanColumn);
	}

	private boolean isStaticColumn(final String columnDescription) {
		return STATIC_COLUMNS.contains(columnDescription) || Progress.DEFAULT_NOT_STARTED_NAME.equals(columnDescription);
	}

	public int indexOf(final String columnDescription) {
		return columns.indexOf(getColumnForDescription(columnDescription));
	}

	protected void removeColumn(final String columnDescription) {
		if (isStaticColumn(columnDescription)) return;
		columns.remove(getColumnForDescription(columnDescription));
	}
}
