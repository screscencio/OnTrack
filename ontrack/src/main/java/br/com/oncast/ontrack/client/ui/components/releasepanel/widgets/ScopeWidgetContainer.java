package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.components.scope.ScopeCardWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

// IMPORTANT This class should only be used to bypass the GWT limitation in which a class cannot have more that one 'UiFactory' for the same type.
public class ScopeWidgetContainer extends ModelWidgetContainer<Scope, ScopeCardWidget> {

	private Release release;

	public ScopeWidgetContainer(final ModelWidgetFactory<Scope, ScopeCardWidget> modelWidgetFactory) {
		super(modelWidgetFactory);
		this.addStyleName("scopeWidgetContainer");
	}

	public void setOwnerRelease(final Release release) {
		this.release = release;
	}

	public Release getOwnerRelease() {
		return this.release;
	}

}
