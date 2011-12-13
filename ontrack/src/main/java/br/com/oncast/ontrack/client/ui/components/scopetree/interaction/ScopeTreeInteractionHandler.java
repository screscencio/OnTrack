package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InternalActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.NodeEditionInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.OperationNotAllowedException;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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
			catch (final OperationNotAllowedException e) {
				ClientServiceProvider.getInstance().getErrorTreatmentService().treatUserWarning(e.getMessage(), e);
			}
			catch (final UnableToCompleteActionException e) {
				this.pendingAction = null;
				// TODO ++Implement an adequate exception treatment.
				// TODO ++Display error to the user
				throw new RuntimeException(e);
			}
		}

		public boolean hasPendingAction() {
			return pendingAction != null;
		}

		public void rollbackPendingAction() {
			try {
				pendingAction.rollback(tree);
			}
			catch (final UnableToCompleteActionException e) {
				// TODO ++Implement an adequate exception treatment.
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
	private ProjectContext context;

	@Override
	public void onKeyUp(final KeyUpEvent event) {
		assureConfigured();

		final ScopeTreeItem selected = tree.getSelected();
		if (selected == null) return;

		ScopeTreeShortcutMappings.interpretKeyboardCommand(applicationActionHandler, internalActionHandler, event.getNativeKeyCode(), event.isControlKeyDown(),
				event.isShiftKeyDown(), event.isAltKeyDown(), selected.getReferencedScope(), context);
	}

	@Override
	public void onItemEditionStart(final ScopeTreeItem item) {
		internalActionHandler.onInternalActionExecutionRequest(new NodeEditionInternalAction(item.getReferencedScope()));
	}

	@Override
	public void onItemEditionEnd(final ScopeTreeItem item, final String value) {
		assureConfigured();

		if (internalActionHandler.hasPendingAction()) {
			final ModelAction action = internalActionHandler.getPendingActionEquivalentModelActionFor(value);
			internalActionHandler.rollbackPendingAction();
			applicationActionHandler.onUserActionExecutionRequest(action);
		}
		else applicationActionHandler.onUserActionExecutionRequest(new ScopeUpdateAction(item.getReferencedScope().getId(), value));
	}

	@Override
	public void onItemEditionCancel() {
		assureConfigured();

		if (!internalActionHandler.hasPendingAction()) return;
		internalActionHandler.rollbackPendingAction();
	}

	@Override
	public void onBindReleaseRequest(final UUID scopeId, final String releaseDescription) {
		final ModelAction action = internalActionHandler.getPendingActionEquivalentModelActionFor(releaseDescription);
		internalActionHandler.rollbackPendingAction();
		applicationActionHandler.onUserActionExecutionRequest(action);
	}

	@Override
	public void onDeclareProgressRequest(final UUID scopeId, final String progressDescription) {
		final ModelAction action = internalActionHandler.getPendingActionEquivalentModelActionFor(progressDescription);
		internalActionHandler.rollbackPendingAction();
		applicationActionHandler.onUserActionExecutionRequest(action);
	}

	@Override
	public void onDeclareEffortRequest(final UUID scopeId, final String effortDescription) {
		final ModelAction action = internalActionHandler.getPendingActionEquivalentModelActionFor(effortDescription);
		internalActionHandler.rollbackPendingAction();
		applicationActionHandler.onUserActionExecutionRequest(action);
	}

	private void assureConfigured() {
		if (applicationActionHandler == null || tree == null || context == null) throw new RuntimeException("This class was not yet configured.");
	}

	public void configure(final ScopeTreeWidget tree, final ActionExecutionRequestHandler actionHandler) {
		this.tree = tree;
		this.applicationActionHandler = actionHandler;
	}

	public void setContext(final ProjectContext context) {
		this.context = context;
	}

}
