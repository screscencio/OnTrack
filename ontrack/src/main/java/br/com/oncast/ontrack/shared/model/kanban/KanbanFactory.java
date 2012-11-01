package br.com.oncast.ontrack.shared.model.kanban;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class KanbanFactory {

	public static Kanban createEmpty() {
		return new Kanban();
	}

	public static Kanban createFor(final Release release) {
		final Kanban kanban = new Kanban();
		addColumnsFrom(kanban, release);
		return kanban;
	}

	private static void addColumnsFrom(final Kanban kanban, final Release release) {
		final HashSet<String> addedColumns = new HashSet<String>();
		for (final Scope scope : release.getScopeList()) {
			for (final Scope task : scope.getAllLeafs()) {
				final String description = task.getProgress().getDescription();
				if (addedColumns.add(description)) kanban.appendColumn(description);
			}
		}
	}

	protected static SimpleKanban merge(final SimpleKanban baseKanban, final SimpleKanban otherKanban) {
		final SimpleKanban newKanban = new SimpleKanban();

		for (final KanbanColumn column : baseKanban.getColumns()) {
			newKanban.appendColumn(column.getDescription());
		}

		final List<KanbanColumn> otherColumns = otherKanban.getColumns();
		Collections.reverse(otherColumns);
		for (final KanbanColumn column : otherColumns) {
			newKanban.prependColumn(column.getDescription());
		}
		return newKanban;
	}
}
