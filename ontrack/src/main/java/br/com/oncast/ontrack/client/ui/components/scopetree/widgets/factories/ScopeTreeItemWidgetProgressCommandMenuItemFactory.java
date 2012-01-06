package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;

import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetProgressCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private final ScopeTreeItemWidgetEditionHandler controller;

	public ScopeTreeItemWidgetProgressCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public CommandMenuItem createCustomItem(final String inputText) {
		return new CommandMenuItem("Mark as '" + inputText + "'", inputText, new Command() {

			@Override
			public void execute() {
				controller.declareProgress(inputText);
			}
		});
	}

	@Override
	public CommandMenuItem createItem(final String itemText, final String progressToDeclare) {
		return new CommandMenuItem(itemText, progressToDeclare, new Command() {

			@Override
			public void execute() {
				controller.declareProgress(progressToDeclare);
			}
		});
	}
}
