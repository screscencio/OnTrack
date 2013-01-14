package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.tag.Tag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TagWidget extends Composite {

	private static TagWidgetUiBinder uiBinder = GWT.create(TagWidgetUiBinder.class);

	interface TagWidgetUiBinder extends UiBinder<Widget, TagWidget> {}

	@UiField
	Label label;

	@UiField
	Label color;

	private Tag tag;

	private final PopupConfig colorPopUp;

	public TagWidget(final ColorSelectionListener listener) {
		initWidget(uiBinder.createAndBindUi(this));

		final ColorPicker picker = new ColorPicker(new ColorSelectionListener() {

			@Override
			public void onColorPackSelect(final ColorPack colorPack) {
				listener.onColorPackSelect(colorPack);
				update();
			}
		});

		colorPopUp = PopupConfig.configPopup()
				.popup(picker)
				.alignHorizontal(HorizontalAlignment.LEFT, new AlignmentReference(color.asWidget(), HorizontalAlignment.RIGHT, -10))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(color.asWidget(), VerticalAlignment.BOTTOM, -10));
	}

	public TagWidget(final Tag tag, final ColorSelectionListener listener) {
		this(listener);
		this.tag = tag;
		update();

	}

	private void update() {
		label.setText(tag.getDescription());
		color.getElement().getStyle().setBackgroundColor(tag.getColorPack().getBackground().toCssRepresentation());
		color.getElement().getStyle().setColor(tag.getColorPack().getForeground().toCssRepresentation());
	}

	@UiHandler("color")
	protected void onColorClick(final ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
		colorPopUp.pop();
	}
}
