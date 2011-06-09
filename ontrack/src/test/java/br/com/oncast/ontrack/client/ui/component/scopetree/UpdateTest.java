package br.com.oncast.ontrack.client.ui.component.scopetree;

import static org.junit.Assert.assertTrue;

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
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;

import com.octo.gwt.test.GwtTest;

public class UpdateTest extends GwtTest {

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
		projectScope.add(new Scope("3"));
		projectScope.add(new Scope("2"));

		return projectScope;
	}

	private Scope getModifiedRootScope() {
		final Scope projectScope = new Scope("Root");
		projectScope.add(new Scope("1"));
		projectScope.add(new Scope("2"));

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

	private ScopeTree getModifiedRootTree() {
		treeAfterManipulation = new ScopeTree();
		treeAfterManipulation.setScope(getModifiedRootScope());
		return treeAfterManipulation;
	}

	@Test
	public void shouldUpdateScopeWithNewValue() throws ActionNotFoundException {
		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeUpdateAction(firstScope, "3"));

		assertTrue(getModifiedScope().deepEquals(scope));
		assertTrue(getModifiedTree().deepEquals(tree));
	}

	@Test
	public void shouldUpdateRootScope() throws ActionNotFoundException {
		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeUpdateAction(rootScope, "Root"));

		assertTrue(getModifiedRootScope().deepEquals(scope));
		assertTrue(getModifiedRootTree().deepEquals(tree));
	}

	@Test
	public void shouldRollbackUpdatedScope() throws ActionNotFoundException {
		planningActionExecutionRequestHandler.onActionExecutionRequest(new ScopeUpdateAction(firstScope, "3"));

		assertTrue(getModifiedScope().deepEquals(scope));
		assertTrue(getModifiedTree().deepEquals(tree));

		planningActionExecutionRequestHandler.onActionUndoRequest();

		assertTrue(getUnmodifiedScope().deepEquals(scope));
		assertTrue(getUnmodifiedTree().deepEquals(tree));
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
