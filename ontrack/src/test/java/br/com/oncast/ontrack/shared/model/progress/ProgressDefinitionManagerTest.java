package br.com.oncast.ontrack.shared.model.progress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ProgressDefinitionManagerTest {

	private ProgressDefinitionManager progressDefinitionManager;

	@Before
	public void before() {
		progressDefinitionManager = ProgressDefinitionManager.getInstance();
		// Make sure this singleton is in its original state. Other tests may be changed this singleton.
		progressDefinitionManager.getProgressDefinitions().clear();
	}

	@After
	public void after() {
		// Don't affect other tests that use this singleton.
		progressDefinitionManager.getProgressDefinitions().clear();
	}

	@Test
	public void shouldContainNoProgressIfManagerWasNotPopulated() {
		assertEquals(0, progressDefinitionManager.getProgressDefinitions().size());
	}

	@Test
	public void shouldContainTheAppDefinedProgress() {
		progressDefinitionManager.populate(ProjectTestUtils.createProject());

		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.NOT_STARTED.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.UNDER_WORK.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.DONE.getDescription()));
		assertEquals(3, progressDefinitionManager.getProgressDefinitions().size());
	}

	@Test
	public void shouldNotContainTheUserDefinedProgress() {
		final String userDefinedProgress = "in design";

		final Scope scope = ScopeTestUtils.getScope();
		scope.getChild(0).getProgress().setDescription(userDefinedProgress);

		final Project project = ProjectTestUtils.createProject(scope, null);
		progressDefinitionManager.populate(project);

		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.NOT_STARTED.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.UNDER_WORK.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.DONE.getDescription()));
		assertFalse(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress));
		assertEquals(3, progressDefinitionManager.getProgressDefinitions().size());
	}
}
