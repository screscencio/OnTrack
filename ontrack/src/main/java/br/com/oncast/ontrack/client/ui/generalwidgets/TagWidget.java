package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.shared.model.tag.Tag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TagWidget extends Composite {

	private static TagWidgetUiBinder uiBinder = GWT.create(TagWidgetUiBinder.class);

	interface TagWidgetUiBinder extends UiBinder<Widget, TagWidget> {}

	@UiField
	Label label;

	@UiField(provided = true)
	EditableColorPackWidget color;

	private final Tag tag;

	public TagWidget(final Tag tag, final ColorSelectionListener listener) {
		this.tag = tag;
		this.color = new EditableColorPackWidget(tag.getColorPack(), listener);
		initWidget(uiBinder.createAndBindUi(this));
		update();

	}

	private void update() {
		label.setText(tag.getDescription());
		color.update();
	}

}
