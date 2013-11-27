package br.com.oncast.ontrack.client.ui.component.scopetree;

import br.com.oncast.ontrack.client.services.ClientServicesTestUtils;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.Effort;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.mocks.EffortDeepEqualityComparator;
import br.com.oncast.ontrack.utils.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

@GwtModule("br.com.oncast.ontrack.Application")
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
	public static void beforeClass() throws Exception {
		DeepEqualityTestUtils.setCustomDeepEqualityComparator(Effort.class, new EffortDeepEqualityComparator());
		ClientServicesTestUtils.configure().mockEssential();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		DeepEqualityTestUtils.removeCustomDeepEqualityComparator(Effort.class);
		ClientServicesTestUtils.reset();
	}

	@Before
	public void setUp() throws Exception {
		scope = getScope();
		tree = new ScopeTree();
		tree.setContext(ProjectTestUtils.createProjectContext(scope, null));

		projectContext = ProjectTestUtils.createProjectContext(scope, ReleaseTestUtils.createRelease(""));
		actionExecutionService = ActionExecutionFactoryTestUtil.create(projectContext);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	@After
	public void cleanUp() throws Exception {
		getBrowserSimulator().fireLoopEnd();
	}

	private Scope getScope() {
		rootScope = ScopeTestUtils.createScope("Project");
		firstScope = ScopeTestUtils.createScope("1");
		rootScope.add(firstScope);
		childScope = ScopeTestUtils.createScope("1.1");
		firstScope.add(childScope);
		firstScope.add(ScopeTestUtils.createScope("1.2"));
		rootScope.add(ScopeTestUtils.createScope("2"));

		return rootScope;
	}

	private ProjectContext getModifiedContext() {
		final Scope projectScope = ScopeTestUtils.createScope("Project");
		projectScope.add(ScopeTestUtils.createScope("1").add(ScopeTestUtils.createScope("1.2")));
		projectScope.add(ScopeTestUtils.createScope("1.1"));
		projectScope.add(ScopeTestUtils.createScope("2"));

		return ProjectTestUtils.createProjectContext(projectScope, null);
	}

	private ProjectContext getUnmodifiedContext() {
		final Scope unmodifiedScope = ScopeTestUtils.createScope("Project");
		unmodifiedScope.add(ScopeTestUtils.createScope("1").add(ScopeTestUtils.createScope("1.1")).add(ScopeTestUtils.createScope("1.2")));
		unmodifiedScope.add(ScopeTestUtils.createScope("2"));

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
}
