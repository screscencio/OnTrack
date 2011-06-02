package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.release.Release;

public class ReleaseWidgetFactoryImpl implements ReleaseWidgetFactory {

	@Override
	public ReleaseWidget createReleaseWidget(final Release release) {
		return new ReleaseWidget(release, this);
	}

}
