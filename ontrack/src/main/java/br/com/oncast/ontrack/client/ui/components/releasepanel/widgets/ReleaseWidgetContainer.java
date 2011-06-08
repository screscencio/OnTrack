package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.release.Release;

// IMPORTANT This class should only be used to bypass the GWT limitation in which a class cannot have more that one 'UiFactory' for the same type.
class ReleaseWidgetContainer extends VerticalModelWidgetContainer<Release, ReleaseWidget> {

	public ReleaseWidgetContainer(final ModelWidgetFactory<Release, ReleaseWidget> modelWidgetFactory, final ModelWidgetContainerListener listener) {
		super(modelWidgetFactory, listener);
	}
}
