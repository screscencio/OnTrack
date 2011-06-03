package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.release.Release;

public class ReleaseWidgetFactoryMock implements ModelWidgetFactory<Release, ReleaseWidget> {

	@Override
	public ReleaseWidget createWidget(final Release release) {
		return new ReleaseWidgetMock(release, this);
	}
}