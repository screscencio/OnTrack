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
		final HashSet<String> columns = new HashSet<String>();
		for (final Scope scope : release.getScopeList()) {
			final String description = scope.getProgress().getDescription();
			if (!FIXED_COLUMNS.contains(description) && columns.add(description)) kanban.addColumn(getColumn(description));
		}
	}

	private static KanbanColumn getColumn(final String description) {
		return new KanbanColumn(description);
	}

	public static Kanban createFrom(final Kanban kanban, final Release release) {
		final Kanban newKanban = new Kanban();

		final HashSet<String> columns = new HashSet<String>();
		for (final KanbanColumn column : kanban.getColumns()) {
			final String title = column.getTitle();
			newKanban.addColumn(new KanbanColumn(title));
			columns.add(title);
		}

		final List<Scope> scopeList = release.getScopeList();
		Collections.reverse(scopeList);
		for (final Scope scope : scopeList) {
			final String description = scope.getProgress().getDescription();
			if (!FIXED_COLUMNS.contains(description) && columns.add(description)) newKanban.prependColumn(getColumn(description));
		}
		return newKanban;
	}

	public static Kanban createInfered(final Kanban kanban, final Release release) {
		// FIXME MAT implement and test
		return null;
	}
}
