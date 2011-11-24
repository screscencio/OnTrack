package br.com.oncast.ontrack.client.ui.component.scopetree;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.actions.ActionExecutionFactoryTestUtil;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

import com.octo.gwt.test.GwtTest;

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
		rootScope.add(new Scope("2"));
		thirdScope = new Scope("3");
		rootScope.add(thirdScope);
		lastScope = new Scope("4");
		rootScope.add(lastScope);

		return rootScope;
	}

	private ProjectContext getModifiedScope() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("2"));
		projectScope.add(new Scope("1"));
		projectScope.add(new Scope("4"));
		projectScope.add(new Scope("3"));

		return ProjectTestUtils.createProjectContext(projectScope, null);
	}

	private ProjectContext getUnmodifiedScope() {
		final Scope unmodifiedScope = new Scope("Project");
		unmodifiedScope.add(new Scope("1"));
		unmodifiedScope.add(new Scope("2"));
		unmodifiedScope.add(new Scope("3"));
		unmodifiedScope.add(new Scope("4"));

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

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
