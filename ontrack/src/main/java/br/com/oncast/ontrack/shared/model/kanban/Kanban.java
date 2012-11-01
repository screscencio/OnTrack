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

	public KanbanColumn getDoneColumn() {
		return doneColumn;
	}

	public KanbanColumn getNotStartedColumn() {
		return notStartedColumn;
	}

	@Override
	public List<KanbanColumn> getColumns() {
		final List<KanbanColumn> columns = fullKanban.getColumns();
		columns.add(0, notStartedColumn);
		columns.add(doneColumn);
		return columns;
	}

	public List<KanbanColumn> getNonStaticColumns() {
		return fullKanban.getColumns();
	}

	public boolean hasNonInferedColumn(final String columnDescription) {
		return isStaticColumn(columnDescription) || kanbanWithoutInference.hasColumn(getNormalizedDescription(columnDescription));
	}

	@Override
	public boolean hasColumn(final String columnDescription) {
		return getColumn(columnDescription) != null;
	}

	@Override
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
		assureIsAnEditableColumn(columnDescription);

		fullKanban.removeColumn(columnDescription);

		if (!kanbanWithoutInferenceContainsColumn(columnDescription)) return;
		kanbanWithoutInference.removeColumn(columnDescription);
	}

	@Override
	public void moveColumn(final String columnDescription, final int requestedIndex) {
		assureIsAnEditableColumn(columnDescription);

		fullKanban.moveColumn(columnDescription, requestedIndex);

		if (!kanbanWithoutInferenceContainsColumn(columnDescription)) return;
		kanbanWithoutInference.moveColumn(columnDescription, requestedIndex);
	}

	@Override
	public void renameColumn(final String columnDescription, final String newDescription) {
		final KanbanColumn columnToRename = getColumn(columnDescription);
		if (columnToRename == null) throw new RuntimeException("The column with description '" + columnDescription + "' was not found.");
		if (columnToRename.isStaticColumn()) throw new RuntimeException("It's not possible to rename a static column");

		if (columnToRename.getDescription().equals(newDescription)) return;

		final KanbanColumn desiredColumn = getColumn(newDescription);
		if (desiredColumn != null && desiredColumn.isStaticColumn()) throw new RuntimeException("It's not possible to rename to a static column");

		if (desiredColumn != null && !columnToRename.equals(desiredColumn)) {
			fullKanban.removeColumn(columnDescription);
			if (kanbanWithoutInferenceContainsColumn(columnDescription)) kanbanWithoutInference.removeColumn(columnDescription);
			return;
		}
		fullKanban.renameColumn(columnDescription, newDescription);

		if (!kanbanWithoutInferenceContainsColumn(columnDescription)) return;
		kanbanWithoutInference.renameColumn(columnDescription, newDescription);
	}

	public void merge(final Kanban kanbanToMerge) {
		if (isLocked || kanbanToMerge.isEmpty()) return;
		fullKanban = KanbanFactory.merge(kanbanToMerge.fullKanban, kanbanWithoutInference);
	}

	public boolean isStaticColumn(final String columnDescription) {
		final String key = getNormalizedDescription(columnDescription);
		return STATIC_COLUMNS.contains(key.toLowerCase());
	}

	public KanbanColumn getColumnPredeceding(final String columnDescription) {
		final int index = indexOf(columnDescription) - 1;
		return (index < 0) ? notStartedColumn : fullKanban.getColumn(index);
	}

	private boolean isEmpty() {
		return fullKanban.getColumns().isEmpty();
	}

	private void assureIsAnEditableColumn(final String columnDescription) {
		assureContainsColumn(columnDescription);
		assureIsNotAStaticColumn(columnDescription);
	}

	private void assureIsNotAStaticColumn(final String columnDescription) {
		if (isStaticColumn(columnDescription)) throw new RuntimeException("Cannot change a fixed column");
	}

	private String getNormalizedDescription(final String columnDescription) {
		return columnDescription.trim().isEmpty() ? Progress.DEFAULT_NOT_STARTED_NAME : columnDescription;
	}

	private void assureContainsColumn(final String columnDescription) {
		if (getColumn(columnDescription) == null) throw new RuntimeException("The column with description '" + columnDescription + "' was not found.");
	}

	private boolean kanbanWithoutInferenceContainsColumn(final String columnDescription) {
		return kanbanWithoutInference.getColumn(columnDescription) != null;
	}

	public int size() {
		return getColumns().size();
	}
}
