package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.ColorSelectionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableColorPackWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PaddedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.TagUpdateAction;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

;

public class TagAssociationWidgetEditMenu extends Composite implements PopupAware, HasCloseHandlers<TagAssociationWidgetEditMenu> {

	private static TagAssociationWidgetEditMenuUiBinder uiBinder = GWT.create(TagAssociationWidgetEditMenuUiBinder.class);

	interface TagAssociationWidgetEditMenuUiBinder extends UiBinder<Widget, TagAssociationWidgetEditMenu> {}

	public TagAssociationWidgetEditMenu() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	PaddedTextBox description;

	@UiField(provided = true)
	EditableColorPackWidget color;

	private TagAssociationMetadata association;

	public TagAssociationWidgetEditMenu(final TagAssociationMetadata association) {
		this.association = association;
		final Tag tag = association.getTag();
		final ColorPack colorPack = tag.getColorPack();
		color = new EditableColorPackWidget(colorPack, new ColorSelectionListener() {
			@Override
			public void onColorPackSelect(final ColorPack colorPack) {
				launchAction(new TagUpdateAction(getTagId(), tag.getDescription(), colorPack));
				show();
			}
		});
		initWidget(uiBinder.createAndBindUi(this));
		description.setText(tag.getDescription());
	}

	@UiHandler("description")
	protected void onKeyDown(final KeyDownEvent event) {
		event.stopPropagation();

		final int key = event.getNativeKeyCode();
		if (key == BrowserKeyCodes.KEY_ESCAPE) {
			description.setText("");
			hide();
		}
		else if (key == BrowserKeyCodes.KEY_ENTER) hide();
	}

	@UiHandler("remove")
	protected void onRemoveClick(final ClickEvent event) {
		launchAction(new ScopeRemoveTagAssociationAction(association.getSubject().getId(), getTagId()));
		hide();
	}

	private UUID getTagId() {
		return association.getTag().getId();
	}

	private void launchAction(final ModelAction action) {
		ClientServiceProvider.getInstance().getActionExecutionService().onUserActionExecutionRequest(action);
	}

	@Override
	public void show() {
		description.selectAll();
		description.setFocus(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		final String text = description.getText().trim();

		if (!text.isEmpty() && !text.equalsIgnoreCase(association.getTag().getDescription())) {
			launchAction(new TagUpdateAction(getTagId(), text, null));
		}

		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<TagAssociationWidgetEditMenu> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

}
