package br.com.oncast.ontrack.shared.model.kanban;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;

public class KanbanFactoryTest {

	@Test
	public void shouldCreateWithDefaultColumns() throws Exception {
		final Kanban kanban = KanbanFactory.createFor(ReleaseFactoryTestUtil.create("Mock Release"));
		Assert.assertEquals(2, kanban.getColumns().size());
		Assert.assertThat(getTitles(kanban.getColumns()), JUnitMatchers.hasItems("Not Started", ProgressState.DONE.getDescription()));
	}

	private List<String> getTitles(final List<KanbanColumn> columns) {
		final List<String> descriptions = new ArrayList<String>();
		for (final KanbanColumn c : columns)
			descriptions.add(c.getTitle());
		return descriptions;
	}
}
