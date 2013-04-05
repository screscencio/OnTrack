package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetValueCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	private final ScopeTreeItemWidgetEditionHandler controller;

	public ScopeTreeItemWidgetValueCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public SimpleCommandMenuItem createCustomItem(final String inputText) {
		return new SimpleCommandMenuItem(messages.use(inputText), inputText, new Command() {

			@Override
			public void execute() {
				controller.declareValue(inputText);
			}
		});
	}

	@Override
	public SimpleCommandMenuItem createItem(final String itemText, final String valueToDeclare) {
		return new SimpleCommandMenuItem(itemText, valueToDeclare, new Command() {

			@Override
			public void execute() {
				controller.declareValue(valueToDeclare);
			}
		});
	}

	@Override
	public String getNoItemText() {
		return null;
	}

	@Override
	public boolean shouldPrioritizeCustomItem() {
		return true;
	}

}
