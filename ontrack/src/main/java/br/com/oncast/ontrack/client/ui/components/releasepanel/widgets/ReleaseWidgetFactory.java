package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleaseWidgetFactory implements ModelWidgetFactory<Release, ReleaseWidget> {

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;
	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	public ReleaseWidgetFactory(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler,
			final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory) {
		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
		this.scopeWidgetFactory = scopeWidgetFactory;
	}

	@Override
	public ReleaseWidget createWidget(final Release release) {
		final ReleaseWidget widget = new ReleaseWidget(release, this, scopeWidgetFactory, releasePanelInteractionHandler);
		widget.setContainerState(true);
		return widget;
	}
}
