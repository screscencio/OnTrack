package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomWidgetCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.ReleaseCommandMenuItem;
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
	public CommandMenuItem createItem(final String itemText, final String releaseToBind) {
		return new CustomWidgetCommandMenuItem(new ReleaseCommandMenuItem(itemText), releaseToBind, new Command() {
			@Override
			public void execute() {
				controller.bindRelease(releaseToBind);
			}
		}).setGrowAnimation(false);
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
