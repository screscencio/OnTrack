package br.com.oncast.ontrack.shared.model.kanban;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

// FIXME LOBO Test this class
public class KanbanFactory {

	private static final ArrayList<String> FIXED_COLUMNS = new ArrayList<String>();

	static {
		FIXED_COLUMNS.add(ProgressState.NOT_STARTED.getDescription());
		FIXED_COLUMNS.add(ProgressState.DONE.getDescription());
	}

	public static Kanban createFor(final Release release) {
		final Kanban kanban = new Kanban();
		kanban.addColumn(getColumn(Progress.DEFAULT_NOT_STARTED_NAME));

		addColumnsFrom(kanban, release);

		kanban.addColumn(getColumn(ProgressState.DONE.getDescription()));
		return kanban;
	}

	private static void addColumnsFrom(final Kanban kanban, final Release release) {
		final HashSet<String> addedColumns = new HashSet<String>();
		for (final Scope scope : release.getScopeList()) {
			final String description = scope.getProgress().getDescription();
			if (!FIXED_COLUMNS.contains(description) && addedColumns.add(description)) kanban.addColumn(getColumn(description));
		}
	}

	private static KanbanColumn getColumn(final String description) {
		return new KanbanColumn(description);
	}

	public static Kanban merge(final Kanban baseKanban, final Kanban otherKanban) {
		final Kanban newKanban = new Kanban();
		final HashSet<String> addedColumns = new HashSet<String>();

		for (final KanbanColumn column : baseKanban.getColumns()) {
			final String title = column.getTitle();
			newKanban.addColumn(getColumn(title));
			addedColumns.add(title);
		}

		final List<KanbanColumn> otherColumns = otherKanban.getColumns();
		Collections.reverse(otherColumns);
		for (final KanbanColumn column : otherColumns) {
			final String title = column.getTitle();
			if (!FIXED_COLUMNS.contains(title) && addedColumns.add(title)) newKanban.prependColumn(getColumn(title));
		}
		return newKanban;
	}

	public static Kanban createEmpty() {
		final Kanban kanban = new Kanban();
		kanban.addColumn(getColumn(Progress.DEFAULT_NOT_STARTED_NAME));
		kanban.addColumn(getColumn(ProgressState.DONE.getDescription()));
		return kanban;
	}
}
