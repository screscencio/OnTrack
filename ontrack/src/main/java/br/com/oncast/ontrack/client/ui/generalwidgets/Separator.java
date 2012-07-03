package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Separator extends Composite {

	private static SeparatorUiBinder uiBinder = GWT.create(SeparatorUiBinder.class);

	interface SeparatorUiBinder extends UiBinder<Widget, Separator> {}

	interface SeparatorStyle extends CssResource {
		String vertical();

		String verticalSeparator();
	}

	@UiField
	protected SimplePanel border;

	@UiField
	protected SeparatorStyle style;

	@UiField
	protected Panel rootPanel;

	public Separator() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setVertical(final String bool) {
		if (!Boolean.valueOf(bool)) return;

		rootPanel.addStyleName(style.verticalSeparator());
		addStyleNameToBorders(style.vertical());
	}

	private void addStyleNameToBorders(final String styleName) {
		border.addStyleName(styleName);
	}
}
