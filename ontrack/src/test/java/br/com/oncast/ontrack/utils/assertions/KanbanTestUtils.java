package br.com.oncast.ontrack.utils.assertions;

import java.util.ArrayList;

import org.junit.Assert;

import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.kanban.KanbanFactory;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;

public class KanbanTestUtils {

	public static final String DONE = ProgressState.DONE.getDescription();
	public static final String NOT_STARTED = Progress.DEFAULT_NOT_STARTED_NAME;

	public static void assertColumns(final Kanban kanban, final String... columnTitles) {
		Assert.assertArrayEquals(columnTitles, getDescriptions(kanban));
	}

	private static Object[] getDescriptions(final Kanban kanban) {
		final ArrayList<String> list = new ArrayList<String>();
		for (final KanbanColumn c : kanban.getColumns()) {
			list.add(c.getDescription());
		}
		return list.toArray();
	}

	public static Kanban createWith(final String... columnDescriptions) {
		final Kanban kanban = KanbanFactory.createEmpty();
		for (final String description : columnDescriptions) {
			kanban.appendColumn(description);
		}
		return kanban;
	}
}
