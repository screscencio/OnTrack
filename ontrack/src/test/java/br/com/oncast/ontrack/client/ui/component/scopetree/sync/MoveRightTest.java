package br.com.oncast.ontrack.client.ui.component.scopetree.sync;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionManager;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeMoveRightAction;

import com.octo.gwt.test.GwtTest;

public class MoveRightTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private Scope secondScope;
	private ScopeTreeWidget tree;
	private ScopeTreeWidget treeAfterManipulation;
	private ActionManager scopeTreeWidgetActionManager;

	@Before
	public void setUp() {
		scope = getScope();

		final ScopeTreeWidgetInteractionHandler interactionHandler = mock(ScopeTreeWidgetInteractionHandler.class);
		tree = new ScopeTreeWidget(interactionHandler);
		treeAfterManipulation = new ScopeTreeWidget(interactionHandler);

		tree.add(new ScopeTreeItem(scope));

		scopeTreeWidgetActionManager = new ActionManager(new ScopeTreeActionFactory(tree));
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

	private ScopeTreeWidget getUnmodifiedTree() {
		treeAfterManipulation.clear();
		treeAfterManipulation.add(new ScopeTreeItem(getUnmodifiedScope()));

		return treeAfterManipulation;
	}

	private ScopeTreeWidget getModifiedTree() {
		treeAfterManipulation.clear();
		treeAfterManipulation.add(new ScopeTreeItem(getModifiedScope()));

		return treeAfterManipulation;
	}

	@Test
	public void shouldMoveRight() throws ActionNotFoundException {
		scopeTreeWidgetActionManager.execute(new ScopeMoveRightAction(secondScope));

		assertEquals(getModifiedScope(), scope);
		assertEquals(getModifiedTree(), tree);
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveRightFirst() throws ActionNotFoundException {
		scopeTreeWidgetActionManager.execute(new ScopeMoveRightAction(firstScope));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveRoot() throws ActionNotFoundException {
		scopeTreeWidgetActionManager.execute(new ScopeMoveRightAction(rootScope));
	}

	@Test
	public void shouldMoveLeftAfterUndo() throws ActionNotFoundException {
		scopeTreeWidgetActionManager.execute(new ScopeMoveRightAction(secondScope));

		assertEquals(getModifiedScope(), scope);
		assertEquals(getModifiedTree(), tree);

		scopeTreeWidgetActionManager.undo();

		assertEquals(getUnmodifiedScope(), scope);
		assertEquals(getUnmodifiedTree(), tree);
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
