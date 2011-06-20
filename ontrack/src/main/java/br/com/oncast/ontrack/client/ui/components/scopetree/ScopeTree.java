package br.com.oncast.ontrack.client.ui.components.scopetree;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.Component;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.InternalInsertionAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements Component, DeeplyComparable {

	private final ScopeTreeWidget tree;
	private final ScopeTreeActionFactory treeActionFactory;
	private final ActionExecutionListener actionExecutionListener;
	private ActionExecutionRequestHandler applicationActionHandler;

	public ScopeTree() {
		tree = new ScopeTreeWidget(new ScopeTreeWidgetInteractionHandler() {
			private InternalInsertionAction pendingInternalAction = null;

			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (applicationActionHandler == null) return;

				final ScopeTreeItem selected = tree.getSelected();
				if (selected == null) return;

				ScopeTreeShortcutMappings.interpretKeyboardCommand(applicationActionHandler, this, event.getNativeKeyCode(), event.isControlKeyDown(),
						event.isShiftKeyDown(), event.isAltKeyDown(), selected.getReferencedScope());
			}

			@Override
			public void onItemUpdateRequest(final ScopeTreeItem item, final String value) {
				if (applicationActionHandler == null) return;

				if (pendingInternalAction != null) {
					final ModelAction action = pendingInternalAction.createEquivalentModelAction(value);
					try {
						pendingInternalAction.rollback();
					}
					catch (final UnableToCompleteActionException e) {
						// TODO Implement an adequate exception treatment.
						throw new RuntimeException();
					}
					finally {
						this.pendingInternalAction = null;
					}
					applicationActionHandler.onActionExecutionRequest(action);
				}
				else applicationActionHandler.onActionExecutionRequest(new ScopeUpdateAction(item.getReferencedScope(), value));
			}

			@Override
			public void onItemEditCancelation() {
				if (pendingInternalAction == null) return;

				try {
					pendingInternalAction.rollback();
				}
				catch (final UnableToCompleteActionException e) {
					// TODO Implement an adequate exception treatment.
					throw new RuntimeException();
				}
				finally {
					this.pendingInternalAction = null;
				}
			}

			// FIXME Receive editionModeStatus inside the widget, and then create a method to consult for this info, so that actions are only done when not
			// editing

			// TODO Separate interfaces so that method responsibilities are not mixed.
			@Override
			public void onInternalActionExecutionRequest(final InternalInsertionAction internalAction) {
				this.pendingInternalAction = internalAction;

				try {
					internalAction.execute(tree.getSelected());
				}
				catch (final UnableToCompleteActionException e) {
					this.pendingInternalAction = null;
					// TODO Implement an adequate exception treatment.
					// TODO Display error to the user
					throw new RuntimeException();
				}
			}

			@Override
			public void onEditionModeRequest() {
				tree.getSelected().enterEditMode();
			}
		});
		actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final boolean wasRollback) {
				try {
					final ScopeTreeAction scopeTreeAction = treeActionFactory.createEquivalentActionFor(action);
					if (!wasRollback) scopeTreeAction.execute(context);
					else scopeTreeAction.rollback(context);
				}
				catch (final ScopeNotFoundException e) {
					// TODO Redraw the entire structure to eliminate inconsistencies
					throw new RuntimeException("It was not possible to update the view because an inconsistency with the model was detected.", e);
				}
			}
		};
		treeActionFactory = new ScopeTreeActionFactory(tree);
	}

	@Override
	public ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener;
	}

	@Override
	public void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler) {
		this.applicationActionHandler = actionHandler;
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
	public boolean deepEquals(final Object other) {
		if (!(other instanceof ScopeTree)) return false;
		return tree.deepEquals(((ScopeTree) other).asWidget());
	}
}
