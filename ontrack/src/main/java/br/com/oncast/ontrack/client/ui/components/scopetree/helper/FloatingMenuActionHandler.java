package br.com.oncast.ontrack.client.ui.components.scopetree.helper;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeInternalActionHandler;
import br.com.oncast.ontrack.shared.model.action.ModelAction;

public interface FloatingMenuActionHandler extends ScopeTreeInternalActionHandler {

	public abstract void onUserActionExecutionRequest(ModelAction action);

}
