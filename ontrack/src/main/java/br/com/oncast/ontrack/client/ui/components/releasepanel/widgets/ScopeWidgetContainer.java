package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.scope.Scope;

// IMPORTANT This class should only be used to bypass the GWT limitation in which a class cannot have more that one 'UiFactory' for the same type.
class ScopeWidgetContainer extends VerticalModelWidgetContainer<Scope, ScopeWidget> {

	public ScopeWidgetContainer(final ModelWidgetFactory<Scope, ScopeWidget> modelWidgetFactory, final ModelWidgetContainerListener listener) {
		super(modelWidgetFactory, listener);
	}
}
