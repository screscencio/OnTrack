package br.com.oncast.ontrack.shared.model.kanban;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;

// TODO +++ Incorporate SimpleKanban into this class for efficiency and to remove duplications.
public class Kanban extends SimpleKanban implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final ArrayList<String> STATIC_COLUMNS = new ArrayList<String>();

	static {
		STATIC_COLUMNS.add(Progress.DEFAULT_NOT_STARTED_NAME.toLowerCase());
		STATIC_COLUMNS.add(ProgressState.DONE.getDescription().toLowerCase());
	}

	private boolean isLocked;

	// IMPORTANT Non final attributes are needed for serialization. Do not remove this.
	private SimpleKanban fullKanban = null;

	private SimpleKanban kanbanWithoutInference = null;

	private KanbanColumn notStartedColumn = null;

	private KanbanColumn doneColumn = null;

	protected Kanban() {
		fullKanban = new SimpleKanban();
		kanbanWithoutInference = new SimpleKanban();
		isLocked = false;
		notStartedColumn = new KanbanColumn(Progress.DEFAULT_NOT_STARTED_NAME, true);
		doneColumn = new KanbanColumn(ProgressState.DONE.getDescription(), true);
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(final boolean bool) {
		isLocked = bool;
	}

	@Override
	public List<KanbanColumn> getColumns() {
		final List<KanbanColumn> columns = fullKanban.getColumns();
		columns.add(0, notStartedColumn);
		columns.add(doneColumn);
		return columns;
	}

	public boolean hasColumn(final String columnDescription) {
		return getColumn(columnDescription) != null;
	}

	public KanbanColumn getColumn(final String columnDescription) {
		final String description = getNormalizedDescription(columnDescription);
		for (final KanbanColumn column : getColumns()) {
			if (column.getDescription().toLowerCase().equals(description.toLowerCase())) return column;
		}
		return null;
	}

	@Override
	public int indexOf(final String columnDescription) {
		return fullKanban.indexOf(columnDescription);
	}

	@Override
	public void appendColumn(final String columnDescription) {
		if (isStaticColumn(columnDescription)) return;

		kanbanWithoutInference.appendColumn(columnDescription);
		fullKanban.appendColumn(columnDescription);
	}

	@Override
	public void prependColumn(final String columnDescription) {
		if (isStaticColumn(columnDescription)) return;

		kanbanWithoutInference.prependColumn(columnDescription);
		fullKanban.prependColumn(columnDescription);
	}

	@Override
	public void removeColumn(final String columnDescription) {
		avoidStaticColumns(columnDescription);

		kanbanWithoutInference.removeColumn(columnDescription);
		fullKanban.removeColumn(columnDescription);
	}

	@Override
	public void moveColumn(final String columnDescription, final int requestedIndex) {
		avoidStaticColumns(columnDescription);

		kanbanWithoutInference.moveColumn(columnDescription, requestedIndex);
		fullKanban.moveColumn(columnDescription, requestedIndex);
	}

	public void merge(final Kanban kanbanToMerge) {
		if (isLocked || kanbanToMerge.isEmpty()) return;
		fullKanban = KanbanFactory.merge(kanbanToMerge.fullKanban, kanbanWithoutInference);
	}

	private boolean isEmpty() {
		return fullKanban.getColumns().isEmpty();
	}

	private void avoidStaticColumns(final String columnDescription) {
		if (isStaticColumn(columnDescription)) throw new RuntimeException("Cannot move a fixed column");
	}

	public boolean isStaticColumn(final String columnDescription) {
		final String key = getNormalizedDescription(columnDescription);
		return STATIC_COLUMNS.contains(key.toLowerCase());
	}

	private String getNormalizedDescription(final String columnDescription) {
		return columnDescription.trim().isEmpty() ? Progress.DEFAULT_NOT_STARTED_NAME : columnDescription;
	}

	public KanbanColumn getColumnPredeceding(final String columnDescription) {
		final int index = indexOf(columnDescription) - 1;
		return (index < 0) ? notStartedColumn : fullKanban.getColumn(index);
	}
}
