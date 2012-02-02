package br.com.oncast.ontrack.shared.model.kanban;

import static br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils.createReleaseForKanbanWithColumns;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;

public class KanbanFactoryTest {

	private static final String DONE_DESCRIPTION = ProgressState.DONE.getDescription();
	private static final String NOT_STARTED_DESCRIPTION = Progress.DEFAULT_NOT_STARTED_NAME;

	@Test
	public void shouldCreateWithDefaultColumns() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns());
		Assert.assertEquals(2, kanban.getColumns().size());
		Assert.assertThat(getTitles(kanban.getColumns()), JUnitMatchers.hasItems(NOT_STARTED_DESCRIPTION, DONE_DESCRIPTION));
	}

	@Test
	public void notStartedShouldBeTheFirstColumn() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", "Implementing", "Testing"));
		assertEquals(NOT_STARTED_DESCRIPTION, kanban.getColumns().get(0).getTitle());
	}

	@Test
	public void doneShouldBeTheLastColumn() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", "Implementing", "Testing"));
		final List<KanbanColumn> columns = kanban.getColumns();
		assertEquals(DONE_DESCRIPTION, columns.get(columns.size() - 1).getTitle());
	}

	@Test
	public void columnsShouldBeOrderedByProgressDeclarationOrder() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", "Implementing", "Testing"));
		assertKanbanColumnTitles(kanban, NOT_STARTED_DESCRIPTION, "Planning", "Implementing", "Testing", DONE_DESCRIPTION);
	}

	@Test
	public void columnsShouldNotBeDuplicated() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", "Implementing", "Planning", "Testing"));
		assertKanbanColumnTitles(kanban, NOT_STARTED_DESCRIPTION, "Planning", "Implementing", "Testing", DONE_DESCRIPTION);
	}

	@Test
	public void doneShouldBeTheLastColumnEvenWhenTheScopesWithDoneProgressAreNotAtTheEnd() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", DONE_DESCRIPTION, "Implementing", DONE_DESCRIPTION,
				"Testing"));
		assertKanbanColumnTitles(kanban, NOT_STARTED_DESCRIPTION, "Planning", "Implementing", "Testing", DONE_DESCRIPTION);
	}

	@Test
	public void notStartedShouldBeTheFirstColumnEvenWhenTheScopesWithNotStartedProgressAreNotAtTheBeginning() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", "", NOT_STARTED_DESCRIPTION, "Implementing",
				DONE_DESCRIPTION, "Testing"));
		assertKanbanColumnTitles(kanban, NOT_STARTED_DESCRIPTION, "Planning", "Implementing", "Testing", DONE_DESCRIPTION);
	}

	@Test
	public void mergedKanbanShouldContainTheBaseKanbansColumnsInOrder() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", "Implementing", "Testing"));
		final Kanban newKanban = KanbanFactory.merge(kanban, KanbanFactory.createFor(createReleaseForKanbanWithColumns()));

		assertKanbanColumnTitles(newKanban, NOT_STARTED_DESCRIPTION, "Planning", "Implementing", "Testing", DONE_DESCRIPTION);
	}

	@Test
	public void shouldInsertNewColumnsOnBeginningOfNewKanban() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", "Implementing", "Testing"));

		final Release release = createReleaseForKanbanWithColumns("New Progress1", "New Progress2");
		final Kanban newKanban = KanbanFactory.merge(kanban, KanbanFactory.createFor(release));
		assertKanbanColumnTitles(newKanban, NOT_STARTED_DESCRIPTION, "New Progress1", "New Progress2", "Planning", "Implementing", "Testing", DONE_DESCRIPTION);
	}

	@Test
	public void mergedKanbanShouldNotContainRepetitionsAndKeepOrderOfBaseKanban() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(createReleaseForKanbanWithColumns("Planning", "Implementing", "Testing"));

		final Kanban newKanban = KanbanFactory.merge(kanban, KanbanFactory.createFor(createReleaseForKanbanWithColumns("New Progress1", "Implementing")));

		assertKanbanColumnTitles(newKanban, NOT_STARTED_DESCRIPTION, "New Progress1", "Planning", "Implementing", "Testing", DONE_DESCRIPTION);
	}

	private void assertKanbanColumnTitles(final Kanban kanban, final String... columnTitles) {
		final List<KanbanColumn> columns = kanban.getColumns();

		assertEquals("There are different number of columns than the expected", columnTitles.length, columns.size());
		for (int i = 0; i < columns.size(); i++) {
			assertEquals(columnTitles[i], columns.get(i).getTitle());
		}
	}

	private List<String> getTitles(final List<KanbanColumn> columns) {
		final List<String> descriptions = new ArrayList<String>();
		for (final KanbanColumn c : columns)
			descriptions.add(c.getTitle());
		return descriptions;
	}
}
