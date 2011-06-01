package br.com.oncast.ontrack.client.ui.component.scopetree;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertChildAction;

import com.octo.gwt.test.GwtTest;

public class InsertChildTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private ScopeTree tree;
	private ScopeTree treeAfterManipulation;
	private ProjectContext projectContext;
	private PlanningActionExecutionRequestHandler planningActionExecutionRequestHandler;

	@Before
	public void setUp() {
		scope = getScope();

		tree = new ScopeTree();
		tree.setScope(scope);

		projectContext = new ProjectContext((new Project(scope, new Release(""))));

		final List<ActionExecutionListener> listeners = new ArrayList<ActionExecutionListener>();
		listeners.add(tree.getActionExecutionListener());
		planningActionExecutionRequestHandler = new PlanningActionExecutionRequestHandler(projectContext, listeners);
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
		projectScope.add(new Scope("1").add(new Scope("")));
		projectScope.add(new Scope("2"));

		return projectScope;
	}

	private Scope getModifiedScopeForRootChild() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("1"));
		projectScope.add(new Scope("2"));
		projectScope.add(new Scope(""));

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
		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeInsertChildAction(firstScope));

		assertEquals(getModifiedScope(), scope);
		assertEquals(getModifiedTree(), tree);
	}

	@Test
	public void shouldInsertChildForRoot() throws ActionNotFoundException {
		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeInsertChildAction(rootScope));

		assertEquals(getModifiedScopeForRootChild(), scope);
		assertEquals(getModifiedTreeForRootChild(), tree);
	}

	@Test
	public void shouldRemoveInsertedChildAfterUndo() throws ActionNotFoundException {
		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeInsertChildAction(firstScope));

		assertEquals(getModifiedScope(), scope);
		assertEquals(getModifiedTree(), tree);

		planningActionExecutionRequestHandler.onActionUndoRequest();

		assertEquals(getUnmodifiedScope(), scope);
		assertEquals(getUnmodifiedTree(), tree);
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}
}
