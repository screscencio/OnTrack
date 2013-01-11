package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import br.com.oncast.ontrack.shared.model.color.ColorPack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ColorPicker extends Composite {

	private static ColorPickerUiBinder uiBinder = GWT.create(ColorPickerUiBinder.class);

	interface ColorPickerUiBinder extends UiBinder<Widget, ColorPicker> {}

	@UiField
	HTMLPanel panel;

	public ColorPicker(final ColorSelectionListener listener) {
		initWidget(uiBinder.createAndBindUi(this));

		final ColorSelectionListener selectionListener = new ColorSelectionListener() {

			@Override
			public void onColorPackSelect(final ColorPack colorPack) {
				listener.onColorPackSelect(colorPack);
				// FIXME LOBO Hide this panel
			}
		};

		final List<ColorPack> colorPacks = ColorPack.getDefaultColorPacks();
		for (final ColorPack colorPack : colorPacks) {
			panel.add(new ColorPackWidget(colorPack, selectionListener));
		}
	}

}
