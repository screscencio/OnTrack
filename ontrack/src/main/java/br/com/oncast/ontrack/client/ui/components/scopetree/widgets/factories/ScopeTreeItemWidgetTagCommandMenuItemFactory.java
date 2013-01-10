package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
<<<<<<< HEAD
import br.com.oncast.ontrack.client.ui.generalwidgets.TagCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.TagWidget;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
=======
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
>>>>>>> Underwork

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetTagCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private final ScopeTreeItemWidgetEditionHandler controller;
	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	public ScopeTreeItemWidgetTagCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public TagCommandMenuItem createCustomItem(final String inputText) {
		// FIXME LOBO bUSCAR OBJETO TAG PELA DESCRICAO
		// FIXME I18N MESSAGES
		return new TagCommandMenuItem(new TagWidget(new Tag(new UUID(), messages.create(inputText))), inputText, new Command() {

			@Override
			public void execute() {
				// controller.addTag(inputText);
			}
		});
	}

	@Override
	public TagCommandMenuItem createItem(final String itemText, final String valueToDeclare) {
		// FIXME LOBO bUSCAR OBJETO TAG PELA DESCRICAO
		return new TagCommandMenuItem(new TagWidget(new Tag(new UUID(), valueToDeclare)), valueToDeclare, new Command() {

			@Override
			public void execute() {
				// controller.addTag(valueToDeclare);
			}
		});
	}

	// FIXME LOBO
	@Override
	public String getNoItemText() {
		return null;
	}
}
