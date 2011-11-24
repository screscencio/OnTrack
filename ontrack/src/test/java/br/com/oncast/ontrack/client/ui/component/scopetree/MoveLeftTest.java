package br.com.oncast.ontrack.client.ui.component.scopetree;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.mocks.EffortDeepEqualityComparator;
import br.com.oncast.ontrack.utils.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

import com.octo.gwt.test.GwtTest;

public class MoveLeftTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private Scope childScope;
	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private ActionExecutionServiceImpl actionExecutionService;

	@BeforeClass
	public static void beforeClass() {
		DeepEqualityTestUtils.setCustomDeepEqualityComparator(Effort.class, new EffortDeepEqualityComparator());
	}

	@AfterClass
	public static void afterClass() {
		DeepEqualityTestUtils.removeCustomDeepEqualityComparator(Effort.class);
	}

	@Before
	public void setUp() {
		scope = getScope();
		tree = new ScopeTree();
		tree.setContext(ProjectTestUtils.createProjectContext(scope, null));

		projectContext = ProjectTestUtils.createProjectContext(scope, ReleaseFactoryTestUtil.create(""));
		actionExecutionService = ActionExecutionFactoryTestUtil.create(projectContext);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() {
		rootScope = new Scope("Project");
		firstScope = new Scope("1");
		rootScope.add(firstScope);
		childScope = new Scope("1.1");
		firstScope.add(childScope);
		firstScope.add(new Scope("1.2"));
		rootScope.add(new Scope("2"));

		return rootScope;
	}

	private ProjectContext getModifiedContext() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("1").add(new Scope("1.2")));
		projectScope.add(new Scope("1.1"));
		projectScope.add(new Scope("2"));

		return ProjectTestUtils.createProjectContext(projectScope, null);
	}

	private ProjectContext getUnmodifiedContext() {
		final Scope unmodifiedScope = new Scope("Project");
		unmodifiedScope.add(new Scope("1").add(new Scope("1.1")).add(new Scope("1.2")));
		unmodifiedScope.add(new Scope("2"));

		return ProjectTestUtils.createProjectContext(unmodifiedScope, null);
	}

	private ScopeTree getUnmodifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getUnmodifiedContext());
		return treeAfterManipulation;
	}

	private ScopeTree getModifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getModifiedContext());
		return treeAfterManipulation;
	}

	@Test
	public void shouldMoveLeft() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveLeftAction(childScope.getId()));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveToTheSameLevelAsRoot() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveLeftAction(firstScope.getId()));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveRoot() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveLeftAction(rootScope.getId()));
	}

	@Test
	public void shouldMoveRightAfterUndo() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveLeftAction(childScope.getId()));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());

		actionExecutionService.onUserActionUndoRequest();

		DeepEqualityTestUtils.assertObjectEquality(scope, getUnmodifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getUnmodifiedTree());
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
