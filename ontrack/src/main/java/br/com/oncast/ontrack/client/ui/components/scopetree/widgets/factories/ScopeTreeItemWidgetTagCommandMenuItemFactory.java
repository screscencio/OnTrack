package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ColorSelectionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.TagCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.TagMenuItem;
import br.com.oncast.ontrack.shared.model.action.ScopeAddTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.TagAction;
import br.com.oncast.ontrack.shared.model.action.TagCreateAction;
import br.com.oncast.ontrack.shared.model.action.TagUpdateAction;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.tag.exception.TagNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

public class ScopeTreeItemWidgetTagCommandMenuItemFactory implements ScopeTreeItemWidgetCommandMenuItemFactory {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private final ScopeTreeItemWidgetEditionHandler controller;

	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	private ColorPack cachedColors = SERVICE_PROVIDER.colorProvider().pickColorPack();

	public ScopeTreeItemWidgetTagCommandMenuItemFactory(final ScopeTreeItemWidgetEditionHandler controller) {
		this.controller = controller;
	}

	@Override
	public TagCommandMenuItem createCustomItem(final String inputText) {
		final Tag tag = createTag(inputText);
		return new TagCommandMenuItem(new TagMenuItem(tag, new ColorSelectionListener() {

			@Override
			public void onColorPackSelect(final ColorPack colorPack) {
				tag.setColorPack(colorPack);
			}
		}), inputText, new Command() {
			@Override
			public void execute() {
				launchAction(new TagCreateAction(controller.getScope().getId(), inputText, tag.getColorPack().getForeground(), tag.getColorPack().getBackground()));
				cachedColors = SERVICE_PROVIDER.colorProvider().pickColorPack();
			}
		}).setGrowAnimation(false);
	}

	@Override
	public TagCommandMenuItem createItem(final String itemText, final String valueToDeclare) {
		final Tag tag = getTagForDescription(itemText);
		return new TagCommandMenuItem(new TagMenuItem(tag, new ColorSelectionListener() {

			@Override
			public void onColorPackSelect(final ColorPack colorPack) {
				tag.setColorPack(colorPack);
				launchAction(new TagUpdateAction(tag.getId(), valueToDeclare, tag.getColorPack().getForeground(), tag.getColorPack().getBackground()));
			}
		}), valueToDeclare, new Command() {

			@Override
			public void execute() {
				final Scope scope = controller.getScope();

				if (getContext().getTagsFor(scope).contains(tag)) {
					launchAction(new ScopeRemoveTagAssociationAction(scope.getId(), tag.getId()));
				} else {
					launchAction(new ScopeAddTagAssociationAction(scope.getId(), tag.getId()));
				}
			}

		});
	}

	private Tag getTagForDescription(final String itemText) {
		try {
			return getContext().findTag(itemText);
		} catch (final TagNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private Tag createTag(final String inputText) {
		return new Tag(new UUID(), messages.create(inputText), cachedColors);
	}

	private void launchAction(final TagAction action) {
		SERVICE_PROVIDER.actionExecution().onUserActionExecutionRequest(action);
	}

	@Override
	public String getNoItemText() {
		return null;
	}

	@Override
	public boolean shouldPrioritizeCustomItem() {
		return false;
	}

	private ProjectContext getContext() {
		return SERVICE_PROVIDER.contextProvider().getCurrent();
	}

}
