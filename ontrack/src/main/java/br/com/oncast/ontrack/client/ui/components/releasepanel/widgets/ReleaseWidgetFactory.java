package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;

public class ReleaseWidgetFactory implements ModelWidgetFactory<Release, ReleaseWidget> {

	@Override
	public ReleaseWidget createWidget(final Release release) {
		final ReleaseWidget widget = new ReleaseWidget(release, this);
		widget.setContainerState(true);
		return widget;
	}
}
