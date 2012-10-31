package br.com.oncast.ontrack.shared.model.kanban;

import static br.com.oncast.ontrack.utils.assertions.KanbanTestUtils.DONE;
import static br.com.oncast.ontrack.utils.assertions.KanbanTestUtils.NOT_STARTED;
import static br.com.oncast.ontrack.utils.assertions.KanbanTestUtils.assertColumns;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KanbanTest {

	@Test
	public void shouldCreateWithDefaultColumns() throws Exception {
		final Kanban kanban = new Kanban();
		assertColumns(kanban, NOT_STARTED, DONE);
	}

	@Test
	public void preppendedColumnsShouldKeepNotStartedColumnAsFirstColumn() throws Exception {
		final Kanban kanban = new Kanban();
		prependColumns(kanban, "Planning", "", NOT_STARTED, "Implementing", DONE, "Testing");
		assertColumns(kanban, NOT_STARTED, "Testing", "Implementing", "Planning", DONE);
	}

	@Test
	public void appendedColumnsShouldKeepDoneColumnAsLastColumn() {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "First", "Old Last", "New Last");

		assertColumns(kanban, NOT_STARTED, "First", "Old Last", "New Last", DONE);
	}

	@Test
	public void prependShouldNotDuplicateColumns() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "First", "Last");

		kanban.prependColumn("First");
		assertColumns(kanban, NOT_STARTED, "First", "Last", DONE);
	}

	@Test
	public void appendShouldNotDuplicateColumns() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "First", "Last");

		kanban.appendColumn("Last");
		assertColumns(kanban, NOT_STARTED, "First", "Last", DONE);
	}

	@Test
	public void shouldBeAbleToMoveColumnToZeroPosition() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "A", "B", "C");
		final int desiredIndex = 0;

		kanban.moveColumn("B", desiredIndex);
		assertEquals(desiredIndex, kanban.indexOf("B"));
	}

	@Test
	public void shouldBeAbleToMoveColumnToLastPosition() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "A", "B", "C");
		final int desiredIndex = 2;

		kanban.moveColumn("B", desiredIndex);
		assertEquals(desiredIndex, kanban.indexOf("B"));
	}

	@Test
	public void shouldBeAbleToMoveColumnLeft() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "A", "B", "C");
		final int desiredIndex = 1;

		kanban.moveColumn("C", desiredIndex);
		assertColumns(kanban, NOT_STARTED, "A", "C", "B", DONE);
	}

	@Test
	public void shouldBeAbleToMoveColumnRight() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "A", "B", "C");
		final int desiredIndex = 1;

		kanban.moveColumn("A", desiredIndex);
		assertColumns(kanban, NOT_STARTED, "B", "A", "C", DONE);
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotBeAbleToMoveDoneColumn() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "A", "B", "C");
		final int desiredIndex = 1;

		kanban.moveColumn(DONE, desiredIndex);
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotBeAbleToMoveNotStartedColumn() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "A", "B", "C");
		final int desiredIndex = 1;

		kanban.moveColumn(NOT_STARTED, desiredIndex);
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotBeAbleToMoveNotStartedColumnAsEmptyString() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "A", "B", "C");
		final int desiredIndex = 1;

		kanban.moveColumn("", desiredIndex);
	}

	@Test
	public void shouldBeAbleToMoveColumnToRight() throws Exception {
		final Kanban kanban = new Kanban();
		appendColumns(kanban, "A", "B", "C");
		final int desiredIndex = 2;

		kanban.moveColumn("B", desiredIndex);
		assertColumns(kanban, NOT_STARTED, "A", "C", "B", DONE);
	}

	@Test
	public void shouldBeAbleToMergeToAnotherKanban() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");

		final Kanban kanbanToMerge = new Kanban();
		appendColumns(kanbanToMerge, "Y", "Z");

		baseKanban.merge(kanbanToMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "Y", "Z", DONE);
	}

	@Test
	public void mergingShouldNotForgetTheOriginalColumns() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");

		final Kanban firstMerge = new Kanban();
		appendColumns(firstMerge, "Y", "Z");

		final Kanban secondMerge = new Kanban();
		appendColumns(secondMerge, "U", "V");

		baseKanban.merge(firstMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "Y", "Z", DONE);

		baseKanban.merge(secondMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "U", "V", DONE);
	}

	@Test
	public void kanbanShouldNotMergeAfterLocking() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");

		final Kanban kanbanToMerge = new Kanban();
		appendColumns(kanbanToMerge, "Y", "Z");

		baseKanban.setLocked(true);
		baseKanban.merge(kanbanToMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", DONE);
	}

	@Test
	public void kanbanShouldNotForgetTheOriginalColumnsAfterLocking() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");

		final Kanban firstMerge = new Kanban();
		appendColumns(firstMerge, "Y", "Z");

		final Kanban secondMerge = new Kanban();
		appendColumns(secondMerge, "U", "V");

		baseKanban.merge(firstMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "Y", "Z", DONE);

		baseKanban.setLocked(true);
		baseKanban.merge(secondMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "Y", "Z", DONE);

		baseKanban.setLocked(false);
		baseKanban.merge(secondMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "U", "V", DONE);
	}

	@Test
	public void kanbanShouldKeepUpdatingTheOriginalColumnsAfterLocking() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");

		final Kanban firstMerge = new Kanban();
		appendColumns(firstMerge, "Y", "Z");

		final Kanban secondMerge = new Kanban();
		appendColumns(secondMerge, "U", "V");

		baseKanban.merge(firstMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "Y", "Z", DONE);

		baseKanban.setLocked(true);
		baseKanban.appendColumn("M");
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "Y", "Z", "M", DONE);

		baseKanban.setLocked(false);
		baseKanban.merge(secondMerge);
		assertColumns(baseKanban, NOT_STARTED, "A", "B", "C", "M", "U", "V", DONE);
	}

	@Test
	public void shouldAppendWhenTheDesiredIndexIsBiggerThanTheKanbanColumnCount() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");

		final Kanban firstMerge = new Kanban();
		appendColumns(firstMerge, "W", "X", "Y", "Z");

		baseKanban.merge(firstMerge);

		baseKanban.moveColumn("B", 9);

		assertColumns(baseKanban, NOT_STARTED, "A", "C", "W", "X", "Y", "Z", "B", DONE);
	}

	@Test
	public void shouldBeAbleToRenameAColumn() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");
		baseKanban.renameColumn("B", "2");
		assertColumns(baseKanban, NOT_STARTED, "A", "2", "C", DONE);
	}

	@Test
	public void renameShouldBeCaseInsensitiveOnMatch() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");
		baseKanban.renameColumn("b", "2");
		assertColumns(baseKanban, NOT_STARTED, "A", "2", "C", DONE);
	}

	@Test
	public void renameShouldBeCaseSensitiveOnDescriptionUpdate() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");
		baseKanban.renameColumn("B", "b");
		assertColumns(baseKanban, NOT_STARTED, "A", "b", "C", DONE);
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotBeAbleToRenameAStaticColumn() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");
		baseKanban.renameColumn(NOT_STARTED, "b");
		assertColumns(baseKanban, NOT_STARTED, "A", "b", "C", DONE);
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotBeAbleToRenameAColumnThatTheKanbanNotContains() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");
		baseKanban.renameColumn("D", "b");
		assertColumns(baseKanban, NOT_STARTED, "A", "b", "C", DONE);
	}

	@Test
	public void renameToAnExistantColumnShouldOnlyRemoveThePreviousColumn() throws Exception {
		final Kanban baseKanban = new Kanban();
		appendColumns(baseKanban, "A", "B", "C");
		baseKanban.renameColumn("B", "C");
		assertColumns(baseKanban, NOT_STARTED, "A", "C", DONE);
	}

	private void prependColumns(final Kanban kanban, final String... columns) {
		for (final String column : columns) {
			kanban.prependColumn(column);
		}
	}

	private void appendColumns(final Kanban kanban, final String... columns) {
		for (final String column : columns) {
			kanban.appendColumn(column);
		}
	}

}
