package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveRightScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionFactoryImpl;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionManager;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.event.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.octo.gwt.test.GwtTest;

public class MoveRightScopeTreeWidgetTest extends GwtTest {

	private Scope scope;
	private Scope rootScope;
	private Scope firstScope;
	private Scope secondScope;
	private ScopeTreeWidget tree;
	private ScopeTreeWidget modifedTree;

	@Before
	public void setUp() {
		scope = getScope();

		final ScopeTreeWidgetInteractionHandler interactionHandler = mock(ScopeTreeWidgetInteractionHandler.class);
		tree = new ScopeTreeWidget(interactionHandler);
		modifedTree = new ScopeTreeWidget(interactionHandler);
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

	private ScopeTreeWidget getModifiedTree() {
		modifedTree.add(new ScopeTreeItem(getModifiedScope()));

		return modifedTree;
	}

	@Test
	public void shouldMoveRightScope() throws NotFoundException {
		tree.add(new ScopeTreeItem(scope));

		new ScopeTreeWidgetActionManager(new ScopeTreeWidgetActionFactoryImpl(tree)).execute(new MoveRightScopeAction(secondScope));

		assertEquals(getModifiedScope(), scope);
		assertEquals(getModifiedTree(), tree);
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveRightFirstScope() throws NotFoundException {
		tree.add(new ScopeTreeItem(scope));

		new ScopeTreeWidgetActionManager(new ScopeTreeWidgetActionFactoryImpl(tree)).execute(new MoveRightScopeAction(firstScope));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotMoveRootScope() throws NotFoundException {
		tree.add(new ScopeTreeItem(scope));

		new ScopeTreeWidgetActionManager(new ScopeTreeWidgetActionFactoryImpl(tree)).execute(new MoveRightScopeAction(rootScope));
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
