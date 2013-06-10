package br.com.oncast.ontrack.shared.model.kanban;

import java.util.Collections;
import java.util.List;

public class KanbanFactory {

	public static Kanban create() {
		return new Kanban();
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
