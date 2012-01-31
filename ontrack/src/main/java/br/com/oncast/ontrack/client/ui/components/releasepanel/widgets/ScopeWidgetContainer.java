package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

// IMPORTANT This class should only be used to bypass the GWT limitation in which a class cannot have more that one 'UiFactory' for the same type.
public class ScopeWidgetContainer extends VerticalModelWidgetContainer<Scope, ScopeWidget> {

	private Release release;

	public ScopeWidgetContainer(final ModelWidgetFactory<Scope, ScopeWidget> modelWidgetFactory, final ModelWidgetContainerListener listener) {
		super(modelWidgetFactory, listener);
	}

	public void setOwnerRelease(final Release release) {
		this.release = release;
	}

	public Release getOwnerRelease() {
		return this.release;
	}
}
