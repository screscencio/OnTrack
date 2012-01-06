package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;

import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetReleaseCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private final ScopeTreeItemWidgetEditionHandler controller;

	public ScopeTreeItemWidgetReleaseCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public CommandMenuItem createCustomItem(final String inputText) {
		return new CommandMenuItem("Create '" + inputText + "'", inputText, new Command() {

			@Override
			public void execute() {
				controller.bindRelease(inputText);
			}
		});
	}

	@Override
	public CommandMenuItem createItem(final String itemText, final String releaseToBind) {
		return new CommandMenuItem(itemText, releaseToBind, new Command() {

			@Override
			public void execute() {
				controller.bindRelease(releaseToBind);
			}
		});
	}
}
