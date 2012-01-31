package br.com.oncast.ontrack.shared.model.kanban;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class KanbanTest {

	private static final String DONE = ProgressState.DONE.getDescription();
	private static final String NOT_STARTED = Progress.DEFAULT_NOT_STARTED_NAME;

	@Test
	public void shouldReturnTheSameColumnWhenAppended() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(getRelease());

		final KanbanColumn newColumn = new KanbanColumn("New Column");
		kanban.appendColumn(newColumn);

		assertSame(newColumn, kanban.getColumnForDescription("New Column"));
	}

	@Test
	public void appendedColumnsShouldKeepDoneColumnAsLastColumn() {
		final Kanban kanban = KanbanFactory.createFor(getRelease("First", "Old Last"));

		kanban.appendColumn(new KanbanColumn("New Last"));
		assertColumns(kanban, NOT_STARTED, "First", "Old Last", "New Last", DONE);
	}

	@Test
	public void shouldNotDuplicateColumnsOnAppend() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(getRelease("First", "Last"));

		kanban.appendColumn(new KanbanColumn("Last"));
		assertColumns(kanban, NOT_STARTED, "First", "Last", DONE);
	}

	private Release getRelease(final String... progressDescriptions) {
		final Release release = ReleaseFactoryTestUtil.create("Mock Release");
		for (final String description : progressDescriptions) {
			final Scope scope = ScopeTestUtils.createScope();
			scope.getProgress().setDescription(description);
			release.addScope(scope);
		}
		return release;
	}

	private void assertColumns(final Kanban kanban, final String... columnTitles) {
		final List<KanbanColumn> columns = kanban.getColumns();

		assertEquals("There are different number of columns than the expected", columnTitles.length, columns.size());
		for (int i = 0; i < columns.size(); i++) {
			assertEquals(columnTitles[i], columns.get(i).getTitle());
		}
	}

}
