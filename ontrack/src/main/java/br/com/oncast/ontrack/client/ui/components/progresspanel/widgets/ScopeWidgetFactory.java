package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeWidget> {

	private final ProgressPanelWidgetInteractionHandler releasePanelInteractionHandler;

	public ScopeWidgetFactory(final ProgressPanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
	}

	@Override
	public ScopeWidget createWidget(final Scope scope) {
		final ScopeWidget newScopeWidget = new ScopeWidget(scope, releasePanelInteractionHandler);
		return newScopeWidget;
	}
}
