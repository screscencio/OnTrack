package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InternalActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

import com.google.gwt.event.dom.client.KeyUpEvent;

public final class ScopeTreeInteractionHandler implements ScopeTreeWidgetInteractionHandler {

	private class InternalActionHandler implements InternalActionExecutionRequestHandler {

		private InternalAction pendingAction = null;

		@Override
		public void onInternalActionExecutionRequest(final InternalAction internalAction) {
			this.pendingAction = internalAction;

			try {
				internalAction.execute(tree);
			}
			catch (final UnableToCompleteActionException e) {
				this.pendingAction = null;
				// TODO Implement an adequate exception treatment.
				// TODO Display error to the user
				throw new RuntimeException();
			}
		}

		public boolean hasPendingAction() {
			return pendingAction != null;
		}

		public void rollbackPendingAction() {
			try {
				pendingAction.rollback();
			}
			catch (final UnableToCompleteActionException e) {
				// TODO Implement an adequate exception treatment.
				throw new RuntimeException();
			}
			finally {
				this.pendingAction = null;
			}
		}

		public ModelAction getPendingActionEquivalentModelActionFor(final String value) {
			return pendingAction.createEquivalentModelAction(value);
		}
	}

	private final InternalActionHandler internalActionHandler = new InternalActionHandler();
	private ActionExecutionRequestHandler applicationActionHandler;
	private ScopeTreeWidget tree;

	@Override
	public void onKeyUp(final KeyUpEvent event) {
		assureConfigured();

		final ScopeTreeItem selected = tree.getSelected();
		if (selected == null) return;

		ScopeTreeShortcutMappings.interpretKeyboardCommand(applicationActionHandler, internalActionHandler, event.getNativeKeyCode(), event.isControlKeyDown(),
				event.isShiftKeyDown(), event.isAltKeyDown(), selected.getReferencedScope());
	}

	@Override
	public void onItemUpdateRequest(final ScopeTreeItem item, final String value) {
		assureConfigured();

		if (internalActionHandler.hasPendingAction()) {
			final ModelAction action = internalActionHandler.getPendingActionEquivalentModelActionFor(value);
			internalActionHandler.rollbackPendingAction();
			applicationActionHandler.onActionExecutionRequest(action);
		}
		else applicationActionHandler.onActionExecutionRequest(new ScopeUpdateAction(item.getReferencedScope(), value));
	}

	@Override
	public void onItemEditCancelation() {
		assureConfigured();

		if (!internalActionHandler.hasPendingAction()) return;
		internalActionHandler.rollbackPendingAction();
	}

	private void assureConfigured() {
		if (applicationActionHandler == null || tree == null) throw new RuntimeException("This class was not yet configured.");
	}

	public void configure(final ScopeTreeWidget tree, final ActionExecutionRequestHandler actionHandler) {
		this.tree = tree;
		this.applicationActionHandler = actionHandler;
	}
}
