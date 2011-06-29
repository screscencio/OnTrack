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
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertChildAction;

import com.octo.gwt.test.GwtTest;

public class InsertChildTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private String newScopeDescription;

	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private ActionExecutionService actionExecutionService;

	@Before
	public void setUp() {
		scope = getScope();

		tree = new ScopeTree();
		tree.setScope(scope);

		newScopeDescription = "description for new scope";
		projectContext = new ProjectContext((new Project(scope, new Release(""))));

		final ContextProviderService contextService = new ContextProviderServiceMock(projectContext);

		actionExecutionService = new ActionExecutionService(contextService);
		actionExecutionService.addActionExecutionListener(tree.getActionExecutionListener());
	}

	private Scope getScope() {
		rootScope = new Scope("Project");
		firstScope = new Scope("1");
		rootScope.add(firstScope);
		rootScope.add(new Scope("2"));

		return rootScope;
	}

	private Scope getModifiedScope() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("1").add(new Scope(newScopeDescription)));
		projectScope.add(new Scope("2"));

		return projectScope;
	}

	private Scope getModifiedScopeForRootChild() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("1"));
		projectScope.add(new Scope("2"));
		projectScope.add(new Scope(newScopeDescription));

		return projectScope;
	}

	private Scope getUnmodifiedScope() {
		final Scope unmodifiedScope = new Scope("Project");
		unmodifiedScope.add(new Scope("1"));
		unmodifiedScope.add(new Scope("2"));

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

	private ScopeTree getModifiedTreeForRootChild() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setScope(getModifiedScopeForRootChild());
		return treeAfterManipulation;
	}

	@Test
	public void shouldInsertChild() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeInsertChildAction(firstScope.getId(), newScopeDescription));

		assertTrue(getModifiedScope().deepEquals(scope));
		assertTrue(getModifiedTree().deepEquals(tree));
	}

	@Test
	public void shouldInsertChildForRoot() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeInsertChildAction(rootScope.getId(), newScopeDescription));

		assertTrue(getModifiedScopeForRootChild().deepEquals(scope));
		assertTrue(getModifiedTreeForRootChild().deepEquals(tree));
	}

	@Test
	public void shouldRemoveInsertedChildAfterUndo() throws ActionNotFoundException {
		actionExecutionService.onActionExecutionRequest(new ScopeInsertChildAction(firstScope.getId(), newScopeDescription));

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
