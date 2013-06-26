package br.com.oncast.ontrack.shared.model.kanban;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.ClientServicesTestUtils;

public class KanbanFactoryTest {

	@Before
	public void setup() throws Exception {
		ClientServicesTestUtils.configure().mockEssential();
	}

	@Test
	public void mergedKanbanShouldContainTheBaseKanbansColumnsInOrder() throws Exception {
		final SimpleKanban kanban = createSimpleKanban("Planning", "Implementing", "Testing");
		final SimpleKanban newKanban = KanbanFactory.merge(kanban, createSimpleKanban());

		assertColumns(newKanban, "Planning", "Implementing", "Testing");
	}

	@Test
	public void shouldInsertNewColumnsOnBeginningOfNewKanban() throws Exception {
		final SimpleKanban kanban = createSimpleKanban("Planning", "Implementing", "Testing");
		final SimpleKanban newKanban = KanbanFactory.merge(kanban, createSimpleKanban("New Progress1", "New Progress2"));

		assertColumns(newKanban, "New Progress1", "New Progress2", "Planning", "Implementing", "Testing");
	}

	@Test
	public void mergedKanbanShouldNotContainRepetitionsAndKeepOrderOfBaseKanban() throws Exception {
		final SimpleKanban kanban = createSimpleKanban("Planning", "Implementing", "Testing");
		final SimpleKanban newKanban = KanbanFactory.merge(kanban, createSimpleKanban("New Progress1", "Implementing"));

		assertColumns(newKanban, "New Progress1", "Planning", "Implementing", "Testing");
	}

	private void assertColumns(final SimpleKanban kanban, final String... columnTitles) {
		Assert.assertArrayEquals(columnTitles, getDescriptions(kanban));
	}

	private static Object[] getDescriptions(final SimpleKanban kanban) {
		final ArrayList<String> list = new ArrayList<String>();
		for (final KanbanColumn c : kanban.getColumns()) {
			list.add(c.getDescription());
		}
		return list.toArray();
	}

	private SimpleKanban createSimpleKanban(final String... columns) {
		final SimpleKanban kanban = new SimpleKanban();
		for (final String column : columns)
			kanban.appendColumn(column);
		return kanban;
	}
}
