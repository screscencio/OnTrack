package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InternalActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.NodeEditionInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.OneStepInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.TwoStepInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.OperationNotAllowedException;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.client.utils.jquery.Event;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public final class ScopeTreeInteractionHandler implements ScopeTreeWidgetInteractionHandler {

	private class InternalActionHandler implements InternalActionExecutionRequestHandler {

		private TwoStepInternalAction pendingAction = null;

		@Override
		public void handle(final TwoStepInternalAction internalAction) {
			this.pendingAction = internalAction;
			try {
				execute(internalAction);
			}
			catch (final RuntimeException e) {
				this.pendingAction = null;
				throw e;
			}
		}

		@Override
		public void handle(final OneStepInternalAction internalAction) {
			execute(internalAction);
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

		private void execute(final InternalAction internalAction) {
			try {
				internalAction.execute(tree);
			}
			catch (final OperationNotAllowedException e) {
				ClientServiceProvider.getInstance().getErrorTreatmentService().treatUserWarning(e.getMessage(), e);
			}
			catch (final UnableToCompleteActionException e) {
				// TODO ++Implement an adequate exception treatment.
				// TODO ++Display error to the user
				throw new RuntimeException(e);
			}
		}
	}

	private final InternalActionHandler internalActionHandler = new InternalActionHandler();
	private ActionExecutionRequestHandler applicationActionHandler;
	private ScopeTreeWidget tree;
	private ProjectContext context;

	@Override
	public void handle(final Event e) {
		// FIXME: When triggered by an ENTER keydown event, the following ENTER keyup event must also be suppressed.

		assureConfigured();

		final ScopeTreeItem selected = tree.getSelected();
		if (selected == null) return;

		ScopeTreeShortcutMappings.interpretKeyboardCommand(applicationActionHandler, internalActionHandler, e.which(), e.shiftKey(),
				e.ctrlKey(), e.altKey(), e.metaKey(), selected.getReferencedScope(), context);

		e.stopPropagation();
		e.preventDefault();
	}

	@Override
	public void onItemEditionStart(final ScopeTreeItem item) {
		internalActionHandler.handle(new NodeEditionInternalAction(item.getReferencedScope()));
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
		applicationActionHandler.onUserActionExecutionRequest(new ScopeBindReleaseAction(scopeId, releaseDescription));
	}

	@Override
	public void onDeclareProgressRequest(final UUID scopeId, final String progressDescription) {
		applicationActionHandler.onUserActionExecutionRequest(new ScopeDeclareProgressAction(scopeId, progressDescription));
	}

	@Override
	public void onDeclareEffortRequest(final UUID scopeId, final String effortDescription) {
		float declaredEffort;
		boolean hasDeclaredEffort;

		try {
			declaredEffort = Float.valueOf(effortDescription);
			hasDeclaredEffort = (effortDescription != null && !effortDescription.isEmpty());
		}
		catch (final NumberFormatException e) {
			declaredEffort = 0;
			hasDeclaredEffort = false;
		}

		applicationActionHandler.onUserActionExecutionRequest(new ScopeDeclareEffortAction(scopeId, hasDeclaredEffort, declaredEffort));
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
