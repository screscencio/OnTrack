package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionFactory;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionManager;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.event.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.octo.gwt.test.GwtTest;

public class ScopeTreeWidgetTest extends GwtTest {

	private Scope scope;
	private Scope scopeToBeMoved;
	private ScopeTreeWidget tree;
	private ScopeTreeWidget modifedTree;
	private ScopeTreeWidgetInteractionHandler interactionHandler;

	@Before
	public void setUp() {
		scope = getScope();

		interactionHandler = new ScopeTreeWidgetInteractionHandler() {
			@Override
			public void onItemUpdate(final ScopeTreeItem item, final String newContent) {}

			@Override
			public void onKeyUp(final KeyUpEvent event) {}
		};

		tree = new ScopeTreeWidget(interactionHandler);
		modifedTree = new ScopeTreeWidget(interactionHandler);
	}

	private Scope getScope() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("111"));
		projectScope.add(new Scope("222"));

		scopeToBeMoved = new Scope("333");

		projectScope.add(scopeToBeMoved);
		projectScope.add(new Scope("444"));

		return projectScope;
	}

	private Scope getModifiedScope() {
		final Scope projectScope = new Scope("Project");
		projectScope.add(new Scope("111"));
		projectScope.add(new Scope("333"));
		projectScope.add(new Scope("222"));
		projectScope.add(new Scope("444"));

		return projectScope;
	}

	private ScopeTreeWidget getModifiedTree() {
		modifedTree.add(new ScopeTreeItem(getModifiedScope()));

		return modifedTree;
	}

	@Test
	public void scopeShouldBeMovedUp() throws NotFoundException {
		tree.add(new ScopeTreeItem(scope));

		new ScopeTreeWidgetActionManager(new ScopeTreeWidgetActionFactory(tree)).execute(new MoveUpScopeAction(scopeToBeMoved));

		assertEquals(getModifiedScope(), scope);
		assertEquals(getModifiedTree(), tree);
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
