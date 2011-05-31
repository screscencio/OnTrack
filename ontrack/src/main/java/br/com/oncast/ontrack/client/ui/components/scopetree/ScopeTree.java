package br.com.oncast.ontrack.client.ui.components.scopetree;

import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_F2;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements IsWidget {

	private final ScopeTreeWidget tree;
	private final ScopeTreeActionFactory treeActionFactory;
	private final ActionExecutionListener actionExecutionListener;
	private ActionExecutionRequestHandler actionHandler;

	public ScopeTree() {
		tree = new ScopeTreeWidget(new ScopeTreeWidgetInteractionHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (actionHandler == null) return;

				final ScopeTreeItem selected = tree.getSelected();
				if (selected == null) return;

				ScopeTreeShortcutMappings.interpretKeyboardCommand(event.getNativeKeyCode(), event.isControlKeyDown(), event.isShiftKeyDown(), actionHandler,
						selected.getReferencedScope());

				if (event.getNativeKeyCode() == KEY_F2) {
					tree.getSelected().enterEditMode();
					tree.setSelected(null);
				}
			}

			@Override
			public void onItemUpdate(final ScopeTreeItem item, final String pattern) {
				if (actionHandler == null) return;
				actionHandler.onActionExecutionRequest(new ScopeUpdateAction(item.getReferencedScope(), pattern));
			}
		});
		actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ScopeAction action, final boolean wasRollback) {
				try {
					final ScopeTreeAction scopeTreeAction = treeActionFactory.createEquivalentActionFor(action);
					if (!wasRollback) scopeTreeAction.execute();
					else scopeTreeAction.rollback();
				}
				catch (final ScopeNotFoundException e) {
					// TODO Redraw the entire structure to eliminate inconsistencies
					throw new RuntimeException("It was not possible to update the view because an inconsistency with the model was detected.", e);
				}
			}
		};
		treeActionFactory = new ScopeTreeActionFactory(tree);
	}

	public ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener;
	}

	public void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	public void setScope(final Scope scope) {
		tree.clear();
		final ScopeTreeItem rootItem = new ScopeTreeItem(scope);

		tree.add(rootItem);
		rootItem.setState(true);
		tree.setSelected(rootItem);
	}

	@Override
	public Widget asWidget() {
		return tree;
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ScopeTree)) return false;

		final ScopeTree otherTree = (ScopeTree) other;

		return tree.equals(otherTree.asWidget());
	}
}
