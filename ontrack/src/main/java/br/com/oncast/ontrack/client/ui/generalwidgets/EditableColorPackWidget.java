package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.shared.model.color.ColorPack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EditableColorPackWidget extends Composite implements ModelWidget<ColorPack> {

	private static EditableColorPackWidgetUiBinder uiBinder = GWT.create(EditableColorPackWidgetUiBinder.class);

	interface EditableColorPackWidgetUiBinder extends UiBinder<Widget, EditableColorPackWidget> {}

	@UiField
	Label anchor;

	private final PopupConfig colorPopUp;

	private ColorPack colorPack;

	public EditableColorPackWidget(final ColorPack colorPack, final ColorSelectionListener listener) {
		this.colorPack = colorPack;
		initWidget(uiBinder.createAndBindUi(this));

		final ColorPicker picker = new ColorPicker(new ColorSelectionListener() {
			@Override
			public void onColorPackSelect(final ColorPack selectedColorPack) {
				System.out.println(selectedColorPack.toString());
				listener.onColorPackSelect(selectedColorPack);
				EditableColorPackWidget.this.colorPack = selectedColorPack;
				update();
			}

		});

		colorPopUp = PopupConfig.configPopup()
				.popup(picker)
				.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(anchor.asWidget(), HorizontalAlignment.RIGHT, -10))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(anchor.asWidget(), VerticalAlignment.BOTTOM, -10));

		update();
	}

	@UiHandler("anchor")
	protected void onColorClick(final ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
		colorPopUp.pop();
	}

	@Override
	public boolean update() {
		anchor.getElement().getStyle().setBackgroundColor(colorPack.getBackground().toCssRepresentation());
		anchor.getElement().getStyle().setColor(colorPack.getForeground().toCssRepresentation());
		return false;
	}

	@Override
	public ColorPack getModelObject() {
		return colorPack;
	}

}
