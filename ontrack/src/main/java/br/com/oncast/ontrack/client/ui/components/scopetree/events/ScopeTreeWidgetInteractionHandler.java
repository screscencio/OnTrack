package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.OneStepInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.TwoStepInternalAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public interface ScopeTreeWidgetInteractionHandler extends
		ScopeTreeItemEditionStartEventHandler,
		ScopeTreeItemEditionEndEventHandler,
		ScopeTreeItemEditionCancelEventHandler,
		ScopeTreeItemBindReleaseEventHandler,
		ScopeTreeItemDeclareProgressEventHandler,
		ScopeTreeItemDeclareEffortEventHandler,
		ScopeTreeItemDeclareValueEventHandler {

	Scope getSelectedScope();

	void onInternalAction(OneStepInternalAction action);

	void onInternalAction(TwoStepInternalAction action);

	void onUserActionExecutionRequest(ScopeAction scopeMoveUpAction);

	ProjectContext getProjectContext();

	void assureConfigured();
}
