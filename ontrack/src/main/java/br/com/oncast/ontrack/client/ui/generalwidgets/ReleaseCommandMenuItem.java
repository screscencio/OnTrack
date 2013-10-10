package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseCommandMenuItem extends Composite {

	private static final String PATH_SEPARATOR = "/";

	private static ReleaseCommandMenuItemUiBinder uiBinder = GWT.create(ReleaseCommandMenuItemUiBinder.class);

	interface ReleaseCommandMenuItemUiBinder extends UiBinder<Widget, ReleaseCommandMenuItem> {}

	interface ReleaseCommandMenuItemStyle extends CssResource {
		String singleName();
	}

	@UiField
	ReleaseCommandMenuItemStyle style;

	@UiField
	SpanElement path;

	@UiField
	DivElement name;

	public ReleaseCommandMenuItem(final String fullName) {
		initWidget(uiBinder.createAndBindUi(this));
		int lastSeparatorIndex = -1;
		int index = 0;
		while ((index = fullName.indexOf(PATH_SEPARATOR, lastSeparatorIndex + 1)) > -1) {
			lastSeparatorIndex = index;
		}
		if (lastSeparatorIndex == -1) {
			name.setInnerText(fullName);
			name.setClassName(style.singleName());
			return;
		}
		path.setInnerText(fullName.substring(0, lastSeparatorIndex).replaceAll(PATH_SEPARATOR, " / "));
		name.setInnerHTML(fullName.substring(lastSeparatorIndex + PATH_SEPARATOR.length()));
	}

}
