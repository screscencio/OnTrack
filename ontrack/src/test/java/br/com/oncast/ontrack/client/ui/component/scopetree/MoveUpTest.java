package br.com.oncast.ontrack.client.ui.component.scopetree;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.ClientServiceProviderTestUtils;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.googlecode.gwt.test.GwtTest;

public class MoveUpTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private Scope lastScope;
	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private ActionExecutionServiceImpl actionExecutionService;

	@BeforeClass
	public static void beforeClass() throws Exception {
		ClientServiceProviderTestUtils.configure().mockEssential();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		ClientServiceProviderTestUtils.reset();
	}

	@Before
	public void setUp() throws Exception {
		scope = getScope();
		tree = new ScopeTree();
		tree.setContext(ProjectTestUtils.createProjectContext(scope, null));

		projectContext = ProjectTestUtils.createProjectContext(scope, ReleaseFactoryTestUtil.create(""));
		actionExecutionService = ActionExecutionFactoryTestUtil.create(projectContext);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() {
		rootScope = ScopeTestUtils.createScope("Project");
		firstScope = ScopeTestUtils.createScope("1");
		rootScope.add(firstScope);
		rootScope.add(ScopeTestUtils.createScope("2"));
		rootScope.add(ScopeTestUtils.createScope("3"));
		lastScope = ScopeTestUtils.createScope("4");
		rootScope.add(lastScope);

		return rootScope;
	}

	private ProjectContext getModifiedContext() {
		final Scope projectScope = ScopeTestUtils.createScope("Project");
		projectScope.add(ScopeTestUtils.createScope("1"));
		projectScope.add(ScopeTestUtils.createScope("2"));
		projectScope.add(ScopeTestUtils.createScope("4"));
		projectScope.add(ScopeTestUtils.createScope("3"));

		return ProjectTestUtils.createProjectContext(projectScope, null);
	}

	private ProjectContext getUnmodifiedContext() {
		final Scope unmodifiedScope = ScopeTestUtils.createScope("Project");
		unmodifiedScope.add(ScopeTestUtils.createScope("1"));
		unmodifiedScope.add(ScopeTestUtils.createScope("2"));
		unmodifiedScope.add(ScopeTestUtils.createScope("3"));
		unmodifiedScope.add(ScopeTestUtils.createScope("4"));

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
	public void shouldMoveUp() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveUpAction(lastScope.getId()));

		DeepEqualityTestUtils.assertObjectEquality(scope, getModifiedContext().getProjectScope());
		DeepEqualityTestUtils.assertObjectEquality(tree, getModifiedTree());
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveUpFirstItem() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveUpAction(firstScope.getId()));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveUpRoot() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveUpAction(rootScope.getId()));
	}

	@Test
	public void shouldMoveDownItemAfterUndo() throws ActionNotFoundException {
		actionExecutionService.onUserActionExecutionRequest(new ScopeMoveUpAction(lastScope.getId()));

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
