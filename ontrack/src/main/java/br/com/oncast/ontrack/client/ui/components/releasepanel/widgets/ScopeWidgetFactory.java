package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeWidget> {

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;

	public ScopeWidgetFactory(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
	}

	@Override
	public ScopeWidget createWidget(final Scope scope) {
		return new ScopeWidget(scope, releasePanelInteractionHandler);
	}
}
