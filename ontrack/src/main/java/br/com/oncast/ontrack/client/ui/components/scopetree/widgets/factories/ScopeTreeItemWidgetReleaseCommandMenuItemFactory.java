package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetReleaseCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private final ScopeTreeItemWidgetEditionHandler controller;
	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	public ScopeTreeItemWidgetReleaseCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public SimpleCommandMenuItem createCustomItem(final String inputText) {
		return new SimpleCommandMenuItem(messages.create(inputText), inputText, new Command() {
			@Override
			public void execute() {
				controller.bindRelease(inputText);
			}
		}).setGrowAnimation(false);
	}

	@Override
	public SimpleCommandMenuItem createItem(final String itemText, final String releaseToBind) {
		return new SimpleCommandMenuItem(itemText, releaseToBind, new Command() {

			@Override
			public void execute() {
				controller.bindRelease(releaseToBind);
			}
		}).setRtl(true);
	}

	@Override
	public String getNoItemText() {
		return null;
	}

	@Override
	public boolean shouldPrioritizeCustomItem() {
		return false;
	}
}
