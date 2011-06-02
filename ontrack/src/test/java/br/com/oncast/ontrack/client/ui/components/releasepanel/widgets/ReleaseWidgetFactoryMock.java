package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidgetFactory;
import br.com.oncast.ontrack.shared.release.Release;

public class ReleaseWidgetFactoryMock implements ReleaseWidgetFactory {
	@Override
	public ReleaseWidget createReleaseWidget(final Release release) {
		return new ReleaseWidgetMock(release, this);
	}
}