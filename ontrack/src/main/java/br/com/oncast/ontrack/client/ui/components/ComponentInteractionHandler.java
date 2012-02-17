package br.com.oncast.ontrack.client.ui.components;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public interface ComponentInteractionHandler {

	void onScopeSelectionRequest(final Scope scope);

}
