package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.shared.scope.Scope;

public class ScopeContainer extends VerticalModelWidgetContainer<Scope, ScopeWidget> {

	public ScopeContainer(final ModelWidgetFactory<Scope, ScopeWidget> modelWidgetFactory, final ModelWidgetContainerListener listener) {
		super(modelWidgetFactory, listener);
	}
}
