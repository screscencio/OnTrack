package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.shared.model.tag.Tag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TagMenuItem extends Composite {

	private static TagMenuItemUiBinder uiBinder = GWT.create(TagMenuItemUiBinder.class);

	interface TagMenuItemUiBinder extends UiBinder<Widget, TagMenuItem> {}

	@UiField
	Label label;

	@UiField(provided = true)
	EditableColorPackWidget color;

	private final Tag tag;

	public TagMenuItem(final Tag tag, final ColorSelectionListener listener) {
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
