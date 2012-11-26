package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public interface ScopeTreeWidgetInteractionHandler extends
		ScopeTreeItemEditionStartEventHandler,
		ScopeTreeItemEditionEndEventHandler,
		ScopeTreeItemEditionCancelEventHandler,
		ScopeTreeItemBindReleaseEventHandler,
		ScopeTreeItemDeclareProgressEventHandler,
		ScopeTreeItemDeclareEffortEventHandler,
		ScopeTreeItemDeclareValueEventHandler,
		ScopeTreeInternalActionHandler {

	Scope getSelectedScope();

	ProjectContext getProjectContext();

	void assureConfigured();

	void focusTree();

	void onUserActionExecutionRequest(ModelAction action);
}
