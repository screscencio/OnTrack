package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.shared.release.Release;

public class ReleaseContainer extends VerticalModelWidgetContainer<Release, ReleaseWidget> {

	public ReleaseContainer(final ModelWidgetFactory<Release, ReleaseWidget> modelWidgetFactory, final ModelWidgetContainerListener listener) {
		super(modelWidgetFactory, listener);
	}
}
