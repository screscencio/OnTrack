package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;

import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetReleaseCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private final ScopeTreeItemWidgetEditionHandler controller;

	public ScopeTreeItemWidgetReleaseCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public SimpleCommandMenuItem createCustomItem(final String inputText) {
		return new SimpleCommandMenuItem("Create '" + inputText + "'", inputText, new Command() {

			@Override
			public void execute() {
				controller.bindRelease(inputText);
			}
		});
	}

	@Override
	public SimpleCommandMenuItem createItem(final String itemText, final String releaseToBind) {
		return new SimpleCommandMenuItem(itemText, releaseToBind, new Command() {

			@Override
			public void execute() {
				controller.bindRelease(releaseToBind);
			}
		});
	}
}
