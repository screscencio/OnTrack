package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

public interface InternalActionRequestHandler {

	public void onInternalActionExecutionRequest(InternalInsertionAction internalAction);

	public void onEditionModeRequest();
}
