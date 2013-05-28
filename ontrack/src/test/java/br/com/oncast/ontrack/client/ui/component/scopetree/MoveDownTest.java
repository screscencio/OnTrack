package br.com.oncast.ontrack.client.ui.component.scopetree;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.ClientServicesTestUtils;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

@GwtModule("br.com.oncast.ontrack.Application")
public class MoveDownTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private Scope thirdScope;
	private Scope lastScope;
	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private ActionExecutionServiceImpl actionExecutionService;

	@BeforeClass
	public static void beforeClass() throws Exception {
		ClientServicesTestUtils.configure().mockEssential();
	}

	@AfterClass
	public static void afterClass() throws Exception {
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

	private Scope getScope() {
		rootScope = ScopeTestUtils.createScope("Project");
		firstScope = ScopeTestUtils.createScope("1");
		rootScope.add(firstScope);
		rootScope.add(ScopeTestUtils.createScope("2"));
		thirdScope = ScopeTestUtils.createScope("3");
		rootScope.add(thirdScope);
		lastScope = ScopeTestUtils.createScope("4");
		rootScope.add(lastScope);

		return rootScope;
	}

	private ProjectContext getModifiedScope() {
		final Scope projectScope = ScopeTestUtils.createScope("Project");
		projectScope.add(ScopeTestUtils.createScope("2"));
		projectScope.add(ScopeTestUtils.createScope("1"));
		projectScope.add(ScopeTestUtils.createScope("4"));
		projectScope.add(ScopeTestUtils.createScope("3"));

		return ProjectTestUtils.createProjectContext(projectScope, null);
	}

	private ProjectContext getUnmodifiedScope() {
		final Scope unmodifiedScope = ScopeTestUtils.createScope("Project");
		unmodifiedScope.add(ScopeTestUtils.createScope("1"));
		unmodifiedScope.add(ScopeTestUtils.createScope("2"));
		unmodifiedScope.add(ScopeTestUtils.createScope("3"));
		unmodifiedScope.add(ScopeTestUtils.createScope("4"));

		return ProjectTestUtils.createProjectContext(unmodifiedScope, null);
	}

	private ScopeTree getUnmodifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getUnmodifiedScope());
		return treeAfterManipulation;
	}

	private ScopeTree getModifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setContext(getModifiedScope());
		return treeAfterManipulation;
	}

	@Test
	public void shouldMoveDown() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveDownAction(firstScope.getId()));
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveDownAction(thirdScope.getId()));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedScope().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveLast() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveDownAction(lastScope.getId()));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveRoot() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveDownAction(rootScope.getId()));
	}

	@Test
	public void shouldMoveUpAfterUndo() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveDownAction(firstScope.getId()));
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveDownAction(thirdScope.getId()));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedScope().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());

		actionExecutionService.onUserActionUndoRequest();
		actionExecutionService.onUserActionUndoRequest();

		DeepEqualityTestUtils.assertObjectEquality(scope, getUnmodifiedScope().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getUnmodifiedTree());
	}
}
