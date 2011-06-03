package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.scope.Scope;

public class ScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeWidget> {

	private static ScopeWidgetFactory instance;

	public static ScopeWidgetFactory getInstance() {
		if (instance != null) return instance;
		return instance = new ScopeWidgetFactory();
	}

	private ScopeWidgetFactory() {}

	@Override
	public ScopeWidget createWidget(final Scope scope) {
		return new ScopeWidget(scope);
	}
}
