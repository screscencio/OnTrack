package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.model.color.ColorPack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class ColorPicker extends Composite implements HasCloseHandlers<ColorPicker>, PopupAware {

	private static final int COLUMNS = 6;
	private static final int ROWS = 4;
	private static ColorPickerUiBinder uiBinder = GWT.create(ColorPickerUiBinder.class);

	interface ColorPickerUiBinder extends UiBinder<Widget, ColorPicker> {}

	@UiField(provided = true)
	Grid panel;

	public ColorPicker(final ColorSelectionListener listener) {
		panel = new Grid(ROWS, COLUMNS);
		initWidget(uiBinder.createAndBindUi(this));

		final ColorSelectionListener selectionListener = new ColorSelectionListener() {

			@Override
			public void onColorPackSelect(final ColorPack colorPack) {
				listener.onColorPackSelect(colorPack);
				hide();
			}
		};

		for (int i = 0; i < ColorPack.getDefaultColorPacks().size(); i++)
			panel.setWidget(i / COLUMNS, i % COLUMNS, new ColorPackWidget(ColorPack.getDefaultColorPacks().get(i), selectionListener));
	}

	@Override
	public void show() {
		panel.setVisible(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		panel.setVisible(false);
		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ColorPicker> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

}
