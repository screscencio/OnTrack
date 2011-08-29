package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;

public class ReleaseWidgetFactory implements ModelWidgetFactory<Release, ReleaseWidget> {

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;

	public ReleaseWidgetFactory(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
	}

	@Override
	public ReleaseWidget createWidget(final Release release) {
		final ReleaseWidget widget = new ReleaseWidget(release, this, releasePanelInteractionHandler);
		widget.setContainerState(true);
		return widget;
	}
}
