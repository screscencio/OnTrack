package br.com.oncast.ontrack.client.ui.components.releasepanel;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelItemWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidgetFactory;
import br.com.oncast.ontrack.shared.release.Release;

public class ReleaseWidgetFactoryMock implements ReleaseWidgetFactory {
	@Override
	public ReleasePanelItemWidget createReleaseWidget(final Release release) {
		return new ReleaseWidgetMock(release, this);
	}
}