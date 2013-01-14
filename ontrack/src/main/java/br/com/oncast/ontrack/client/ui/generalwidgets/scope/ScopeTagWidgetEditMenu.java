package br.com.oncast.ontrack.client.ui.generalwidgets.scope;

import br.com.oncast.ontrack.client.ui.generalwidgets.ColorSelectionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableColorPackWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PaddedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTagWidgetEditMenu extends Composite implements PopupAware, HasCloseHandlers<ScopeTagWidgetEditMenu> {

	private static ScopeTagWidgetEditMenuUiBinder uiBinder = GWT.create(ScopeTagWidgetEditMenuUiBinder.class);

	interface ScopeTagWidgetEditMenuUiBinder extends UiBinder<Widget, ScopeTagWidgetEditMenu> {}

	public ScopeTagWidgetEditMenu() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	PaddedTextBox description;

	@UiField(provided = true)
	EditableColorPackWidget color;

	public ScopeTagWidgetEditMenu(final TagAssociationMetadata association) {
		color = new EditableColorPackWidget(association.getTag().getColorPack(), new ColorSelectionListener() {
			@Override
			public void onColorPackSelect(final ColorPack colorPack) {

			}
		});
		initWidget(uiBinder.createAndBindUi(this));
		description.setText(association.getTag().getDescription());
	}

	@Override
	public void show() {
		description.selectAll();
		description.setFocus(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ScopeTagWidgetEditMenu> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}
}
