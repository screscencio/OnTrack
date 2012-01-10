package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;

import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetValueCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private final ScopeTreeItemWidgetEditionHandler controller;

	public ScopeTreeItemWidgetValueCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public CommandMenuItem createCustomItem(final String inputText) {
		return new CommandMenuItem("Use '" + inputText + "'", inputText, new Command() {

			@Override
			public void execute() {
				controller.declareEffort(inputText);
			}
		});
	}

	@Override
	public CommandMenuItem createItem(final String itemText, final String effortToDeclare) {
		return new CommandMenuItem(itemText, effortToDeclare, new Command() {

			@Override
			public void execute() {
				controller.declareValue(effortToDeclare);
			}
		});
	}
}
