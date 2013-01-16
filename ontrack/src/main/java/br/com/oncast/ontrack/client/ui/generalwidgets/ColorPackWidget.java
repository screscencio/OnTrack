package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.shared.model.color.ColorPack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ColorPackWidget extends Composite {

	private static ColorPackWidgetUiBinder uiBinder = GWT.create(ColorPackWidgetUiBinder.class);

	interface ColorPackWidgetUiBinder extends UiBinder<Widget, ColorPackWidget> {}

	@UiField
	Label label;

	private final ColorSelectionListener listener;
	private final ColorPack colorPack;

	public ColorPackWidget(final ColorPack colorPack, final ColorSelectionListener listener) {
		this.listener = listener;
		this.colorPack = colorPack;
		initWidget(uiBinder.createAndBindUi(this));
		label.getElement().getStyle().setBackgroundColor(colorPack.getBackground().toCssRepresentation());
		label.getElement().getStyle().setColor(colorPack.getForeground().toCssRepresentation());
	}

	@UiHandler("label")
	protected void onClick(final ClickEvent event) {
		listener.onColorPackSelect(colorPack);
	}
}
