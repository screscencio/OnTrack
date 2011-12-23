package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

public interface InternalActionExecutionRequestHandler {

	public void handle(TwoStepInternalAction internalAction);

	public void handle(OneStepInternalAction internalAction);
}
