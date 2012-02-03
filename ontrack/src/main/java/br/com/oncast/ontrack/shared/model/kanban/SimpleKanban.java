package br.com.oncast.ontrack.shared.model.kanban;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO +++ Incorporate this class into Kanban for efficiency and to remove duplications.
public class SimpleKanban implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<KanbanColumn> columns = null;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected SimpleKanban() {
		this.columns = new ArrayList<KanbanColumn>();
	}

	protected List<KanbanColumn> getColumns() {
		return new ArrayList<KanbanColumn>(columns);
	}

	protected void appendColumn(final String columnDescription) {
		addColumn(columns.size(), columnDescription);
	}

	protected void prependColumn(final String columnDescription) {
		addColumn(0, columnDescription);
	}

	protected void moveColumn(final String columnDescription, final int requestedIndex) {
		final KanbanColumn kanbanColumn = getColumn(columnDescription);
		if (!columns.remove(kanbanColumn)) throw new RuntimeException("Column not found");
		columns.add(Math.min(requestedIndex, columns.size()), kanbanColumn);
	}

	protected int indexOf(final String columnDescription) {
		return columns.indexOf(getColumn(columnDescription));
	}

	protected void removeColumn(final String columnDescription) {
		columns.remove(getColumn(columnDescription));
	}

	private void addColumn(final int index, final String columnDescription) {
		if (getColumn(columnDescription) != null) return;
		columns.add(index, new KanbanColumn(columnDescription));
	}

	private KanbanColumn getColumn(final String columnDescription) {
		for (final KanbanColumn column : columns) {
			if (column.getDescription().equals(columnDescription)) return column;
		}
		return null;
	}

	public KanbanColumn getColumn(final int index) {
		if (index < 0 || index >= columns.size()) return null;
		return columns.get(index);
	}
}
