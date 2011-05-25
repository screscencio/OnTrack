package br.com.oncast.ontrack.client.ui.component.scopetree.sync;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeTreeActionFactoryImpl;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeTreeActionManager;
import br.com.oncast.ontrack.client.ui.component.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.ActionNotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertChildAction;

import com.octo.gwt.test.GwtTest;

public class InsertChildTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private ScopeTreeWidget tree;
	private ScopeTreeWidget treeAfterManipulation;
	private ScopeTreeActionManager scopeTreeWidgetActionManager;

	@Before
	public void setUp() {
		scope = getScope();

		final ScopeTreeWidgetInteractionHandler interactionHandler = mock(ScopeTreeWidgetInteractionHandler.class);
		tree = new ScopeTreeWidget(interactionHandler);
		treeAfterManipulation = new ScopeTreeWidget(interactionHandler);

		tree.add(new ScopeTreeItem(scope));

		scopeTreeWidgetActionManager = new ScopeTreeActionManager(new ScopeTreeActionFactoryImpl(tree));
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

	private ScopeTreeWidget getModifiedTreeForRootChild() {
		treeAfterManipulation.add(new ScopeTreeItem(getModifiedScopeForRootChild()));

		return treeAfterManipulation;
	}

	@Test
	public void shouldInsertChild() throws ActionNotFoundException {
		scopeTreeWidgetActionManager.execute(new ScopeInsertChildAction(firstScope));

		assertEquals(getModifiedScope(), scope);
		assertEquals(getModifiedTree(), tree);
	}

	@Test
	public void shouldInsertChildForRoot() throws ActionNotFoundException {
		scopeTreeWidgetActionManager.execute(new ScopeInsertChildAction(rootScope));

		assertEquals(getModifiedScopeForRootChild(), scope);
		assertEquals(getModifiedTreeForRootChild(), tree);
	}

	@Test
	public void shouldRemoveInsertedChildAfterUndo() throws ActionNotFoundException {
		scopeTreeWidgetActionManager.execute(new ScopeInsertChildAction(firstScope));

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
