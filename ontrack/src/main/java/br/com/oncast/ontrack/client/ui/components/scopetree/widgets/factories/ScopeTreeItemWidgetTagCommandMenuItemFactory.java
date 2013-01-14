package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.TagCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.TagWidget;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetTagCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private final ScopeTreeItemWidgetEditionHandler controller;
	private final Color bgColor = ClientServiceProvider.getInstance().getColorProviderService().pickColor();
	private final Color txColor = ClientServiceProvider.getInstance().getColorProviderService().pickColor();

	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	public ScopeTreeItemWidgetTagCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public TagCommandMenuItem createCustomItem(final String inputText) {
		// FIXME LOBO bUSCAR OBJETO TAG PELA DESCRICAO
		// FIXME I18N MESSAGES
		return new TagCommandMenuItem(new TagWidget(new Tag(new UUID(), messages.create(inputText), new ColorPack(txColor, bgColor))), inputText,
				new Command() {

					@Override
					public void execute() {
						// controller.addTag(inputText);
					}
				});
	}

	@Override
	public TagCommandMenuItem createItem(final String itemText, final String valueToDeclare) {
		// FIXME LOBO bUSCAR OBJETO TAG PELA DESCRICAO
		return new TagCommandMenuItem(new TagWidget(new Tag(new UUID(), valueToDeclare, new ColorPack(txColor, bgColor))), valueToDeclare, new Command() {

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
