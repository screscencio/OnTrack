package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DescriptionWidget extends Composite {

	private static DescriptionWidgetUiBinder uiBinder = GWT.create(DescriptionWidgetUiBinder.class);

	interface DescriptionWidgetUiBinder extends UiBinder<Widget, DescriptionWidget> {}

	@UiField
	Label label;

	public DescriptionWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setSelected(final Scope scope) {
		label.setText(scope.getDescription());
	}

}
