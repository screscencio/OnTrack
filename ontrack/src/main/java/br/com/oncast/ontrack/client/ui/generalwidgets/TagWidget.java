package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.shared.model.tag.Tag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
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

	public TagWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public TagWidget(final Tag tag) {
		this();
		label.setText(tag.getDescription());
	}

	@UiHandler("color")
	protected void onColorClick(final ClickEvent event) {
		Window.alert("HA!");
		event.preventDefault();
		event.stopPropagation();
	}
}
