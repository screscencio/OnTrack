package br.com.oncast.ontrack.shared.model.kanban;

import java.io.Serializable;
import java.util.List;

public class Kanban extends SimpleKanban implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isLocked;

	private SimpleKanban fullKanban = null;

	private SimpleKanban kanbanWithoutInference = null;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected Kanban() {
		fullKanban = new SimpleKanban();
		kanbanWithoutInference = new SimpleKanban();
		isLocked = false;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(final boolean bool) {
		isLocked = bool;
	}

	@Override
	public List<KanbanColumn> getColumns() {
		return fullKanban.getColumns();
	}

	@Override
	public KanbanColumn getColumnForDescription(final String description) {
		return fullKanban.getColumnForDescription(description);
	}

	@Override
	public int indexOf(final String columnDescription) {
		return fullKanban.indexOf(columnDescription);
	}

	@Override
	public void appendColumn(final String columnDescription) {
		kanbanWithoutInference.appendColumn(columnDescription);
		fullKanban.appendColumn(columnDescription);
	}

	@Override
	public void moveColumn(final String columnDescription, final int requestedIndex) {
		kanbanWithoutInference.moveColumn(columnDescription, requestedIndex);
		fullKanban.moveColumn(columnDescription, requestedIndex);
	}

	public void merge(final Kanban kanbanToMerge) {
		if (isLocked || kanbanToMerge.isEmpty()) return;
		fullKanban = KanbanFactory.merge(kanbanToMerge.fullKanban, kanbanWithoutInference);
	}

	private boolean isEmpty() {
		return getColumns().size() <= STATIC_COLUMNS.size();
	}

	@Override
	public void prependColumn(final String columnDescription) {
		kanbanWithoutInference.prependColumn(columnDescription);
		fullKanban.prependColumn(columnDescription);
	}

	@Override
	public void removeColumn(final String columnDescription) {
		kanbanWithoutInference.removeColumn(columnDescription);
		fullKanban.removeColumn(columnDescription);
	}

	public boolean hasColumnForDescription(final String columnDescription) {
		return getColumnForDescription(columnDescription) != null;
	}
}
