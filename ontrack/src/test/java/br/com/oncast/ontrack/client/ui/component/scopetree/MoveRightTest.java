package br.com.oncast.ontrack.client.ui.component.scopetree;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.mocks.ContextProviderServiceMock;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveRightAction;

import com.octo.gwt.test.GwtTest;

public class MoveRightTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private Scope secondScope;
	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private ActionExecutionService actionExecutionService;

	@Before
	public void setUp() {
		scope = getScope();
		tree = new ScopeTree();
		tree.setScope(scope);

		projectContext = new ProjectContext((new Project(scope, new Release(""))));
		final ContextProviderService contextService = new ContextProviderServiceMock(projectContext);
		actionExecutionService = new ActionExecutionService(contextService);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() {
		rootScope = new Scope("Project");
		firstScope = new Scope("1");
		secondScope = new Scope("2");

		rootScope.add(firstScope);
		rootScope.add(secondScope);
		secondScope.add(new Scope("2.1"));

		return rootScope;
	}

	private Scope getModifiedScope() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("1").add(new Scope("2").add(new Scope("2.1"))));

		return projectScope;
	}

	private Scope getUnmodifiedScope() {
		final Scope unmodifiedScope = new Scope("Project");
		unmodifiedScope.add(new Scope("1"));
		unmodifiedScope.add(new Scope("2").add(new Scope("2.1")));

		return unmodifiedScope;
	}

	private ScopeTree getUnmodifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setScope(getUnmodifiedScope());
		return treeAfterManipulation;
	}

	private ScopeTree getModifiedTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setScope(getModifiedScope());
		return treeAfterManipulation;
	}

	@Test
	public void shouldMoveRight() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeMoveRightAction(secondScope));

		assertTrue(getModifiedScope().deepEquals(scope));
		assertTrue(getModifiedTree().deepEquals(tree));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveRightFirst() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeMoveRightAction(firstScope));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveRoot() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeMoveRightAction(rootScope));
	}

	@Test
	public void shouldMoveLeftAfterUndo() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeMoveRightAction(secondScope));

		assertTrue(getModifiedScope().deepEquals(scope));
		assertTrue(getModifiedTree().deepEquals(tree));

		actionExecutionService.onActionUndoRequest();

		assertTrue(getUnmodifiedScope().deepEquals(scope));
		assertTrue(getUnmodifiedTree().deepEquals(tree));
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
