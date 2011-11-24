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
	public void shouldContainTheUserDefinedProgress() {
		final String userDefinedProgress = "in design";

		final Scope scope = ScopeTestUtils.getScope();
		scope.getChild(0).getProgress().setDescription(userDefinedProgress);

		final Project project = ProjectTestUtils.createProject(scope, null);
		progressDefinitionManager.populate(project);

		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.NOT_STARTED.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.UNDER_WORK.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.DONE.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress));
		assertEquals(4, progressDefinitionManager.getProgressDefinitions().size());
	}

	@Test
	public void shouldContainAllUserDefinedProgresses() {
		final String userDefinedProgress1 = "in design";
		final String userDefinedProgress2 = "test";
		final String userDefinedProgress3 = "homologation";
		final String userDefinedProgress4 = "for PO checking";

		final Scope scope = ScopeTestUtils.getScope();
		scope.getChild(0).getProgress().setDescription(userDefinedProgress1);
		scope.getChild(0).getChild(0).getProgress().setDescription(userDefinedProgress2);
		scope.getChild(1).getProgress().setDescription(userDefinedProgress3);
		scope.getChild(2).getProgress().setDescription(userDefinedProgress4);

		final Project project = ProjectTestUtils.createProject(scope, null);
		progressDefinitionManager.populate(project);

		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.NOT_STARTED.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.UNDER_WORK.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.DONE.getDescription()));

		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress1));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress2));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress3));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress4));

		assertEquals(7, progressDefinitionManager.getProgressDefinitions().size());
	}

	@Test
	public void shouldNotContainAProgressThatIsNotInAppDefinedProgressesAndWasNotDefinedByUser() {
		final String userDefinedProgress1 = "in design";
		final String userDefinedProgress2 = "test";
		final String userDefinedProgress3 = "homologation";
		final String userDefinedProgress4 = "for PO checking";

		final Scope scope = ScopeTestUtils.getScope();
		scope.getChild(0).getProgress().setDescription(userDefinedProgress1);
		scope.getChild(0).getChild(0).getProgress().setDescription(userDefinedProgress2);
		scope.getChild(1).getProgress().setDescription(userDefinedProgress3);
		scope.getChild(2).getProgress().setDescription(userDefinedProgress4);

		final Project project = ProjectTestUtils.createProject(scope, null);
		progressDefinitionManager.populate(project);

		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.NOT_STARTED.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.UNDER_WORK.getDescription()));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(ProgressState.DONE.getDescription()));

		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress1));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress2));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress3));
		assertTrue(progressDefinitionManager.getProgressDefinitions().contains(userDefinedProgress4));

		assertFalse(progressDefinitionManager.getProgressDefinitions().contains("Anything"));
		assertFalse(progressDefinitionManager.getProgressDefinitions().contains("Other thing"));
		assertFalse(progressDefinitionManager.getProgressDefinitions().contains("Another state not defined"));
		assertFalse(progressDefinitionManager.getProgressDefinitions().contains("Anything"));

		assertEquals(7, progressDefinitionManager.getProgressDefinitions().size());
	}
}
