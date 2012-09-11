package br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationMenuItemSeparator extends Composite {

	private static AnnotationMenuItemSeparatorUiBinder uiBinder = GWT.create(AnnotationMenuItemSeparatorUiBinder.class);

	interface AnnotationMenuItemSeparatorUiBinder extends UiBinder<Widget, AnnotationMenuItemSeparator> {}

	public AnnotationMenuItemSeparator() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
