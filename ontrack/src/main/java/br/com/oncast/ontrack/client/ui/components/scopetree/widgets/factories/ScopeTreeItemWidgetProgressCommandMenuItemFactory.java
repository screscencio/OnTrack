package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetProgressCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	private final ScopeTreeItemWidgetEditionHandler controller;

	public ScopeTreeItemWidgetProgressCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public SimpleCommandMenuItem createCustomItem(final String inputText) {
		return new SimpleCommandMenuItem(messages.markAs(inputText), inputText, new Command() {

			@Override
			public void execute() {
				controller.declareProgress(inputText);
			}
		});
	}

	@Override
	public SimpleCommandMenuItem createItem(final String itemText, final String progressToDeclare) {
		return new SimpleCommandMenuItem(itemText, progressToDeclare, new Command() {

			@Override
			public void execute() {
				controller.declareProgress(progressToDeclare);
			}
		});
	}

	@Override
	public boolean shouldPrioritizeCustomItem() {
		return false;
	}

	@Override
	public String getNoItemText() {
		return null;
	}
}
