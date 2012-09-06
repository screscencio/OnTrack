package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationMenuWidget extends Composite {

	private static AnnotationMenuUiBinder uiBinder = GWT.create(AnnotationMenuUiBinder.class);

	interface AnnotationMenuUiBinder extends UiBinder<Widget, AnnotationMenuWidget> {}

	@UiField
	HTMLPanel container;

	private final Set<AnnotationMenuItem> items;

	public AnnotationMenuWidget() {
		items = new HashSet<AnnotationMenuItem>();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void update() {
		for (final AnnotationMenuItem item : items) {
			item.update();
		}
	}

	public void add(final AnnotationMenuItem item) {
		if (!items.add(item)) return;

		container.add(item);
	}

	public void clear() {
		container.clear();
	}

}
